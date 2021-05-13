package de.themonstrouscavalca.dbaser.models.impl;

import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
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

    public boolean hasId(){
        return this.getId() != null && this.getId() > 0;
    }

    public void setId(Long id){
        this.id = id;
    }

    protected IMapParameters baseExportToMap(){
        ParameterMap params = new ParameterMap();
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
