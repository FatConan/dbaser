package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;

public class QueryAndParametersBundle{
    private final QueryBuilder query;
    private final IMapParameters parameters;

    public QueryAndParametersBundle(QueryBuilder query, IMapParameters parameters){
        this.query = query;
        this.parameters = parameters;
    }

    public QueryBuilder getQuery(){
        return query;
    }

    public IMapParameters getParameters(){
        return parameters;
    }
}
