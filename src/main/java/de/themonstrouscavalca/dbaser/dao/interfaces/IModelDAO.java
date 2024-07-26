package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

import java.util.List;
import java.util.Optional;

public interface IModelDAO<T extends IModel>{
    Optional<T> get(IMapParameters parameters);
    List<T> getList(IMapParameters parameters);
    List<T> getList();
    T save(T entity);
    T save(T entity, boolean forceInsert);
    T delete(IMapParameters parameters);
    T createInstance();
}
