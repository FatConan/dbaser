package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicIdentifiedModelDAO<T extends BasicIdentifiedModel> implements IModelDAO<T>{
    protected Logger logger = LoggerFactory.getLogger(BasicIdentifiedModelDAO.class);

    protected final HandleResultSets<T> handler = new HandleResultSets<>();

    protected IProvideConnection connectionProvider;

    protected abstract String getSelectSpecificSQL();
    protected abstract String getSelectListSQL();
    protected abstract String getUpdateSQL();
    protected abstract String getInsertSQL();
    protected abstract String getDeleteSQL();

    protected List<T> getList(String sql, Map<String, Object> parameters){
        List<T> results = new ArrayList<>();
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
                if(rso.isPresent()){
                    ResultSet rs = rso.get();
                    while(rs.next()){
                        T entity = this.createInstance();
                        entity.populateFromResultSet(rs);
                        results.add(entity);
                    }
                }
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error listing entries", e);
        }
        return results;
    }

    public T get(Map<String, Object> parameters){
        T entity = this.createInstance();
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(this.getSelectSpecificSQL(), parameters)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error fetching entry", e);
        }
        return entity;
    }

    public List<T> getList(Map<String, Object> parameters){
        return getList(this.getSelectListSQL(), parameters);
    }

    public List<T> getList(){
        return getList(this.getSelectListSQL(), new HashMap<>());
    }

    public T save(T entity, boolean forceInsert){
        String statement = this.getInsertSQL();
        if(entity.getId() != null && !forceInsert){
            statement = this.getUpdateSQL();
        }

        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeUpdate(statement, entity)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilderException e){
           logger.error("Error saving entry", e);
        }
        return entity;
    }

    public T save(T entity){
        return this.save(entity, false);
    }

    public T delete(Map<String, Object> parameters){
        T entity = this.createInstance();
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeUpdate(this.getDeleteSQL(), parameters)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error deleting entry", e);
        }
        return entity;
    }

    public abstract T createInstance();
}
