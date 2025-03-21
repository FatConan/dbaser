package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.util.List;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    @FunctionalInterface
    interface Gen<T> {
        T create();
    }

    ResponseOrError<T> extractSingleResult(ResponseOrError<List<T>> listedResults);
    ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity);
    ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult);
    ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator);
    ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator, boolean expectSingleResult, boolean expectedResult);
}
