package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.dao.interfaces.IReadDAO;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class BasicModelReadOnlyDAO<T extends BasicModel> implements IReadDAO<T>{
    protected final HandleResultSets<T> handler = new HandleResultSets<>();
    protected Logger logger = LoggerFactory.getLogger(BasicModelReadOnlyDAO.class);
    protected <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }

    protected final IProvideConnection connectionProvider;

    protected BasicModelReadOnlyDAO(IProvideConnection connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    @Override
    public IProvideConnection getConnectionProvider(){
        return this.connectionProvider;
    }

    protected abstract String getLookupSQL();

    protected abstract String getListSQL();

    public abstract T create();

    //region Overriden Interface Methods
    protected ResponseAndError<List<T>> processList(ExecuteQueries executor, String sql, IMapParameters parameters,
                                                    boolean expectSingleResult, boolean expectingResult){
        try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
            return handler.handleMultipleResultSets(rso, this::create, expectSingleResult, expectingResult);
        }catch(QueryBuilderException | SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(String sql, IMapParameters listingParameters, boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processList(executor,sql, listingParameters, expectSingleResult, expectingResult);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(Connection connection, String sql, IMapParameters listingParameters,
                                          boolean expectSingleResult, boolean expectingResult){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processList(executor, sql, listingParameters, expectSingleResult, expectingResult);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processList(executor, this.getListSQL(), listingParameters, false, false);
        }catch(SQLException e){
            return QuickResponses.sqlError("Error listing entities", e, this::exceptionAction);
        }
    }

    @Override
    public ResponseAndError<List<T>> find(Connection connection, IMapParameters listingParameters){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processList(executor, this.getListSQL(), listingParameters, false, false);
        }
    }
    //endregion

    //region Result set processing for multiple results
    protected ResponseAndError<T> processSingle(ExecuteQueries executor, String sql, IMapParameters parameters){
        try(ResultSetOptional rso = executor.executeQuery(sql, parameters)){
            return handler.handleSingleResultSet(rso, this.create());
        }catch(QueryBuilderException | SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, err -> logger.error(err.getMessage(), err));
        }
    }

    @Override
    public ResponseAndError<T> get(Connection connection, long id){
        try(ExecuteQueries executor = new ExecuteQueries(connection)){
            return this.processSingle(executor, this.getLookupSQL(), ParameterMapBuilder.of("id", id).build());
        }
    }

    @Override
    public ResponseAndError<T> get(long id){
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            return this.processSingle(executor, this.getLookupSQL(), ParameterMapBuilder.of("id", id).build());
        }catch(SQLException e){
            return QuickResponses.sqlError("Error fetching entity", e, err -> logger.error(err.getMessage(), err));
        }
    }
    //endregion

    protected ResponseAndError<Long> count(String sql, IMapParameters params){
        long total = 0L;
        try(ExecuteQueries executor = new ExecuteQueries(this.connectionProvider)){
            try (ResultSetOptional rso = executor.executeQuery(sql, params)) {
                if (rso.isPresent()){
                    ResultSet rs = rso.get();
                    if(rs.next()){
                        total = rs.getLong("total");
                    }
                }
            }
        }catch(SQLException | QueryBuilderException e) {
            return QuickResponses.sqlError("Error counting entities", e, this::exceptionAction);
        }
        return ResponseAndError.success(total);
    }
}

