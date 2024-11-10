package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.util.List;
import java.util.Optional;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    @FunctionalInterface
    interface Gen<T> {
        T create();
    }

    ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity);
    ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult);
    ResponseAndError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator);
}
