package de.themonstrouscavalca.database.dao.interfaces;

import de.themonstrouscavalca.database.models.BasicModel;

import java.util.HashMap;
import java.util.List;

public interface IModelDAO<T extends BasicModel>{
    T get(HashMap<String, Object> replacementParameters);
    List<T> getList(HashMap<String, Object> replacementParameters);
    T save(T entity);
    T delete(T entity);
    T createInstance();
}
