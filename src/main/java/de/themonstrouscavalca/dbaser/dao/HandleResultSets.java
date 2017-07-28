package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HandleResultSets<T extends IPopulateFromResultSet> implements IHandleResultSets<T>{
    protected Logger logger = LoggerFactory.getLogger(HandleResultSets.class);

    @Override
    public T handleResultSet(ResultSetOptional rsOptional, T entity){
        if(rsOptional.isPresent()){
            ResultSet rs = rsOptional.get();
            try{
                if(rs.next()){
                    entity.populateFromResultSet(rs);
                }else{
                    /*entity.setErrorStatus(DBErrorStatus.NOT_FOUND);
                    entity.setError(errorMsg);*/
                }
            }catch(SQLException e){
                logger.error("Error wrapping result set", e);
            }
        }
        return entity;
    }
}
