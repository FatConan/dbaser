package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.SQLException;
import java.util.List;

public interface IProcessingHandlers<T extends BasicModel>{
    @FunctionalInterface
    interface PostHook<V>{
        void hook(ExecuteQueries executor, ResponseAndError<V> entity);
    }

    ResponseAndError<T> processSave(ExecuteQueries executor, T entity, String sql,
                                    PostHook<T> hook) throws SQLException, QueryBuilderException;

    ResponseAndError<List<T>> processPersistAll(ExecuteQueries executor, String sql,
                                               ICollectMappedParameters params,
                                               IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException;

    ResponseAndError<T> processPersist(ExecuteQueries executor, String sql, IMapParameters params,
                                       IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException;

    ResponseAndError<Boolean> processDelete(ExecuteQueries executor, String deleteSql, IMapParameters params,
                                            Long responseIdentifier, PostHook<Long> hook);

    ResponseAndError<List<T>> processList(ExecuteQueries executor, String sql, IMapParameters parameters,
                                                 boolean expectSingleResult, boolean expectingResult,
                                                 IHandleResultSets.Gen<T> generator);

    ResponseAndError<T> processSingle(ExecuteQueries executor, String sql, IMapParameters parameters,
                                             IHandleResultSets.Gen<T> generator);
}
