package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.SQLException;
import java.util.Map;

public interface IExecuteQueries<T extends IExportToMap> extends AutoCloseable{
    ResultSetOptional execute(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException;
    ResultSetOptional execute(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException;
    ResultSetOptional executeUpdate(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException;
    ResultSetOptional executeUpdate(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException;
    ResultSetOptional executeQuery(String sql, Map<String, Object> replacementParameters) throws QueryBuilder.QueryBuilderException, SQLException;
    ResultSetOptional executeQuery(String sql, T entity) throws QueryBuilder.QueryBuilderException, SQLException;
}
