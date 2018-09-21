package de.themonstrouscavalca.dbaser.queries.interfaces;

import java.util.Map;

public interface IMapParameters{
    void put(String key, Object value);
    Object get(String key);
    boolean has(String key);
    boolean isEmpty();
    Map<String, Object> asMap();
}
