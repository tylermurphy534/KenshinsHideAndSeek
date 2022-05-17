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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection implements DatabaseConnection {

    private final HikariConfig config;
    private final HikariDataSource ds;

    public MySQLConnection(){

        String host = "to be implemented";
        String port = "to be implemented";
        String user = "to be implemented";
        String pass = "to be implemented";

        config = new HikariConfig();

        config.setJdbcUrl("jdbc:mariadb://"+host+":"+port+"/kenbot");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("user", user);
        config.addDataSourceProperty("password",pass);
        config.addDataSourceProperty("autoCommit", "true");
        config.setAutoCommit(true);
        config.setMaximumPoolSize(20);

        ds = new HikariDataSource(config);

    }

    @Override
    public Connection connect() throws SQLException {
        return ds.getConnection();
    }

}
