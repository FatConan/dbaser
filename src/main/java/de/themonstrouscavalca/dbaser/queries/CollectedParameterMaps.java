package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

import java.util.ArrayList;
import java.util.Collection;

public class CollectedParameterMaps implements ICollectMappedParameters{
    private final Collection<IMapParameters> mappedParameters = new ArrayList<>();

    public void add(IMapParameters parameters){
        this.mappedParameters.add(parameters);
    }

    public Collection<IMapParameters> get(){
        return this.mappedParameters;
    }

    public static CollectedParameterMaps of(Collection<IMapParameters> parameters){
        CollectedParameterMaps p = new CollectedParameterMaps();
        p.mappedParameters.addAll(parameters);
        return p;
    }
}
