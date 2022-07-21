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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.management.BufferPoolMXBean;
import java.sql.*;
import java.util.UUID;

public class NameDataTable {

    private final Database database;

    protected NameDataTable(Database database) {

        String sql = "CREATE TABLE IF NOT EXISTS hs_names (\n"
                + "	uuid BINARY(16) NOT NULL,\n"
                + "	name VARCHAR(48) NOT NULL,\n"
                + "	PRIMARY KEY (uuid,name)\n"
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
    public String getName(@NotNull UUID uuid) {
        String sql = "SELECT * FROM hs_names WHERE uuid = ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            rs.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        OfflinePlayer retry = Bukkit.getOfflinePlayer(uuid);
        if(retry != null){
            this.update(uuid, retry.getName());
            return retry.getName();
        }
        return null;
    }

    @Nullable
    public UUID getUUID(@NotNull String name) {
        String sql = "SELECT * FROM hs_names WHERE name = ?;";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs  = statement.executeQuery();
            if (rs.next()) {
                return database.decodeUUID(rs.getBytes("uuid"));
            }
            rs.close();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        OfflinePlayer retry = Bukkit.getOfflinePlayer(name);
        if(retry != null){
            this.update(retry.getUniqueId(), name);
            return retry.getUniqueId();
        }
        return null;
    }

    public boolean update(@NotNull UUID uuid, @NotNull String name){
        String sql = "INSERT OR REPLACE INTO hs_names (uuid, name) VALUES (?,?)";
        try(Connection connection = database.connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBytes(1, database.encodeUUID(uuid));
            statement.setString(2, name);
            statement.execute();
            statement.close();
            return true;
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("SQL Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
