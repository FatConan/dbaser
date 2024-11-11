package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.util.List;


public interface IReadDAO<T>{
    IProvideConnection getConnectionProvider();

    //Flexible find
    ResponseAndError<List<T>> find(String sql, IMapParameters listingParameters,
                                   boolean expectSingleResult, boolean expectingResult);
    ResponseAndError<List<T>> find(Connection connection, String sql, IMapParameters listingParameters,
                                   boolean expectSingleResult, boolean expectingResult);

    //Find based on parameterising a set listing query
    ResponseAndError<List<T>> find(IMapParameters listingParameters);
    ResponseAndError<List<T>> find(Connection connection, IMapParameters listingParameters);

    ResponseAndError<T> get(long id);
    ResponseAndError<T> get(Connection connection, long id);
}
