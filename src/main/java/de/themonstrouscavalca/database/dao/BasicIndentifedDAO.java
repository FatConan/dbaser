package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.database.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.database.queries.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicIndentifedDAO<T extends BasicIdentifiedModel> implements IModelDAO<T>{

    protected abstract String getSelectSpecificSQL();
    protected abstract String getSelectListSQL();
    protected abstract String getUpdateSQL();
    protected abstract String getInsertSQL();
    protected abstract String getDeleteSQL();

    protected T execute(Connection connection, String sql, Map<String, Object> replacementParameters, T entity){
        QueryBuilder builder = QueryBuilder.fromString(sql);
        try(PreparedStatement ps = builder.prepare(connection, replacementParameters)){
            builder.parameterize(ps, replacementParameters);
            this.processResultSet(ps, entity);
        }catch(SQLException e){
            //Do something here to record the error
        }
        return entity;
    }

    protected void processResultSet(PreparedStatement ps, T entity) throws SQLException {
        processResultSet(ps, entity, "No matching entry found in the database");
    }

    protected void processResultSet(PreparedStatement ps, T entity, String errorMsg) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if(rs.next()) {
                entity.setFromResultSet(rs);
            }else{
                /*entity.setErrorStatus(DBErrorStatus.NOT_FOUND);
                entity.setError(errorMsg);*/
            }
        }
    }

    protected List<T> getList(Connection connection, String sql, Map<String, Object> replacementParameters){
        List<T> results = new ArrayList<>();
        QueryBuilder builder = QueryBuilder.fromString(sql);
        try(PreparedStatement ps = builder.prepare(connection, replacementParameters)) {
            builder.parameterise(ps, replacementParameters);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    T entity = this.createInstance();
                    entity.setFromResultSet(rs);
                    results.add(entity);
                }
            }
        } catch (SQLException e) {
            //Logger.error("Error fetching entity", e);
        }
        return results;
    }

    public T get(Connection connection, Map<String, Object> replacementParameters){
        T entity = this.createInstance();
        return execute(connection, this.getSelectSpecificSQL(), replacementParameters, entity);
    }

    public List<T> getList(Connection connection, Map<String, Object> replacementParameters){
        return getList(connection, this.getSelectListSQL(), replacementParameters);
    }

    public T save(Connection connection, T entity){
        String statement = this.getInsertSQL();
        if(entity.getId() != null){
            statement = this.getUpdateSQL();
        }
        return execute(connection, statement, entity.replacementParameters(), entity);
    }

    public T delete(Connection connection, T entity){
        return execute(connection, this.getDeleteSQL(), entity.replacementParameters(), entity);
    }

    public abstract T createInstance();
}
