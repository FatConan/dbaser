package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProcessingHandlers;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.dao.interfaces.basic.IReadDAO;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class BasicModelReadOnlyDAO<T extends BasicModel> implements IReadDAO<T>{
    protected Logger logger = LoggerFactory.getLogger(BasicModelReadOnlyDAO.class);
    protected <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }

    protected final IHandleResultSets<T> handler = new HandleResultSets<>();
    protected final IProcessingHandlers<T> processingHandlers = new ProcessingHandlers<>(handler);
    protected final IProvideConnection connectionProvider;

    public BasicModelReadOnlyDAO(IProvideConnection connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public IProvideConnection getConnectionProvider(){
        return this.connectionProvider;
    }

    protected abstract String getLookupSQL();

    protected abstract String getListSQL();

    public abstract T create();

    //region Overriden Interface Methods for multiple results
    @Override
    public ResponseOrError<List<T>> find(String sql, IMapParameters listingParameters, boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processList(executor,sql, listingParameters,
                    expectSingleResult, expectingResult, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<List<T>> find(Connection connection, String sql, IMapParameters listingParameters,
                                         boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processList(executor, sql, listingParameters,
                    expectSingleResult, expectingResult, this::create);
        }
    }

    @Override
    public ResponseOrError<List<T>> find(IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processList(executor, this.getListSQL(), listingParameters,
                    false, false, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<List<T>> find(Connection connection, IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processList(executor, this.getListSQL(), listingParameters,
                    false, false, this::create);
        }
    }
    //endregion

    //region basic flexible queries

    @Override
    public <V> ResponseOrError<V> query(Connection connection, String sql, IMapParameters parameters, IHandleResultSets.Proc<V> handler){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSimple(executor, sql, parameters, handler);
        }
    }

    @Override
    public <V> ResponseOrError<V> query(String sql, IMapParameters listingParameters, IHandleResultSets.Proc<V> handler){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSimple(executor, sql, listingParameters, handler);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, this::exceptionAction);
        }
    }

    @Override
    public <V> ResponseOrError<List<V>> queryList(Connection connection, String sql, IMapParameters parameters, IHandleResultSets.Proc<V> handler){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSimpleList(executor, sql, parameters,
                    DAOConstants.EXPECT_MULTIPLE_RESULTS,
                    DAOConstants.RESULT_NULLABLE,
                    handler);
        }
    }

    @Override
    public <V> ResponseOrError<List<V>> queryList(String sql, IMapParameters listingParameters, IHandleResultSets.Proc<V> handler){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSimpleList(executor, sql, listingParameters,
                    DAOConstants.EXPECT_MULTIPLE_RESULTS,
                    DAOConstants.RESULT_NULLABLE,
                    handler);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, this::exceptionAction);
        }
    }

    //endregion

    //region Result set processing for single results
    @Override
    public ResponseOrError<T> get(IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), listingParameters, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<T> get(Connection connection, IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), listingParameters, this::create);
        }
    }
    //endregion

    @Override
    public ResponseOrError<List<T>> list(){
        return this.find(this.getListSQL(), ParameterMap.empty(),
                DAOConstants.EXPECT_MULTIPLE_RESULTS, DAOConstants.RESULT_NULLABLE);
    }

    @Override
    public ResponseOrError<List<T>> list(Connection connection){
        return this.find(connection, this.getListSQL(), ParameterMap.empty(),
                DAOConstants.EXPECT_MULTIPLE_RESULTS, DAOConstants.RESULT_NULLABLE);
    }

    protected ResponseOrError<Long> count(String sql, IMapParameters params){
        return this.query(sql, params, (rs) -> rs.getLong("total"));
    }
}

