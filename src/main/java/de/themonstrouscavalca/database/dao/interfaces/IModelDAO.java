package de.themonstrouscavalca.database.dao.interfaces;

import de.themonstrouscavalca.database.models.interfaces.IModel;

import java.util.List;
import java.util.Map;

public interface IModelDAO<T extends IModel>{
    T get(Map<String, Object> replacementParameters);
    List<T> getList(Map<String, Object> replacementParameters);
    T save(T entity);
    T delete(T entity);
    T createInstance();
}
