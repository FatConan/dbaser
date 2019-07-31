package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;


/**
 * The QueryAndParametersBundle is a simple pair of a QueryBuilder and a IMapParameters object.  This is useful for reducing
 * repetition in code by allowing the pairing to be returned from setup methods
 */
public class QueryAndParametersBundle{
    private final QueryBuilder query;
    private final IMapParameters parameters;

    /**
     * Create a QueryAndParametersBundle with the provided query and parameters
     *
     * @param query QueryBuilder object
     * @param parameters IMapParameters object
     */
    public QueryAndParametersBundle(QueryBuilder query, IMapParameters parameters){
        this.query = query;
        this.parameters = parameters;
    }

    /**
     * Get the query from the bundle
     *
     * @return QueryBuilder query
     */
    public QueryBuilder getQuery(){
        return query;
    }

    /**
     * Get the parameters from the bundle
     *
     * @return IMapParameters parameters
     */
    public IMapParameters getParameters(){
        return parameters;
    }
}
