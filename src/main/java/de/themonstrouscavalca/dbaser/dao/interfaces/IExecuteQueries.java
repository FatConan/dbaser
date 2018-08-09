package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface IExecuteQueries extends AutoCloseable{
    ResultSetOptional execute(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional execute(String sql, T entity) throws QueryBuilderException, SQLException;
    ResultSetOptional execute(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional execute(QueryBuilder query, T entity) throws QueryBuilderException, SQLException;

    ResultSetOptional executeUpdate(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional executeUpdate(String sql, T entity) throws QueryBuilderException, SQLException;
    ResultSetOptional executeUpdate(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional executeUpdate(QueryBuilder query, T entity) throws QueryBuilderException, SQLException;

    ResultSetOptional executeQuery(String sql, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional executeQuery(String sql, T entity) throws QueryBuilderException, SQLException;
    ResultSetOptional executeQuery(QueryBuilder query, Map<String, Object> replacementParameters) throws QueryBuilderException, SQLException;
    <T extends IExportToMap> ResultSetOptional executeQuery(QueryBuilder query, T entity) throws QueryBuilderException, SQLException;

    void commit() throws SQLException;
    void rollback() throws SQLException;
}
