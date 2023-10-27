package de.themonstrouscavalca.dbaser;

import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase implements IProvideConnection{
    private static final String url = "jdbc:sqlite:test.db";
    private boolean restoreAutoCommitState;
    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException{
        this.connection = DriverManager.getConnection(url);
        this.restoreAutoCommitState = this.connection.getAutoCommit();
        return this.connection;
    }

    @Override
    public Connection getTransactionalConnection() throws SQLException {
        this.connection = DriverManager.getConnection(url);
        this.restoreAutoCommitState = connection.getAutoCommit();
        this.connection.setAutoCommit(false);
        return this.connection;
    }

    @Override
    public void commitAndRestore(Connection connection) throws SQLException {
        connection.commit();
        connection.setAutoCommit(this.restoreAutoCommitState);
    }

    @Override
    public void rollbackAndRestore(Connection connection) throws SQLException {
        connection.rollback();
        connection.setAutoCommit(this.restoreAutoCommitState);
    }

    public static void killDatabase() {
        try {
            Path dbasePath = Paths.get("test.db");
            if(Files.exists(dbasePath)){
                Files.delete(dbasePath);
            }
        } catch (IOException e) {
           System.out.println(e);
        }
    }

    public void close(){
        if(this.connection != null){
            try {
                this.connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
