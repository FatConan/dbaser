package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.util.List;


public interface IReadDAO<T>{
    IProvideConnection getConnectionProvider();

    ResponseAndError<List<T>> find(IMapParameters listingParameters, boolean expectSingleResult);
    ResponseAndError<List<T>> find(Connection connection, IMapParameters listingParameters, boolean expectSingleResult);
    ResponseAndError<List<T>> find(IMapParameters listingParameters);
    ResponseAndError<List<T>> find(Connection connection, IMapParameters listingParameters);

    ResponseAndError<T> get(long id);
    ResponseAndError<T> get(Connection connection, long id);
}
