/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2022 Tyler Murphy.
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
import net.tylermurphy.hideAndSeek.database.Database;
import net.tylermurphy.hideAndSeek.database.util.LegacyPlayerInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LegacyTable {

    private final Database database;
    private final boolean exists;

    protected LegacyTable(Database database) {

        String sql = "SELECT * FROM player_info LIMIT 1;";

        boolean check;
        try(Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            check = resultSet.next();
        } catch (SQLException e) {
            check = false;
        }

        this.exists = check;
        this.database = database;
    }

    public boolean exists(){
        return exists;
    }

    public boolean copyData(){
        String sql = "SELECT * FROM player_info;";
        List<LegacyPlayerInfo> legacyPlayerInfoList = new ArrayList<>();
        try(Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                legacyPlayerInfoList.add(new LegacyPlayerInfo(
                    resultSet.getBytes("uuid"),
                    resultSet.getInt("wins"),
                    resultSet.getInt("hider_wins"),
                    resultSet.getInt("seeker_wins"),
                    resultSet.getInt("games_played")
                ));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        for(LegacyPlayerInfo legacyInfo : legacyPlayerInfoList){
            database.getGameData().updateInfo(
                legacyInfo.getUniqueId(),
                legacyInfo.getHiderWins(),
                legacyInfo.getSeekerWins(),
                legacyInfo.getGamesPlayer() - legacyInfo.getSeekerWins(),
                legacyInfo.getSeekerWins(),
                0,
                0,
                0,
                0
            );
        }
        return true;
    }

    public boolean drop(){
        String sql = "DROP table player_info";
        try(Connection connection = database.connect(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
