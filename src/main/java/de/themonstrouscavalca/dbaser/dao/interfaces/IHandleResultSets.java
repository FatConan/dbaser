package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

public interface IHandleResultSets<T extends IPopulateFromResultSet>{
    T handleResultSet(ResultSetOptional rsOptional, T entity);
}
