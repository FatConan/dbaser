package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.database.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasicIndentifiedModelDAO<T extends BasicIdentifiedModel> implements IModelDAO<T>{
    protected final ExecuteQueries<T> executer = new ExecuteQueries<>();
    protected final HandleResultSets<T> handler = new HandleResultSets<>();

    protected abstract Connection getConnection();

    protected abstract String getSelectSpecificSQL();
    protected abstract String getSelectListSQL();
    protected abstract String getUpdateSQL();
    protected abstract String getInsertSQL();
    protected abstract String getDeleteSQL();

    protected List<T> getList(String sql, Map<String, Object> replacementParameters){
        List<T> results = new ArrayList<>();
        ResultSetOptional rso = this.executer.executeQuery(this.getConnection(), sql, replacementParameters);
        if(rso.isPresent()){
            ResultSet rs = rso.get();
            try{
                while(rs.next()){
                    T entity = this.createInstance();
                    entity.populateFromResultSet(rs);
                    results.add(entity);
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return results;
    }

    public T get(Map<String, Object> replacementParameters){
        T entity = this.createInstance();
        ResultSetOptional rso = this.executer.executeQuery(this.getConnection(), this.getSelectSpecificSQL(), replacementParameters);
        return handler.handleResultSet(rso, entity);
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
        ResultSetOptional rso = this.executer.execute(this.getConnection(), statement, entity);
        return handler.handleResultSet(rso, entity);
    }

    public T save(T entity){
        return this.save(entity, false);
    }

    public T delete(T entity){
        ResultSetOptional rso = this.executer.execute(this.getConnection(), this.getDeleteSQL(), entity);
        return handler.handleResultSet(rso, entity);
    }

    public abstract T createInstance();
}
