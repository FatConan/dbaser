package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.database.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.database.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.database.queries.QueryBuilder;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicIdentifiedModelDAO<T extends BasicIdentifiedModel> implements IModelDAO<T>{
    protected final HandleResultSets<T> handler = new HandleResultSets<>();

    protected IProvideConnection connectionProvider;

    protected abstract String getSelectSpecificSQL();
    protected abstract String getSelectListSQL();
    protected abstract String getUpdateSQL();
    protected abstract String getInsertSQL();
    protected abstract String getDeleteSQL();

    protected List<T> getList(String sql, Map<String, Object> replacementParameters){
        List<T> results = new ArrayList<>();
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(sql, replacementParameters)){
                if(rso.isPresent()){
                    ResultSet rs = rso.get();
                    while(rs.next()){
                        T entity = this.createInstance();
                        entity.populateFromResultSet(rs);
                        results.add(entity);
                    }
                }
            }
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
                e.printStackTrace();
        }
        return results;
    }

    public T get(Map<String, Object> replacementParameters){
        T entity = this.createInstance();
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(this.getSelectSpecificSQL(), replacementParameters)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            e.printStackTrace();
        }
        return entity;
    }

    public List<T> getList(Map<String, Object> replacementParameters){
        return getList(this.getSelectListSQL(), replacementParameters);
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
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            e.printStackTrace();
        }
        return entity;
    }

    public T save(T entity){
        return this.save(entity, false);
    }

    public T delete(T entity){
        try(ExecuteQueries<T> executor = new ExecuteQueries<>(connectionProvider)){
            try(ResultSetOptional rso = executor.executeUpdate(this.getDeleteSQL(), entity)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            e.printStackTrace();
        }
        return entity;
    }

    public abstract T createInstance();
}
