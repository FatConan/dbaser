package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;

public interface IWriteDAO<T>{
    ResponseAndError<T> save(T entity);
    ResponseAndError<T> save(Connection connection, T entity);
    ResponseAndError<T> save(T entity, boolean forceInsert);
    ResponseAndError<T> save(Connection connection, T entity, boolean forceInsert);

    ResponseAndError<Boolean> delete(long id);
    ResponseAndError<Boolean> delete(Connection connection, long id);
}
