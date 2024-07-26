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
    protected static final String SQLITE_URL_FORMAT = "jdbc:sqlite:%s";
    private static final String DB_FILE = "test.db";
    private boolean restoreAutoCommitState;
    private Connection connection;

    public static void killDatabase(SQLiteDatabase db){
        try{
            Path dbasePath = Paths.get(db.getDBFile());
            if(Files.exists(dbasePath)){
                Files.delete(dbasePath);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    protected String getUrl(){
        return String.format(SQLITE_URL_FORMAT, DB_FILE);
    }

    protected String getDBFile(){
        return DB_FILE;
    }

    protected Connection getConnection(String url, boolean transactional) throws SQLException{
        if(this.connection == null || this.connection.isClosed()){
            this.connection = DriverManager.getConnection(url);
        }
        this.restoreAutoCommitState = this.connection.getAutoCommit();
        if(transactional){
            this.connection.setAutoCommit(false);
        }
        return this.connection;
    }

    @Override
    public Connection getConnection() throws SQLException{
        return this.getConnection(this.getUrl(), false);
    }

    @Override
    public Connection getTransactionalConnection() throws SQLException{
        return this.getConnection(this.getUrl(), true);
    }

    @Override
    public void commitAndRestore(Connection connection) throws SQLException{
        connection.commit();
        connection.setAutoCommit(this.restoreAutoCommitState);
    }

    @Override
    public void rollbackAndRestore(Connection connection) throws SQLException{
        connection.rollback();
        connection.setAutoCommit(this.restoreAutoCommitState);
    }

    public void close(){
        if(this.connection != null){
            try{
                this.connection.close();
            }catch(SQLException e){
                throw new RuntimeException(e);
            }finally{
                this.connection = null;
            }
        }
    }
}
