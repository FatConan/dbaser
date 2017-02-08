package de.themonstrouscavalca.database;

import de.themonstrouscavalca.database.dao.interfaces.IProvideConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase implements IProvideConnection{
    private static final String url = "jdbc:sqlite:test.db";

    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

    public void killDatabase() {
        File f = new File("test.db");
        f.delete();
    }
}
