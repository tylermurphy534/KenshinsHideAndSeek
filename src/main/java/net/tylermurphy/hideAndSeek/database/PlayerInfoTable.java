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

import com.google.common.io.ByteStreams;
import net.tylermurphy.hideAndSeek.Main;
import net.tylermurphy.hideAndSeek.configuration.Config;
import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.util.WinType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.*;

public class PlayerInfoTable {

    private static final Map<UUID, PlayerInfo> CACHE = new HashMap<>();

    protected PlayerInfoTable(){

        String sql = "CREATE TABLE IF NOT EXISTS hs_data (\n"
                + "	uuid BINARY(16) PRIMARY KEY,\n"
                + "	hider_wins int NOT NULL,\n"
                + "	seeker_wins int NOT NULL,\n"
                + "	hider_games int NOT NULL,\n"
                + "	seeker_games int NOT NULL,\n"
                + "	hider_kills int NOT NULL,\n"
                + "	seeker_kills int NOT NULL,\n"
                + "	hider_deaths int NOT NULL,\n"
                + "	seeker_deaths int NOT NULL\n"
                + ");";

        try(Connection connection = Database.connect(); Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private byte[] encodeUUID(UUID uuid){
        try {
            byte[] bytes = new byte[16];
            ByteBuffer.wrap(bytes)
                    .putLong(uuid.getMostSignificantBits())
                    .putLong(uuid.getLeastSignificantBits());
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] result = new byte[is.available()];
            if (is.read(result) == -1) {
                Main.plugin.getLogger().severe("IO Error: Failed to read bytes from input stream");
                return new byte[0];
            }
            return result;
        } catch (IOException e){
            Main.plugin.getLogger().severe("IO Error: " + e.getMessage());
            return new byte[0];
        }
    }

    private UUID decodeUUID(byte[] bytes){
        InputStream is = new ByteArrayInputStream(bytes);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            buffer.put(ByteStreams.toByteArray(is));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (IOException e) {
            Main.plugin.getLogger().severe("IO Error: " + e.getMessage());
        }
        return null;
    }

    @NotNull
    public PlayerInfo getInfo(UUID uuid){
        String sql = "SELECT * FROM hs_data WHERE uuid = ?;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setBytes(1, encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if(rs.next()){
                PlayerInfo info = new PlayerInfo(
                        uuid,
                        rs.getInt("hider_wins"),
                        rs.getInt("seeker_wins"),
                        rs.getInt("hider_games"),
                        rs.getInt("seeker_games"),
                        rs.getInt("hider_kills"),
                        rs.getInt("seeker_kills"),
                        rs.getInt("hider_deaths"),
                        rs.getInt("seeker_deaths")
                );
                rs.close();
                connection.close();
                CACHE.put(uuid, info);
                return info;
            }
            rs.close();
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return new PlayerInfo(uuid, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Nullable
    public PlayerInfo getInfoRanking(String order, int place){
        String sql = "SELECT * FROM hs_data ORDER BY "+order+" DESC LIMIT 1 OFFSET ?;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, place-1);
            ResultSet rs  = statement.executeQuery();
            if(rs.next()){
                UUID uuid = decodeUUID(rs.getBytes("uuid"));
                PlayerInfo info = new PlayerInfo(
                        uuid,
                        rs.getInt("hider_wins"),
                        rs.getInt("seeker_wins"),
                        rs.getInt("hider_games"),
                        rs.getInt("seeker_games"),
                        rs.getInt("hider_kills"),
                        rs.getInt("seeker_kills"),
                        rs.getInt("hider_deaths"),
                        rs.getInt("seeker_deaths")
                );
                rs.close();
                connection.close();
                CACHE.put(uuid, info);
                return info;
            }
            rs.close();
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public List<PlayerInfo> getInfoPage(int page){
        String sql = "SELECT * FROM hs_data ORDER BY (hider_wins + seeker_wins) DESC LIMIT 10 OFFSET ?;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, (page-1)*10);
            ResultSet rs  = statement.executeQuery();
            List<PlayerInfo> infoList = new ArrayList<>();
            while(rs.next()){
                PlayerInfo info = new PlayerInfo(
                        decodeUUID(rs.getBytes("uuid")),
                        rs.getInt("hider_wins"),
                        rs.getInt("seeker_wins"),
                        rs.getInt("hider_games"),
                        rs.getInt("seeker_games"),
                        rs.getInt("hider_kills"),
                        rs.getInt("seeker_kills"),
                        rs.getInt("hider_deaths"),
                        rs.getInt("seeker_deaths")
                );
                infoList.add(info);
            }
            rs.close();
            connection.close();
            return infoList;
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Integer getRanking(String order, UUID uuid){
        String sql = "SELECT count(*) AS total FROM hs_data WHERE "+order+" >= (SELECT "+order+" FROM hs_data WHERE uuid = ?) AND "+order+" > 0;";
        try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setBytes(1, encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if(rs.next()){
                return rs.getInt("total");
            }
            rs.close();
        } catch (SQLException e){
            Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void addWins(List<UUID> uuids, List<UUID> winners, Map<String,Integer> hider_kills, Map<String,Integer> hider_deaths, Map<String,Integer> seeker_kills, Map<String,Integer> seeker_deaths, WinType type){
        for(UUID uuid : uuids){
            String sql = "INSERT OR REPLACE INTO hs_data (uuid, hider_wins, seeker_wins, hider_games, seeker_games, hider_kills, seeker_kills, hider_deaths, seeker_deaths) VALUES (?,?,?,?,?,?,?,?,?)";
            PlayerInfo info = getInfo(uuid);
            try(Connection connection = Database.connect(); PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setBytes(1, encodeUUID(uuid));
                statement.setInt(2, info.hider_wins + (winners.contains(uuid) && type == WinType.HIDER_WIN ? 1 : 0));
                statement.setInt(3, info.seeker_wins + (winners.contains(uuid) && type == WinType.SEEKER_WIN ? 1 : 0));
                statement.setInt(4, info.hider_games + (Board.isHider(uuid) ? 1 : 0));
                statement.setInt(5, info.seeker_games + (Board.isSeeker(uuid) ? 1 : 0));
                statement.setInt(6, info.hider_kills + (Board.isHider(uuid) ? hider_kills.getOrDefault(uuid.toString(), 0) : 0));
                statement.setInt(7, info.seeker_kills + (Board.isSeeker(uuid) ? seeker_kills.getOrDefault(uuid.toString(), 0) : 0));
                statement.setInt(8, info.hider_deaths + (Board.isHider(uuid) ? hider_deaths.getOrDefault(uuid.toString(), 0) : 0));
                statement.setInt(9, info.seeker_deaths + (Board.isSeeker(uuid) ? seeker_deaths.getOrDefault(uuid.toString(), 0) : 0));
                statement.execute();
            } catch (SQLException e){
                Main.plugin.getLogger().severe("SQL Error: " + e.getMessage());
                e.printStackTrace();
                return;
            } finally {
                CACHE.remove(uuid);
            }
        }
    }

}
