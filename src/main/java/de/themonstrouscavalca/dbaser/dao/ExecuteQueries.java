package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IExecuteQueries;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.queries.CollectedParameterMaps;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class ExecuteQueries implements IExecuteQueries{
    private Connection connection;
    private boolean reuseConnection;
    private PreparedStatement statement;
    private ResultSetOptional resultSetOptional;

    /** Create a new query executor from an IProvideConnection instance. By default this will generate
     * auto committing connections when requested.
     * @param connectionProvider A connection provider instance
     * @throws SQLException when failing to establish a connection
     */
    public ExecuteQueries(IProvideConnection connectionProvider) throws SQLException{
        this.reuseConnection = false;
        this.connection = connectionProvider.getConnection();
    }

    /** Create a new query executor from an IProvideConnection instance and a boolean flag indicating whether
     * a non-autocommit connection should be used. Calling this with the flag set as false is the same as calling it
     * with the flag omitted entirely.
     * @param connectionProvider A connection provider instance
     * @param transactional A boolean flag which will disable auto-commit when set to true.
     * @throws SQLException when failing to establish a connection
     */
    public ExecuteQueries(IProvideConnection connectionProvider, boolean transactional) throws SQLException{
        this.reuseConnection = false;
        if(transactional){
            this.connection = connectionProvider.getTransactionalConnection();
        }else{
            this.connection = connectionProvider.getConnection();
        }
    }

    /**
     * Create a query executor from an existing connection. This flags the provided connection for reuse and won't
     * close the connection as part of the usual clean up.
     * @param connection An explicit connection to use rather than a provider
     */
    public ExecuteQueries(Connection connection){
        this.reuseConnection = true;
        this.connection = connection;
    }

    @Override
    public ResultSetOptional executeUpdate(String sql, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
    public ResultSetOptional executeUpdate(QueryBuilder query, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
    public ResultSetOptional executeBatchUpdate(String sql, ICollectMappedParameters replacementParameterList) throws QueryBuilderException, SQLException{
        return this.executeBatchUpdate(QueryBuilder.fromString(sql), replacementParameterList);
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeBatchUpdate(String sql, Collection<T> entities) throws QueryBuilderException, SQLException{
        List<IMapParameters> parameters = entities.stream().map(IExportToMap::exportToMap).collect(Collectors.toList());
        ICollectMappedParameters collected = CollectedParameterMaps.of(parameters);
        return this.executeBatchUpdate(QueryBuilder.fromString(sql), collected);
    }

    @Override
    public <T extends IExportToMap> ResultSetOptional executeBatchUpdate(QueryBuilder query, Collection<T> entities) throws QueryBuilderException, SQLException{
        List<IMapParameters> parameters = entities.stream().map(IExportToMap::exportToMap).collect(Collectors.toList());
        ICollectMappedParameters collected = CollectedParameterMaps.of(parameters);
        return this.executeBatchUpdate(query, collected);
    }

    @Override
    public ResultSetOptional executeBatchUpdate(QueryBuilder query, ICollectMappedParameters replacementParameterList) throws QueryBuilderException, SQLException{
        this.resultSetOptional = new ResultSetOptional();

        try{

            this.statement = query.prepare(this.connection);
            for(IMapParameters pm: replacementParameterList.get()){
                query.batchParameterise(this.statement, pm);
            }
            this.resultSetOptional.setExecuted(this.statement.executeBatch());
        }catch(SQLException | QueryBuilderException e){
            this.resultSetOptional.setException(e);
        }

        return this.resultSetOptional;
    }

    @Override
    public ResultSetOptional executeQuery(String sql, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
    public void commit() throws SQLException{
        try{
            if(this.connection != null && !this.connection.isClosed()){
                this.connection.commit();
            }
        }catch(SQLException e){
            try{
                this.connection.rollback();
            }catch(SQLException e1){
                throw e;
            }
        }
    }

    @Override
    public void rollback() throws SQLException{
        if(this.connection != null && !this.connection.isClosed()){
            this.connection.rollback();
        }
    }


    @Override
    public ResultSetOptional executeQuery(QueryBuilder query, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
    public ResultSetOptional execute(String sql, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
    public ResultSetOptional execute(QueryBuilder query, IMapParameters replacementParameters) throws QueryBuilderException, SQLException{
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
        /* Close any connections */
        if(this.connection != null && !this.reuseConnection){
            try{
                this.connection.close();
            }catch(SQLException e){
                //Already gone
            }
        }

        /* Close any prepared statements */
        if(this.statement != null){
            try{
                this.statement.close();
            }catch(SQLException e){
                //Already gone
            }
        }

        /* Close any result sets */
        if(this.resultSetOptional != null){
            this.resultSetOptional.close();
        }
    }
}
