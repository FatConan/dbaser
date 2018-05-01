package de.themonstrouscavalca.dbaser.models.impl;

import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class BasicIdentifiedModel extends BasicModel implements IUniquelyModel{
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

    public void populateFromResultSet(ResultSetTableAware rs) throws SQLException{
        if(rs.has(this.getTablePrefixedFieldName("id"))){
            this.setId(rs.getLong(this.getTablePrefixedFieldName("id")));
        }
        this.setRemainderFromResultSet(rs);
    }

    protected abstract void setRemainderFromResultSet(ResultSetTableAware rs) throws SQLException;

}
