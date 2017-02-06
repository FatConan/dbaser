package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IExecuteQueries;
import de.themonstrouscavalca.database.models.interfaces.IExportToMap;
import de.themonstrouscavalca.database.queries.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by ian on 2/6/17.
 */
public abstract class ExecuteQueries<T extends IExportToMap> implements IExecuteQueries<T>{

    @Override
    public T execute(Connection connection, String sql, Map<String, Object> replacementParameters, T entity) {
        QueryBuilder builder = QueryBuilder.fromString(sql);
        try(PreparedStatement ps = builder.prepare(connection, replacementParameters)){
            builder.parameterize(ps, replacementParameters);
            this.processResultSet(ps, entity);
        }catch(SQLException e){
            //Do soemthing here
        }catch(QueryBuilder.QueryBuilderException e){
            //Do something here to record the error sensibly propagate the failure through the chain
        }
        return entity;
    }

    @Override
    public T execute(Connection connection, String sql, T entity) {
        return null;
    }
}
