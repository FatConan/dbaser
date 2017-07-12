package de.themonstrouscavalca.dbaser;

import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;

import java.io.File;
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
    public void commitAndRestore() throws SQLException {
        this.connection.commit();
        this.connection.setAutoCommit(this.restoreAutoCommitState);
    }

    @Override
    public void rollbackAndRestore() throws SQLException {
        this.connection.rollback();
        this.connection.setAutoCommit(this.restoreAutoCommitState);
    }

    public void killDatabase() {
        File f = new File("test.db");
        f.delete();
    }
}
