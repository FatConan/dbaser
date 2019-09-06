package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

import java.util.HashMap;
import java.util.Map;

public class ParameterMap implements IMapParameters{
    private static class EmptyParameterMap extends ParameterMap{
        @Override
        public void put(String key, Object value){
            throw new UnsupportedOperationException("This operation is not supported on an immutable EmptyParameterMap instance");
        }
    }

    private static final ParameterMap EMPTY = new EmptyParameterMap();

    public static ParameterMap empty(){
        return EMPTY;
    }

    private final Map<String, Object> params = new HashMap<>();

    @Override
    public void put(String key, Object value){
        this.params.put(key, value);
    }

    @Override
    public Object get(String key){
        return this.params.get(key);
    }

    @Override
    public boolean has(String key){
        return this.params.containsKey(key);
    }

    @Override
    public boolean isEmpty(){
        return this.params.isEmpty();
    }

    @Override
    public Map<String, Object> asMap(){
        return this.params;
    }
}

