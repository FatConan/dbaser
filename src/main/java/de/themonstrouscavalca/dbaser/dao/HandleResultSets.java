package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ProcessingErrorType;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HandleResultSets<T extends IPopulateFromResultSet> implements IHandleResultSets<T>{
    private Logger logger = LoggerFactory.getLogger(HandleResultSets.class);

    private <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }

    @Override
    public ResponseAndError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator, boolean expectSingleResult, boolean expectedResult){
        List<T> entities = new ArrayList<>();
        if(rsOptional.isPresent()){
            ResultSetTableAware rs = rsOptional.get();
            try{
                while(rs.next()){
                    T entity = entityGenerator.create();
                    entity.populateFromResultSet(rs);
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
        return ResponseAndError.success(entities);
    }

    @Override
    public ResponseAndError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator){
        return this.handleMultipleResultSets(rsOptional, entityGenerator, false, false);
    }

    @Override
    public ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult){
        ResponseAndError<List<T>> entities = this.handleMultipleResultSets(rsOptional, () -> entity, true, expectedResult);
        return this.extractSingleResult(entities);
    }


    @Override
    public ResponseAndError<T> extractSingleResult(ResponseAndError<List<T>> listedResults){
        if(listedResults.isSuccess()){
            List<T> ents = listedResults.response().orElse(Collections.emptyList());
            if(ents.size() == 1){
                return ResponseAndError.success(ents.getFirst());
            }
        }
        return QuickResponses.repackageError(listedResults);
    }

    @Override
    public ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity){
        return this.handleSingleResultSet(rsOptional, entity, true);
    }


}
