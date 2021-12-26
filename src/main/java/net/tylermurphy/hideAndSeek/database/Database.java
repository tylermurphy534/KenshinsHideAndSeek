package net.tylermurphy.hideAndSeek.database;

import com.google.common.io.ByteStreams;
import net.tylermurphy.hideAndSeek.Main;

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

    private static final File databaseFile = new File(Main.data, "database.db");

    public static PlayerInfoTable playerInfo;

    protected static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:"+databaseFile;
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    protected static InputStream convertUniqueId(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return new ByteArrayInputStream(bytes);
    }

    protected static UUID convertBinaryStream(InputStream stream) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            buffer.put(ByteStreams.toByteArray(stream));
            buffer.flip();
            return new UUID(buffer.getLong(), buffer.getLong());
        } catch (IOException ignored) {}
        return null;
    }

    public static void init(){
        playerInfo = new PlayerInfoTable();
    }
}
