package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.dao.interfaces.identified.IReadDAO;
import de.themonstrouscavalca.dbaser.models.impl.IdentifiedModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ModelReadOnlyDAO<T extends IdentifiedModel> extends BasicModelReadOnlyDAO<T> implements IReadDAO<T>{
    public ModelReadOnlyDAO(IProvideConnection connectionProvider){
        super(connectionProvider);
    }

    @Override
    public ResponseAndError<T> get(Connection connection, long id){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), ParameterMapBuilder.of("id", id).build(), this::create);
        }
    }

    @Override
    public ResponseAndError<T> get(long id){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processingHandlers.processSingle(executor, this.getLookupSQL(), ParameterMapBuilder.of("id", id).build(), this::create);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, err -> logger.error(err.getMessage(), err));
        }
    }
}
