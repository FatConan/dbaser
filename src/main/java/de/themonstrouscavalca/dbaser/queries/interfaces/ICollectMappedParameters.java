package de.themonstrouscavalca.dbaser.queries.interfaces;

import java.util.Collection;

public interface ICollectMappedParameters{
    void add(IMapParameters parameters);
    Collection<IMapParameters> get();
}
