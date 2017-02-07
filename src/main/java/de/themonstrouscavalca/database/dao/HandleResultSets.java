package de.themonstrouscavalca.database.dao;

import de.themonstrouscavalca.database.dao.interfaces.IHandleResultSets;
import de.themonstrouscavalca.database.models.interfaces.IPopulateFromResultSet;
import de.themonstrouscavalca.database.utils.ResultSetOptional;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HandleResultSets<T extends IPopulateFromResultSet> implements IHandleResultSets<T>{
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

            }
        }
        return entity;
    }
}
