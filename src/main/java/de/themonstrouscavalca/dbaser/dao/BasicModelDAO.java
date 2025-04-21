package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IBasicModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

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


    protected void postSave(ExecuteQueries executor, ResponseOrError<T> entityOptional){
        //Override this in sub classes to perform post save hooks
    }

    protected void postDelete(ExecuteQueries executor, ResponseOrError<Long> entityOptional){
        //Override this in sub classes to perform post save hooks
    }


    protected String selectSaveSQL(T entity, boolean forceInsert){
        if(forceInsert){
            return this.getInsertSQL();
        }
        return this.getUpdateSQL();
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
}
