package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.interfaces.IModel;

public abstract class BasicModel implements IModel{
    private final String TABLE_PREFIX = "";

    public String getTablePrefixedFieldName(String fieldName){
        if(this.TABLE_PREFIX != null && !this.TABLE_PREFIX.isEmpty()){
            return String.format("%s.%s", this.TABLE_PREFIX, fieldName);
        }
        return fieldName;
    }
}
