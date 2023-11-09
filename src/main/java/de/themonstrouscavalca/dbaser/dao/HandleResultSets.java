package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.dbaser.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

public class HandleResultSets<T extends IPopulateFromResultSet> implements IHandleResultSets<T>{
    protected Logger logger = LoggerFactory.getLogger(HandleResultSets.class);

    @Override
    public Optional<T> handleResultSet(ResultSetOptional rsOptional, T entity){
        if(rsOptional.isPresent()){
            ResultSetTableAware rs = rsOptional.get();
            try{
                if(rs.next()){
                    entity.populateFromResultSet(rs);
                }else{
                    return Optional.empty();
                }
            }catch(SQLException e){
                logger.error("Error wrapping result set", e);
                return Optional.empty();
            }
        }else{
            return Optional.empty();
        }
        return Optional.ofNullable(entity);
    }
}
