package de.themonstrouscavalca.dbaser.dao.interfaces.basic;

import de.themonstrouscavalca.dbaser.dao.QuickResponses;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


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

    //Extra handler for find queries expecting a single result
    default ResponseAndError<T> findSingle(String sql, IMapParameters listingParameters){
        ResponseAndError<List<T>> result = this.find(sql, listingParameters, true, false);
        if(result.isSuccess()){
            List<T> found = result.response().orElse(Collections.emptyList());
            return new ResponseAndError<>(Optional.ofNullable(found.getFirst()), null);
        }
        return QuickResponses.repackageError(result);
    }

    default ResponseAndError<T> findSingle(Connection connection, String sql, IMapParameters listingParameters){
        ResponseAndError<List<T>> result = this.find(connection, sql, listingParameters, true, false);
        if(result.isSuccess()){
            List<T> found = result.response().orElse(Collections.emptyList());
            return new ResponseAndError<>(Optional.ofNullable(found.getFirst()), null);
        }
        return QuickResponses.repackageError(result);
    }

    ResponseAndError<T> get(IMapParameters listingParameters);
    ResponseAndError<T> get(Connection connection, IMapParameters listingParameters);
}
