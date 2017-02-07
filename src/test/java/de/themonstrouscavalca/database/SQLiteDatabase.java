package de.themonstrouscavalca.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase{
    private static final String url = "jdbc:sqlite:test.db";

    public static Connection getDatabaseConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

    public static void killDatabase() {
        File f = new File("test.db");
        f.delete();
    }
}
