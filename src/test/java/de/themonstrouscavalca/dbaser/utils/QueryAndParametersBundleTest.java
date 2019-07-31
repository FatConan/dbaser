package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryAndParametersBundleTest{
    @Test
    public void queryAndParametersTest(){
        QueryBuilder query = QueryBuilder.fromString("SELECT 1;");
        IMapParameters params = (new ParameterMapBuilder()).add("TEST", "TEST").build();
        QueryAndParametersBundle bundle = new QueryAndParametersBundle(query, params);

        assertEquals("The query doesn't match", query, bundle.getQuery());
        assertEquals("The parameters don't match", params, bundle.getParameters());
    }
}
