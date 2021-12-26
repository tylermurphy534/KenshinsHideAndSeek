package net.tylermurphy.hideAndSeek.database;

import net.tylermurphy.hideAndSeek.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final File databaseFile = new File(Main.data, "database.db");

    public PlayerInfoTable playerInfo;

    protected Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:"+databaseFile;
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void init(){
        playerInfo = new PlayerInfoTable();
    }
}
