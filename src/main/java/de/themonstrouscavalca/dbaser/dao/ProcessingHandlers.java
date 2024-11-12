package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProcessingHandlers;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class ProcessingHandlers<T extends BasicModel> implements IProcessingHandlers<T>{
    private Logger logger = LoggerFactory.getLogger(ProcessingHandlers.class);

    private <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }
    private final IHandleResultSets<T> handler;

    public ProcessingHandlers(IHandleResultSets<T> handler){
        this.handler = handler;
    }

    public ResponseAndError<T> processSave(ExecuteQueries executor, T entity, String sql, PostHook<T> hook) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.execute(sql, entity)){
            ResponseAndError<T> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleSingleResultSet(rso, entity);
            }else{
                responseAndError = ResponseAndError.success(entity);
            }
            hook.hook(executor, responseAndError);
            return responseAndError;
        }
    }

    public ResponseAndError<List<T>> processPersistAll(ExecuteQueries executor, String sql, ICollectMappedParameters params,
                                                          IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.executeBatchUpdate(sql, params)){
            ResponseAndError<List<T>> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleMultipleResultSets(rso, generator);
            }else{
                //If we don't get a response there's nothing to return
                responseAndError = ResponseAndError.success(null);
            }
            return responseAndError;
        }
    }

    public ResponseAndError<T> processPersist(ExecuteQueries executor, String sql, IMapParameters params,
                                                 IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.execute(sql, params)){
            ResponseAndError<T> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleSingleResultSet(rso, generator.create());
            }else{
                responseAndError = ResponseAndError.success(null);
            }
            return responseAndError;
        }
    }

    public ResponseAndError<Boolean> processDelete(ExecuteQueries executor, String deleteSql, IMapParameters params,
                                                   Long responseIdentifier, PostHook<Long> hook){
        try{
            executor.execute(deleteSql, params);
            ResponseAndError<Long> entityOpt = ResponseAndError.success(responseIdentifier);
            hook.hook(executor, entityOpt);
            return ResponseAndError.success(true);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error deleting entity", e, this::exceptionAction);
        }
    }

    public ResponseAndError<List<T>> processList(ExecuteQueries executor, String sql, IMapParameters parameters,
                                                    boolean expectSingleResult, boolean expectingResult,
                                                    IHandleResultSets.Gen<T> generator){
        try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
            return handler.handleMultipleResultSets(rso, generator, expectSingleResult, expectingResult);
        }catch(QueryBuilderException | SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    public ResponseAndError<T> processSingle(ExecuteQueries executor, String sql, IMapParameters parameters,
                                             IHandleResultSets.Gen<T> generator){
        try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
            return handler.handleSingleResultSet(rso, generator.create());
        }catch(QueryBuilderException | SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, err -> logger.error(err.getMessage(), err));
        }
    }
}
