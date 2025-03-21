package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.SQLException;
import java.util.List;

public interface IProcessingHandlers<T extends BasicModel>{
    @FunctionalInterface
    interface PostHook<V>{
        void hook(ExecuteQueries executor, ResponseOrError<V> entity);
    }

    @FunctionalInterface
    interface ExecutorCall<V>{
        ResultSetOptional execute(ExecuteQueries executor, V entity, String sql) throws SQLException, QueryBuilderException;
    }

    default ExecutorCall<T> executorCall(){
        return (executor, entity, sql) -> executor.execute(sql, entity);
    }

    default ExecutorCall<T> queryingExecutorCall(){
        return (executor,entity,sql)->executor.executeQuery(sql,entity);
    }

    default ExecutorCall<T> defaultExecutorCall(){
        return this.executorCall();
    }

    ResponseOrError<T> processSave(ExecuteQueries executor, T entity, String sql,
                                   ExecutorCall<T> executorCall,
                                   PostHook<T> hook) throws SQLException, QueryBuilderException;

    ResponseOrError<T> processSave(ExecuteQueries executor, T entity, String sql,
                                   PostHook<T> hook) throws SQLException, QueryBuilderException;

    ResponseOrError<List<T>> processPersistAll(ExecuteQueries executor, String sql,
                                               ICollectMappedParameters params,
                                               IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException;

    ResponseOrError<T> processPersist(ExecuteQueries executor, String sql, IMapParameters params,
                                      IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException;

    ResponseOrError<Boolean> processDelete(ExecuteQueries executor, String deleteSql, IMapParameters params,
                                           Long responseIdentifier, PostHook<Long> hook);

    ResponseOrError<List<T>> processList(ExecuteQueries executor, String sql, IMapParameters parameters,
                                         boolean expectSingleResult, boolean expectingResult,
                                         IHandleResultSets.Gen<T> generator);

    ResponseOrError<T> processSingle(ExecuteQueries executor, String sql, IMapParameters parameters,
                                     IHandleResultSets.Gen<T> generator);
}
