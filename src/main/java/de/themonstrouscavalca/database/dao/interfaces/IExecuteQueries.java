package de.themonstrouscavalca.database.dao.interfaces;

import de.themonstrouscavalca.database.models.interfaces.IExportToMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by ian on 2/6/17.
 */
public interface IExecuteQueries<T extends IExportToMap>{
    void processResultSet(PreparedStatement ps, T entity) throws SQLException;
    void processResultSet(PreparedStatement ps, T entity, String errorMsg) throws SQLException;
    T execute(Connection connection, String sql, Map<String, Object> replacementParameters, T entity);
    T execute(Connection connection, String sql, T entity);
}
