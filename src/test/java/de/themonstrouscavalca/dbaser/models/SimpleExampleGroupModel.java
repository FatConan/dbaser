package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by ian on 24/07/17.
 */
public class SimpleExampleGroupModel extends BasicIdentifiedModel{
    private final String TABLE_PREFIX = "groups";

    @Override
    public String getTablePrefix(){
        return TABLE_PREFIX;
    }

    private String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public Map<String, Object> exportToMap(){
        Map<String, Object> exportMap = this.baseExportToMap();
        exportMap.put("name", this.getName());
        return exportMap;
    }

    @Override
    protected void setRemainderFromResultSet(ResultSetChecker checker, ResultSet rs) throws SQLException{
        if(checker.has(this.getTablePrefixedFieldName("name"))){
            this.setName(rs.getString(this.getTablePrefixedFieldName("name")));
        }
    }
}
