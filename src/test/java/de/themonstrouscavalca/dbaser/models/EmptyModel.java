package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class EmptyModel extends BasicModel{
    @Override
    public IMapParameters exportToMap(){
        return ParameterMap.empty();
    }

    @Override
    public void populateFromResultSet(ResultSetTableAware rs) throws SQLException{

    }
}
