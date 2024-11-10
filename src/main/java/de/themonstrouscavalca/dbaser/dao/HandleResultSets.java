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
    protected Logger logger = LoggerFactory.getLogger(HandleResultSets.class);
    protected <E extends Exception> void exceptionAction(E err){
        logger.error(err.getMessage(), err);
    }

    @Override
    public ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity, boolean expectedResult){
        if(rsOptional.isPresent()){
            ResultSetTableAware rs = rsOptional.get();
            try{
                if(rs.next()){
                    entity.populateFromResultSet(rs);
                }else{
                    if(expectedResult){
                        return QuickResponses.missing("No matching entity was found in the database");
                    }
                    return QuickResponses.noResult();
                }
            }catch(SQLException e){
                QuickResponses.sqlError("Error wrapping result set", e, this::exceptionAction);
            }
        }else{
            return QuickResponses.missing("Unable to get result set");
        }
        return ResponseAndError.success(entity);
    }

    @Override
    public ResponseAndError<T> handleSingleResultSet(ResultSetOptional rsOptional, T entity){
        return this.handleSingleResultSet(rsOptional, entity, true);
    }

    @Override
    public ResponseAndError<List<T>> handleMultipleResultSets(ResultSetOptional rsOptional, Gen<T> entityGenerator){
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
        return ResponseAndError.success(entities);
    }
}
