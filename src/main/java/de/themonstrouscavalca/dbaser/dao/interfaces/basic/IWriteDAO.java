package de.themonstrouscavalca.dbaser.dao.interfaces.basic;

import de.themonstrouscavalca.dbaser.dao.interfaces.IProcessingHandlers;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

import java.sql.Connection;

public interface IWriteDAO<T>{
    ResponseOrError<T> save(T entity, IProcessingHandlers.ExecutorCall<T> executorCall, boolean forceInsert);
    ResponseOrError<T> save(Connection connection, T entity, IProcessingHandlers.ExecutorCall<T> executorCall, boolean forceInsert);
    ResponseOrError<T> save(T entity, boolean forceInsert);
    ResponseOrError<T> save(Connection connection, T entity, boolean forceInsert);

    ResponseOrError<Boolean> delete(IMapParameters parameters);
    ResponseOrError<Boolean> delete(Connection connection, IMapParameters parameters);
}
