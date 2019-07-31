package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

public class ParameterMapBuilder{
    private IMapParameters paramMap;

    public ParameterMapBuilder(){
        this.paramMap = new ParameterMap();
    }

    public ParameterMapBuilder add(String key, Object value){
        this.paramMap.put(key, value);
        return this;
    }

    public IMapParameters build(){
        return this.paramMap;
    }
}
