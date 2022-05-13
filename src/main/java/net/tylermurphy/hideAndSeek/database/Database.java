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
import org.sqlite.SQLiteConfig;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private static final File databaseFile = new File(Main.getInstance().getDataFolder(), "database.db");

    private PlayerInfoTable playerInfo;
    private final SQLiteConfig config;

    protected Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:"+databaseFile;
            conn = DriverManager.getConnection(url, config.toProperties());
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe(e.getMessage());
        }
        return conn;
    }

    protected InputStream convertUniqueId(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }

    protected UUID convertBinaryStream(InputStream stream) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            buffer.put(ByteStreams.toByteArray(stream));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (IOException ignored) {}
        return null;
    }

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Main.getInstance().getLogger().severe(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        config = new SQLiteConfig();
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);

        playerInfo = new PlayerInfoTable();
    }

    public PlayerInfoTable getPlayerInfo() {
        return playerInfo;
    }
}
