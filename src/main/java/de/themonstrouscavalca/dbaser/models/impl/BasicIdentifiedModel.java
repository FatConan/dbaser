package de.themonstrouscavalca.dbaser.models.impl;

import de.themonstrouscavalca.dbaser.models.BasicModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicIdentifiedModel extends BasicModel{
    private Long id;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    protected Map<String, Object> baseExportToMap(){
        Map<String, Object> params = new HashMap<>();
        params.put("id", this.id);
        return params;
    }

    public void populateFromResultSet(ResultSet rs) throws SQLException{
        ResultSetChecker checker = new ResultSetChecker(rs);
        if(checker.has("id")){
            this.setId(rs.getLong("id"));
        }
        this.setRemainderFromResultSet(checker, rs);
    }

    protected abstract void setRemainderFromResultSet(ResultSetChecker checker, ResultSet rs) throws SQLException;

}