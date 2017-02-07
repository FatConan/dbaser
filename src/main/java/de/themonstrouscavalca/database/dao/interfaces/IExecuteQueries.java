package de.themonstrouscavalca.database.dao.interfaces;

import de.themonstrouscavalca.database.models.interfaces.IExportToMap;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

import java.sql.Connection;
import java.util.Map;

public interface IExecuteQueries<T extends IExportToMap>{
    ResultSetOptional execute(Connection connection, String sql, Map<String, Object> replacementParameters);
    ResultSetOptional execute(Connection connection, String sql, T entity);
    ResultSetOptional executeQuery(Connection connection, String sql, Map<String, Object> replacementParameters);
    ResultSetOptional executeQuery(Connection connection, String sql, T entity);
}
