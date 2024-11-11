package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.util.List;

public interface IWriteDAO<T>{
    ResponseAndError<T> persist(String sql, IMapParameters parameters);
    ResponseAndError<T> persist(Connection connection, String sql, IMapParameters parameters);
    ResponseAndError<List<T>> persistAll(String sql, ICollectMappedParameters parameters);
    ResponseAndError<List<T>> persistAll(Connection connection, String sql, ICollectMappedParameters parameters);

    ResponseAndError<T> save(T entity);
    ResponseAndError<T> save(Connection connection, T entity);
    ResponseAndError<T> save(T entity, boolean forceInsert);
    ResponseAndError<T> save(Connection connection, T entity, boolean forceInsert);

    ResponseAndError<Boolean> delete(long id);
    ResponseAndError<Boolean> delete(Connection connection, long id);
}
