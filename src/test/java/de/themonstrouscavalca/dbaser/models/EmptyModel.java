package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class EmptyModel extends BasicModel{
    @Override
    public Map<String, Object> exportToMap(){
        return null;
    }

    @Override
    public void populateFromResultSet(ResultSet rs) throws SQLException{

    }
}
