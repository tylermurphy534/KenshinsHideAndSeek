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
import net.tylermurphy.hideAndSeek.game.Board;
import net.tylermurphy.hideAndSeek.game.util.WinType;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

public class PlayerInfoTable {

    private final Map<UUID, PlayerInfo> CACHE = new HashMap<>();
    private final Database database;

    protected PlayerInfoTable(Database database) {

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

        try(Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        this.database = database;
    }

    @Nullable
    public PlayerInfo getInfo(UUID uuid) {
        if(CACHE.containsKey(uuid)) return CACHE.get(uuid);
        String sql = "SELECT * FROM hs_data WHERE uuid = ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
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
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public PlayerInfo getInfoRanking(String order, int place) {
        String sql = "SELECT * FROM hs_data ORDER BY "+order+" DESC LIMIT 1 OFFSET ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, place-1);
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
                UUID uuid = database.decodeUUID(rs.getBytes("uuid"));
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
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public List<PlayerInfo> getInfoPage(int page) {
        String sql = "SELECT * FROM hs_data ORDER BY (hider_wins + seeker_wins) DESC LIMIT 10 OFFSET ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, (page-1)*10);
            ResultSet rs  = statement.executeQuery();
            List<PlayerInfo> infoList = new ArrayList<>();
            while(rs.next()) {
                PlayerInfo info = new PlayerInfo(
                        database.decodeUUID(rs.getBytes("uuid")),
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
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public Integer getRanking(String order, UUID uuid) {
        String sql = "SELECT count(*) AS total FROM hs_data WHERE "+order+" >= (SELECT "+order+" FROM hs_data WHERE uuid = ?) AND "+order+" > 0;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            rs.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void addWins(Board board, List<UUID> uuids, List<UUID> winners, Map<String,Integer> hider_kills, Map<String,Integer> hider_deaths, Map<String,Integer> seeker_kills, Map<String,Integer> seeker_deaths, WinType type) {
        for(UUID uuid : uuids) {
            String sql = "INSERT OR REPLACE INTO hs_data (uuid, hider_wins, seeker_wins, hider_games, seeker_games, hider_kills, seeker_kills, hider_deaths, seeker_deaths) VALUES (?,?,?,?,?,?,?,?,?)";
            PlayerInfo info = getInfo(uuid);
            if(info == null){
                info = new PlayerInfo(uuid, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBytes(1, database.encodeUUID(uuid));
                statement.setInt(2, info.hider_wins + (winners.contains(uuid) && type == WinType.HIDER_WIN ? 1 : 0));
                statement.setInt(3, info.seeker_wins + (winners.contains(uuid) && type == WinType.SEEKER_WIN ? 1 : 0));
                statement.setInt(4, info.hider_games + (board.isHider(uuid) || (board.isSeeker(uuid) && !board.getFirstSeeker().getUniqueId().equals(uuid)) ? 1 : 0));
                statement.setInt(5, info.seeker_games + (board.getFirstSeeker().getUniqueId().equals(uuid) ? 1 : 0));
                statement.setInt(6, info.hider_kills + hider_kills.getOrDefault(uuid.toString(), 0));
                statement.setInt(7, info.seeker_kills + seeker_kills.getOrDefault(uuid.toString(), 0));
                statement.setInt(8, info.hider_deaths + hider_deaths.getOrDefault(uuid.toString(), 0));
                statement.setInt(9, info.seeker_deaths + seeker_deaths.getOrDefault(uuid.toString(), 0));
                statement.execute();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
                e.printStackTrace();
                return;
            } finally {
                CACHE.remove(uuid);
            }
        }
    }

}
