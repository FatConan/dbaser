package de.themonstrouscavalca.dbaser.dao;


import de.themonstrouscavalca.dbaser.dao.interfaces.IProcessingHandlers;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.IdentifiedModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ModelDAO<T extends IdentifiedModel> extends ModelReadOnlyDAO<T> implements IModelDAO<T>{
    public ModelDAO(IProvideConnection db){
        super(db);
    }

    protected abstract String getUpdateSQL();

    protected abstract String getInsertSQL();

    protected abstract String getDeleteSQL();

    public abstract T create();

    protected IProcessingHandlers.ExecutorCall<T> defaultExecutorCall(){
        return this.processingHandlers.defaultExecutorCall();
    }

    protected void postSave(ExecuteQueries executor, ResponseOrError<T> entityOptional){
        //Override this in sub classes to perform post save hooks
    }

    protected void postDelete(ExecuteQueries executor, ResponseOrError<Long> entityOptional){
        //Override this in sub classes to perform post save hooks
    }

    protected String selectSaveSQL(T entity, boolean forceInsert){
        if(entity.hasId() && !forceInsert){
            return this.getUpdateSQL();
        }
        return this.getInsertSQL();
    }

    /** Save mechanisms
     *
     *
     * **/
    @Override
    public ResponseOrError<T> save(T entity, IProcessingHandlers.ExecutorCall<T> executorCall, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert), executorCall, this::postSave);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error saving entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<T> save(Connection connection, T entity, IProcessingHandlers.ExecutorCall<T> executorCall, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert), executorCall, this::postSave);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error saving entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<T> save(T entity, boolean forceInsert){
        return this.save(entity, this.defaultExecutorCall(), forceInsert);
    }

    @Override
    public ResponseOrError<T> save(Connection connection, T entity, boolean forceInsert){
        return this.save(connection, entity, this.defaultExecutorCall(), forceInsert);
    }

    @Override
    public ResponseOrError<T> save(T entity){
       return this.save(entity, false);
    }

    @Override
    public ResponseOrError<T> save(Connection connection, T entity){
        return this.save(connection, entity, false);
    }


    @Override
    public ResponseOrError<Boolean> delete(IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), parameters, null, this::postDelete);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error deleting entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<Boolean> delete(Connection connection, IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), parameters, null, this::postDelete);
        }
    }

    @Override
    public ResponseOrError<Boolean> delete(long id){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), ParameterMapBuilder.of("id", id).build(), id, this::postDelete);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error deleting entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseOrError<Boolean> delete(Connection connection, long id){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), ParameterMapBuilder.of("id", id).build(), id, this::postDelete);
        }
    }
}
