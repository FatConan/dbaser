package de.themonstrouscavalca.database.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ModelExample extends BasicModel {
    public Map<String, Object> replacementParameters(){
        return null;
    }

    public void setFromResultSet(ResultSet rs) throws SQLException{

    }
}
