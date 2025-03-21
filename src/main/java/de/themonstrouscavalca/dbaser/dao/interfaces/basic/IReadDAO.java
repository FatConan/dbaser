package de.themonstrouscavalca.dbaser.dao.interfaces.basic;

import de.themonstrouscavalca.dbaser.dao.QuickResponses;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public interface IReadDAO<T>{
    IProvideConnection getConnectionProvider();

    //Flexible find
    ResponseOrError<List<T>> find(String sql, IMapParameters listingParameters,
                                  boolean expectSingleResult, boolean expectingResult);
    ResponseOrError<List<T>> find(Connection connection, String sql, IMapParameters listingParameters,
                                  boolean expectSingleResult, boolean expectingResult);

    //Find based on parameterising a set listing query
    ResponseOrError<List<T>> find(IMapParameters listingParameters);
    ResponseOrError<List<T>> find(Connection connection, IMapParameters listingParameters);

    //Extra handler for find queries expecting a single result
    default ResponseOrError<T> findSingle(String sql, IMapParameters listingParameters){
        ResponseOrError<List<T>> result = this.find(sql, listingParameters, true, false);
        if(result.isSuccess()){
            List<T> found = result.response().orElse(Collections.emptyList());
            return ResponseOrError.success(found.getFirst());
        }
        return QuickResponses.repackageError(result);
    }

    default ResponseOrError<T> findSingle(Connection connection, String sql, IMapParameters listingParameters){
        ResponseOrError<List<T>> result = this.find(connection, sql, listingParameters, true, false);
        if(result.isSuccess()){
            List<T> found = result.response().orElse(Collections.emptyList());
            return ResponseOrError.success(found.getFirst());
        }
        return QuickResponses.repackageError(result);
    }

    ResponseOrError<T> get(IMapParameters listingParameters);
    ResponseOrError<T> get(Connection connection, IMapParameters listingParameters);
}
