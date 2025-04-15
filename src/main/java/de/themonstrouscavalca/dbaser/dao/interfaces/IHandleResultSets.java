package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.dao.QuickResponses;
import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    @FunctionalInterface
    interface Gen<U> {
        U create();
    }

    @FunctionalInterface
    interface Proc<U> {
        U process(ResultSetTableAware rs) throws SQLException;
    }

    <V> ResponseOrError<List<V>> handleMultipleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler, boolean expectSingleResult, boolean expectedResult);
    <V> ResponseOrError<List<V>> handleMultipleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler);
    <V> ResponseOrError<V> handleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler, boolean expectedResult);
    <V> ResponseOrError<V> handleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler);

    //This was formerly generic within the scope of T, but it has no reason to be so expand it to general use to accommodate
    //the new generic handler methods above. We can also encapsulate it in a default implementation.
    default <V> ResponseOrError<V> extractSingleResult(ResponseOrError<List<V>> listedResults){
        if(listedResults.isSuccess()){
            List<V> ents = listedResults.response().orElse(Collections.emptyList());
            if(ents.size() == 1){
                return ResponseOrError.success(ents.getFirst());
            }
        }
        return QuickResponses.repackageError(listedResults);
    }

    //These are model specific
    ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity);
    ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult);
    ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator);
    ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator, boolean expectSingleResult, boolean expectedResult);
}
