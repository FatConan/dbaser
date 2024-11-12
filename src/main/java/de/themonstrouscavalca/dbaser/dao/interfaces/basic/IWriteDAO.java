package de.themonstrouscavalca.dbaser.dao.interfaces.basic;

import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.util.List;

public interface IWriteDAO<T>{
    ResponseAndError<T> save(T entity, boolean forceInsert);
    ResponseAndError<T> save(Connection connection, T entity, boolean forceInsert);

    ResponseAndError<Boolean> delete(IMapParameters parameters);
    ResponseAndError<Boolean> delete(Connection connection, IMapParameters parameters);
}
