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

package net.tylermurphy.hideAndSeek.database.connections;

import net.tylermurphy.hideAndSeek.Main;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection implements DatabaseConnection {

    private final File databaseFile;
    private final SQLiteConfig config;

    public SQLiteConnection(){

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Main.getInstance().getLogger().severe(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        databaseFile = new File(Main.getInstance().getDataFolder(), "database.db");

        config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);
    }

    @Override
    public Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:"+databaseFile;
            conn = DriverManager.getConnection(url, config.toProperties());
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

}
