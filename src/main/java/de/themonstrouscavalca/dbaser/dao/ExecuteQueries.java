package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IExecuteQueries;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;


public class ExecuteQueries implements IExecuteQueries{
    private Connection connection;
    private PreparedStatement statement;
    private ResultSetOptional resultSetOptional;

    public ExecuteQueries(IProvideConnection connectionProvider) throws SQLException{
        this.connection = connectionProvider.getConnection();
    }

    @Override
    public ResultSetOptional executeUpdate(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        return this.executeUpdate(QueryBuilder.fromString(sql), replacementParameters);
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeUpdate(String sql, T entity) throws QueryBuilderException, SQLException{
        return this.executeUpdate(sql, entity.exportToMap());
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeUpdate(QueryBuilder query, T entity) throws QueryBuilderException, SQLException{
        return this.executeUpdate(query, entity.exportToMap());
    }

    @Override
    public ResultSetOptional executeUpdate(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        this.resultSetOptional = new ResultSetOptional();

        try{
            this.statement = query.fullPrepare(this.connection, replacementParameters);
            this.resultSetOptional.setExecuted(this.statement.executeUpdate());
        }catch(SQLException | QueryBuilderException e){
            this.resultSetOptional.setException(e);
        }

        return this.resultSetOptional;
    }

    @Override
    public ResultSetOptional executeQuery(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        return this.executeQuery(QueryBuilder.fromString(sql), replacementParameters);
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeQuery(String sql, T entity) throws QueryBuilderException, SQLException{
        return this.executeQuery(sql, entity.exportToMap());
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeQuery(QueryBuilder query, T entity) throws QueryBuilderException, SQLException{
        return this.executeQuery(query, entity.exportToMap());
    }

    @Override
    public ResultSetOptional executeQuery(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        this.resultSetOptional = new ResultSetOptional();
        try{
            this.statement = query.fullPrepare(connection, replacementParameters);
            this.resultSetOptional.setResultSet(this.statement.executeQuery());
        }catch(SQLException | QueryBuilderException e){
            this.resultSetOptional.setException(e);
            throw e;
        }

        return this.resultSetOptional;
    }

    @Override
    public ResultSetOptional execute(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        return this.execute(QueryBuilder.fromString(sql), replacementParameters);
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional execute(String sql, T entity) throws QueryBuilderException, SQLException{
        return this.execute(sql, entity.exportToMap());
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional execute(QueryBuilder query, T entity) throws QueryBuilderException, SQLException{
        return this.execute(query, entity.exportToMap());
    }

    @Override
    public ResultSetOptional execute(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException{
        this.resultSetOptional = new ResultSetOptional();
        try{
            this.statement = query.fullPrepare(connection, replacementParameters);
            this.resultSetOptional.setExecuted(this.statement.execute() ? 1 : 0);
        }catch(SQLException | QueryBuilderException e){
            this.resultSetOptional.setException(e);
            throw e;
        }

        return this.resultSetOptional;
    }

    @Override
    public void close(){
        if(this.connection != null){
            try{
                this.connection.close();
            }catch(SQLException e){
                //Already gone
            }
        }

        if(this.statement != null){
            try{
                this.statement.close();
            }catch(SQLException e){
                //Already gone
            }
        }

        if(this.resultSetOptional != null){
            this.resultSetOptional.close();
        }
    }
}
