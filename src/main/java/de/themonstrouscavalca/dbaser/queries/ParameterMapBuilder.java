package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

public class ParameterMapBuilder{
    private IMapParameters paramMap;

    public static ParameterMapBuilder build(){
        return new ParameterMapBuilder();
    }

    private ParameterMapBuilder(){
        this.paramMap = new ParameterMap();
    }

    public ParameterMapBuilder add(String key, Object value){
        this.paramMap.put(key, value);
        return this;
    }

    public IMapParameters get(){
        return this.paramMap;
    }
}
