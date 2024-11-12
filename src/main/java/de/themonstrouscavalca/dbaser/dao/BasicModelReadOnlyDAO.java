package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProcessingHandlers;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.dao.interfaces.basic.IReadDAO;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
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
    public ResponseAndError<List<T>> find(String sql, IMapParameters listingParameters, boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processList(executor,sql, listingParameters,
                    expectSingleResult, expectingResult, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(Connection connection, String sql, IMapParameters listingParameters,
                                          boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processList(executor, sql, listingParameters,
                    expectSingleResult, expectingResult, this::create);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processList(executor, this.getListSQL(), listingParameters,
                    false, false, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(Connection connection, IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processList(executor, this.getListSQL(), listingParameters,
                    false, false, this::create);
        }
    }
    //endregion

    //region Result set processing for single results
    @Override
    public ResponseAndError<T> get(IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), listingParameters, this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> get(Connection connection, IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), listingParameters, this::create);
        }
    }
    //endregion

    protected ResponseAndError<Long> count(String sql, IMapParameters params){
        long total = 0L;
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            try (ResultSetOptional rso = executor.executeQuery(sql, params)) {
                if (rso.isPresent()){
                    ResultSet rs = rso.get();
                    if(rs.next()){
                        total = rs.getLong("total");
                    }
                }
            }
        }catch(SQLException | QueryBuilderException e) {
            return QuickResponses.sqlError("Error counting entities", e, this::exceptionAction);
        }
        return ResponseAndError.success(total);
    }
}

