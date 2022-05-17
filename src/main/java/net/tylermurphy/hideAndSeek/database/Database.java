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
import net.tylermurphy.hideAndSeek.database.connections.DatabaseConnection;
import net.tylermurphy.hideAndSeek.database.connections.SQLiteConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private final GameDataTable playerInfo;
    private final NameDataTable nameInfo;
    private final DatabaseConnection connection;

    public Database(){

        connection = new SQLiteConnection();

        playerInfo = new GameDataTable(this);

        nameInfo = new NameDataTable(this);

        LegacyTable legacyTable = new LegacyTable(this);
        if(legacyTable.exists()){
            if(legacyTable.copyData()){
                if(!legacyTable.drop()){
                    Main.getInstance().getLogger().severe("Failed to drop old legacy table: player_info. Some data may be duplicated!");
                }
            }
        }
    }

    public GameDataTable getGameData(){
        return playerInfo;
    }

    public NameDataTable getNameData() { return nameInfo; }

    protected Connection connect() {
        Connection conn = null;
        try {
            conn = connection.connect();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe(e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    protected byte[] encodeUUID(UUID uuid) {
        try {
            byte[] bytes = new byte[16];
            ByteBuffer.wrap(bytes)
                    .putLong(uuid.getMostSignificantBits())
                    .putLong(uuid.getLeastSignificantBits());
            InputStream is = new ByteArrayInputStream(bytes);
            byte[] result = new byte[is.available()];
            if (is.read(result) == -1) {
                Main.getInstance().getLogger().severe("IO Error: Failed to read bytes from input stream");
                return new byte[0];
            }
            return result;
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("IO Error: " + e.getMessage());
            return new byte[0];
        }
    }

    protected UUID decodeUUID(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            buffer.put(ByteStreams.toByteArray(is));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (IOException e) {
            Main.getInstance().getLogger().severe("IO Error: " + e.getMessage());
        }
        return null;
    }

}
