package de.themonstrouscavalca.dbaser.models.impl;

import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class IdentifiedModel extends BasicModel implements IUniquelyModel{
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

    protected ParameterMapBuilder exportMapBuilder(){
        return new ParameterMapBuilder().add("id", this.id);
    }

    protected void idFromResultSet(ResultSetTableAware rs) throws SQLException{
        this.longFieldFromRS("id", rs, this::setId);
    }

    public abstract void populateFromResultSet(ResultSetTableAware rs) throws SQLException;
}
