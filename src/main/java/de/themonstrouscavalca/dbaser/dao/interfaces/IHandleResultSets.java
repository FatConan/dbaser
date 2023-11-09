package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.util.Optional;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    Optional<T> handleResultSet(ResultSetOptional rsOptional, T entity);
}
