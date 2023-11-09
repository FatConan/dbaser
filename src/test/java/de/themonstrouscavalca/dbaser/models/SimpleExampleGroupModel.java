package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.models.interfaces.fields.IPullFromResultSet;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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
    public void fieldFromRS(String field, ResultSetTableAware rs, IPullFromResultSet handler) throws SQLException{
        String f = this.getTablePrefixedFieldName(field);
        super.fieldFromRS(f, rs, handler);
    }

    @Override
    public IMapParameters exportToMap(){
        IMapParameters exportMap = this.baseExportToMap();
        exportMap.put("name", this.getName());
        return exportMap;
    }

    @Override
    protected void setRemainderFromResultSet(ResultSetTableAware rs) throws SQLException{
        this.stringFieldFromRS("name", rs, this::setName);
    }
}
