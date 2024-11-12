package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IBasicModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.sql.SQLException;


public abstract class BasicModelDAO<T extends BasicModel> extends BasicModelReadOnlyDAO<T> implements IBasicModelDAO<T>{
    public BasicModelDAO(IProvideConnection connectionProvider){
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


    protected String selectSaveSQL(T entity, boolean forceInsert){
        if(forceInsert){
            return this.getInsertSQL();
        }
        return this.getUpdateSQL();
    }

    @Override
    public ResponseAndError<T> save(T entity, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert), this::postSave);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<T> save(Connection connection, T entity, boolean forceInsert){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSave(executor, entity, this.selectSaveSQL(entity, forceInsert), this::postSave);
        }catch(SQLException | QueryBuilderException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<Boolean> delete(IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), parameters, null, this::postDelete);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<Boolean> delete(Connection connection, IMapParameters parameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processDelete(executor, this.getDeleteSQL(), parameters, null, this::postDelete);
        }
    }
}
