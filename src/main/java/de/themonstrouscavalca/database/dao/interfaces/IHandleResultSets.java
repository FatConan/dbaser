package de.themonstrouscavalca.database.dao.interfaces;

import de.themonstrouscavalca.database.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    T handleResultSet(ResultSetOptional rsOptional, T entity);
}
