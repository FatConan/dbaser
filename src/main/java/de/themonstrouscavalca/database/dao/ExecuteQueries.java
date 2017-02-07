package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IExecuteQueries;
import de.themonstrouscavalca.database.models.interfaces.IExportToMap;
import de.themonstrouscavalca.database.queries.QueryBuilder;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ExecuteQueries<T extends IExportToMap> implements IExecuteQueries<T>{
    @Override
    public ResultSetOptional execute(Connection connection, String sql, Map<String, Object> replacementParameters) {
        ResultSetOptional rsOptional = new ResultSetOptional();

        QueryBuilder builder = QueryBuilder.fromString(sql);
        try(PreparedStatement ps = builder.prepare(connection, replacementParameters)){
            builder.parameterize(ps, replacementParameters);
            ResultSet rs = ps.executeQuery();
            rsOptional.setResultSet(rs);
        }catch(SQLException e){
            rsOptional.setErrorMsg("SQL Error:" + e.getMessage());
        }catch(QueryBuilder.QueryBuilderException e){
            rsOptional.setErrorMsg("QueryBuilder Error:" + e.getMessage());
        }

        return rsOptional;
    }

    @Override
    public ResultSetOptional execute(Connection connection, String sql, T entity) {
        return this.execute(connection, sql, entity.exportToMap());
    }
}
