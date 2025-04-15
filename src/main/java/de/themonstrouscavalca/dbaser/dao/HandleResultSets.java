package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandleResultSets<T extends IPopulateFromResultSet> implements IHandleResultSets<T>{
    private Logger logger = LoggerFactory.getLogger(HandleResultSets.class);

    private <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }

    @Override
    public <V> ResponseOrError<List<V>> handleMultipleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler, boolean expectSingleResult, boolean expectedResult){
        List<V> entities = new ArrayList<>();
        if(rsOptional.isPresent()){
            ResultSetTableAware rs = rsOptional.get();
            try{
                while(rs.next()){
                    V entity = handler.process(rs);
                    entities.add(entity);
                }
            }catch(SQLException e){
                QuickResponses.sqlError("Error wrapping result set", e, this::exceptionAction);
            }
        }else{
            return QuickResponses.missing("Unable to get result set");
        }
        if(expectSingleResult && entities.size() > 1){
            return QuickResponses.ambiguous();
        }
        if(entities.isEmpty() && expectedResult){
            return QuickResponses.missing("No matching entity was found in the database");
        }
        return ResponseOrError.success(entities);
    }

    @Override
    public ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator, boolean expectSingleResult, boolean expectedResult){
       return this.handleMultipleSimpleQuery(rsOptional, (rs) -> {
           try {
               T ent = entityGenerator.create();
               ent.populateFromResultSet(rs);
               return ent;
           }catch(SQLException e){
               logger.error("Unable to process result set", e);
               return null;
           }
       }, expectSingleResult, expectedResult);
    }

    @Override
    public ResponseOrError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator){
        return this.handleMultipleResultSets(rsOptional, entityGenerator, DAOConstants.EXPECT_MULTIPLE_RESULTS, DAOConstants.RESULT_NULLABLE);
    }

    @Override
    public ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult){
        ResponseOrError<List<T>> entities = this.handleMultipleResultSets(rsOptional, () -> entity,  DAOConstants.EXPECT_SINGLE_RESULT, expectedResult);
        return this.extractSingleResult(entities);
    }

    @Override
    public <V> ResponseOrError<List<V>> handleMultipleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler){
        return this.handleMultipleSimpleQuery(rsOptional, handler, DAOConstants.EXPECT_MULTIPLE_RESULTS, DAOConstants.RESULT_NULLABLE);
    }

    @Override
    public <V> ResponseOrError<V> handleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler, boolean expectedResult){
        ResponseOrError<List<V>> listedResults = this.handleMultipleSimpleQuery(rsOptional, handler, DAOConstants.EXPECT_SINGLE_RESULT, expectedResult);
        return this.extractSingleResult(listedResults);
    }

    @Override
    public <V> ResponseOrError<V> handleSimpleQuery(ResultSetOptional rsOptional, Proc<V> handler){
        ResponseOrError<List<V>> listedResults = this.handleMultipleSimpleQuery(rsOptional, handler, DAOConstants.EXPECT_SINGLE_RESULT, DAOConstants.RESULT_EXPECTED);
        return this.extractSingleResult(listedResults);
    }

    @Override
    public ResponseOrError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity){
        return this.handleSingleResultSet(rsOptional, entity, DAOConstants.RESULT_EXPECTED);
    }
}
