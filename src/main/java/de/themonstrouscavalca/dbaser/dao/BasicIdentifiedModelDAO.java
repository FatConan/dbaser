package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicIdentifiedModelDAO<T extends BasicIdentifiedModel> implements IModelDAO<T>{
    protected final HandleResultSets<T> handler = new HandleResultSets<>();
    protected Logger logger = LoggerFactory.getLogger(BasicIdentifiedModelDAO.class);
    protected IProvideConnection connectionProvider;

    protected abstract String getSelectSpecificSQL();

    protected abstract String getSelectListSQL();

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    protected List<T> getList(String sql, IMapParameters parameters){
        List<T> results = new ArrayList<>();
        try(ExecuteQueries executor = new ExecuteQueries(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
                if(rso.isPresent()){
                    ResultSetTableAware rs = rso.get();
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

    public Optional<T> get(IMapParameters parameters){
        T entity = this.createInstance();
        try(ExecuteQueries executor = new ExecuteQueries(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(this.getSelectSpecificSQL(), parameters)){
                return handler.handleResultSet(rso, entity);
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error fetching entry", e);
            return Optional.empty();
        }
    }

    public List<T> getList(IMapParameters parameters){
        return getList(this.getSelectListSQL(), parameters);
    }

    public List<T> getList(){
        return getList(this.getSelectListSQL(), ParameterMap.empty());
    }

    public T save(T entity, boolean forceInsert){
        String statement;
        if(entity.hasId() && !forceInsert){
            statement = this.getUpdateSQL();
        }else{
            statement = this.getInsertSQL();
        }

        try(ExecuteQueries executor = new ExecuteQueries(connectionProvider)){
            try(ResultSetOptional rso = executor.executeUpdate(statement, entity)){
                return handler.handleResultSet(rso, entity).orElse(null);
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error saving entry", e);
            return null;
        }
    }

    public T save(T entity){
        return this.save(entity, false);
    }

    public T delete(IMapParameters parameters){
        T entity = this.createInstance();
        try(ExecuteQueries executor = new ExecuteQueries(connectionProvider)){
            try(ResultSetOptional rso = executor.executeUpdate(this.getDeleteSQL(), parameters)){
                return handler.handleResultSet(rso, entity).orElse(null);
            }
        }catch(SQLException | QueryBuilderException e){
            logger.error("Error deleting entry", e);
            return null;
        }
    }

    public abstract T createInstance();
}
