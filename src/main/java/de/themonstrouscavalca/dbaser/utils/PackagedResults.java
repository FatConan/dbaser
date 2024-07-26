package de.themonstrouscavalca.dbaser.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PackagedResults implements AutoCloseable{
    private final Connection connection;
    private final PreparedStatement ps;
    private final ResultSet rs;

    public PackagedResults(Connection connection, PreparedStatement ps, ResultSet rs){
        this.connection = connection;
        this.ps = ps;
        this.rs = rs;
    }

    public Connection getConnection(){
        return this.connection;
    }
    public PreparedStatement getPreparedStatement(){
        return this.ps;
    }
    public ResultSet getResultSet(){
        return this.rs;
    }
    public ResultSetTableAware getResultSetTableAware(){
        return new ResultSetTableAware(this.rs);
    }

    @Override
    public void close() throws Exception{
        this.rs.close();
        this.ps.close();
        this.connection.close();
    }
}
