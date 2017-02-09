package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IModel;

import java.util.List;
import java.util.Map;

public interface IModelDAO<T extends IModel>{
    T get(Map<String, Object> parameters);
    List<T> getList(Map<String, Object> parameters);
    List<T> getList();
    T save(T entity);
    T save(T entity, boolean forceInsert);
    T delete(Map<String, Object> parameters);
    T createInstance();
}
