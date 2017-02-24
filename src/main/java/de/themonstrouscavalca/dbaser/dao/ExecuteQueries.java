package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IExecuteQueries;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ExecuteQueries<T extends IExportToMap> implements IExecuteQueries<T>{
    private final IProvideConnection connectionProvider;
    private Connection connection;
    private PreparedStatement statement;

    public ExecuteQueries(IProvideConnection connectionProvider) throws SQLException{
        this.connectionProvider = connectionProvider;
        this.connection = this.connectionProvider.getConnection();
    }


    @Override
    public ResultSetOptional executeUpdate(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException{
        ResultSetOptional rsOptional = new ResultSetOptional();

        QueryBuilder builder = QueryBuilder.fromString(sql);
        try{
            this.statement = builder.prepare(this.connection, replacementParameters);
            builder.parameterize(this.statement, replacementParameters);
            int executed = this.statement.executeUpdate();
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            rsOptional.setException(e);
        }

        return rsOptional;
    }

    @Override
    public ResultSetOptional executeUpdate(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException{
        return this.executeUpdate(sql, entity.exportToMap());
    }

    @Override
    public ResultSetOptional executeQuery(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException{
        ResultSetOptional rsOptional = new ResultSetOptional();
        QueryBuilder builder = QueryBuilder.fromString(sql);
        try{
            this.statement = builder.prepare(connection, replacementParameters);
            builder.parameterize(this.statement, replacementParameters);
            ResultSet rs = this.statement.executeQuery();
            rsOptional.setResultSet(rs);
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            rsOptional.setException(e);
            throw e;
        }

        return rsOptional;
    }

    @Override
    public ResultSetOptional executeQuery(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException{
        return this.executeQuery(sql, entity.exportToMap());
    }

    @Override
    public ResultSetOptional execute(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException{
        ResultSetOptional rsOptional = new ResultSetOptional();
        QueryBuilder builder = QueryBuilder.fromString(sql);
        try{
            this.statement = builder.prepare(connection, replacementParameters);
            builder.parameterize(this.statement, replacementParameters);
            boolean executed = this.statement.execute();
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            rsOptional.setException(e);
            throw e;
        }

        return rsOptional;
    }

    @Override
    public ResultSetOptional execute(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException{
        return this.execute(sql, entity.exportToMap());
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
    }
}
