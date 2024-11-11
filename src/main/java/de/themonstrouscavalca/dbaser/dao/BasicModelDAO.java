package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.ICollectMappedParameters;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class BasicModelDAO<T extends BasicModel> extends BasicModelReadOnlyDAO<T> implements IModelDAO<T>{

    protected BasicModelDAO(IProvideConnection connectionProvider){
        super(connectionProvider);
    }

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    public abstract T create();


    protected void postSave(ExecuteQueries executor, ResponseAndError<T> entityOptional){
        //Override this in sub classes to perform post save hooks
    }

    protected void postDelete(ExecuteQueries executor, ResponseAndError<Long> entityOptional){
        //Override this in sub classes to perform post save hooks
    }

    protected ResponseAndError<T> processSave(ExecuteQueries executor, T entity, String sql) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.execute(sql, entity)){
            ResponseAndError<T> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleSingleResultSet(rso, entity);
            }else{
                responseAndError = ResponseAndError.success(entity);
            }
            this.postSave(executor, responseAndError);
            return responseAndError;
        }
    }

    protected ResponseAndError<List<T>> processPersistAll(ExecuteQueries executor, String sql, ICollectMappedParameters params,
                                                     IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.executeBatchUpdate(sql, params)){
            ResponseAndError<List<T>> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleMultipleResultSets(rso, generator);
            }else{
                //If we don't get a response there's nothing to return
                responseAndError = ResponseAndError.success(null);
            }
            return responseAndError;
        }
    }

    protected ResponseAndError<T> processPersist(ExecuteQueries executor, String sql, IMapParameters params,
                                                           IHandleResultSets.Gen<T> generator) throws SQLException, QueryBuilderException{
        try(ResultSetOptional rso = executor.execute(sql, params)){
            ResponseAndError<T> responseAndError;
            if(rso.isPresent()){
                responseAndError = handler.handleSingleResultSet(rso, generator.create());
            }else{
                responseAndError = ResponseAndError.success(null);
            }
            return responseAndError;
        }
    }

    protected String selectSaveSQL(T entity, boolean forceInsert){
        if(forceInsert){
            return this.getInsertSQL();
        }
        return this.getUpdateSQL();
    }

    @Override
    public ResponseAndError<T> persist(String sql, IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processPersist(executor, sql, parameters, this::create);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error persisting entity", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> persist(Connection connection, String sql, IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processPersist(executor, sql, parameters, this::create);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error persisting entity", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> persistAll(String sql, ICollectMappedParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processPersistAll(executor, sql, parameters, this::create);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error persisting entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> persistAll(Connection connection, String sql, ICollectMappedParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processPersistAll(executor, sql, parameters, this::create);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error persisting entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> save(T entity, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert));
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> save(Connection connection, T entity, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert));
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> save(T entity){
        return this.save(entity, false);
    }

    @Override
    public ResponseAndError<T> save(Connection connection, T entity){
        return this.save(connection, entity, false);
    }

    protected ResponseAndError<Boolean> processDelete(ExecuteQueries executor, long id){
        IMapParameters params = new ParameterMap();
        params.put("id", id);

        try{
            executor.execute(getDeleteSQL(), params);
            ResponseAndError<Long> entityOpt = ResponseAndError.success(id);
            this.postDelete(executor, entityOpt);
            return ResponseAndError.success(true);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error deleting entity", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<Boolean> delete(long id){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processDelete(executor, id);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<Boolean> delete(Connection connection, long id){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processDelete(executor, id);
        }
    }
}
