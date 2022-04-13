/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.database;

import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.util.WinType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInfoTable {

    protected PlayerInfoTable(){

        String sql = "CREATE TABLE IF NOT EXISTS player_info (\n"
                + "	uuid BINARY(16) PRIMARY KEY,\n"
                + "	wins int NOT NULL,\n"
                + "	seeker_wins int NOT NULL,\n"
                + "	hider_wins int NOT NULL,\n"
                + "	games_played int NOT NULL\n"
                + ");";

        try(Connection connection = Database.connect(); Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
        }
    }

    public PlayerInfo getInfo(UUID uuid){
        String sql = "SELECT * FROM player_info WHERE uuid = ?;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            InputStream is = Database.convertUniqueId(uuid);
            byte[] bytes = new byte[is.available()];
            if(is.read(bytes) == -1){
                throw new IOException("Failed to read bytes from input stream");
            }
            statement.setBytes(1, bytes);
            ResultSet rs  = statement.executeQuery();
            if(rs.next()){
                rs.close();
                connection.close();
                return new PlayerInfo(
                        uuid,
                        rs.getInt("wins"),
                        rs.getInt("seeker_wins"),
                        rs.getInt("hider_wins"),
                        rs.getInt("games_played")
                );
            }
            rs.close();
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
        } catch (IOException e) {
            Main.plugin.getLogger().severe("IO Error: " + e.getMessage());
            e.printStackTrace();
        }
        return new PlayerInfo(uuid, 0, 0, 0, 0);
    }

    public List<PlayerInfo> getInfoPage(int page){
        String sql = "SELECT * FROM player_info ORDER BY wins DESC LIMIT 10 OFFSET ?;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, (page-1)*10);
            ResultSet rs  = statement.executeQuery();
            List<PlayerInfo> infoList = new ArrayList<>();
            while(rs.next()){
                PlayerInfo info = new PlayerInfo(
                        Database.convertBinaryStream(new ByteArrayInputStream(rs.getBytes("uuid"))),
                        rs.getInt("wins"),
                        rs.getInt("seeker_wins"),
                        rs.getInt("hider_wins"),
                        rs.getInt("games_played")
                );
                infoList.add(info);
            }
            rs.close();
            connection.close();
            return infoList;
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
        }
        return null;
    }

    public void addWins(List<UUID> uuids, List<UUID> winners, WinType type){
        for(UUID uuid : uuids){
            String sql = "INSERT OR REPLACE INTO player_info (uuid, wins, seeker_wins, hider_wins, games_played) VALUES (?,?,?,?,?)";
            PlayerInfo info = getInfo(uuid);
            try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
                InputStream is = Database.convertUniqueId(uuid);
                byte[] bytes = new byte[is.available()];
                if(is.read(bytes) == -1){
                    throw new IOException("Failed to read bytes from input stream");
                }
                statement.setBytes(1, bytes);
                statement.setInt(2, info.wins + (winners.contains(uuid) ? 1 : 0));
                statement.setInt(3, info.seeker_wins + (winners.contains(uuid) && type == WinType.SEEKER_WIN ? 1 : 0));
                statement.setInt(4, info.hider_wins + (winners.contains(uuid) && type == WinType.HIDER_WIN ? 1 : 0));
                statement.setInt(5, info.games_played + 1);
                statement.execute();
            } catch (SQLException e){
                Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
                e.printStackTrace();
                return;
            } catch (IOException e) {
                Main.plugin.getLogger().severe("IO Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
