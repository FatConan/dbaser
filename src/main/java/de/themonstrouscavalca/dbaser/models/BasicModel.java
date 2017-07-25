package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.interfaces.IModel;
import de.themonstrouscavalca.dbaser.utils.TableQualifier;

public abstract class BasicModel implements IModel{
    public String getTablePrefix(){
        return null;
    }

    public String getTablePrefixedFieldName(String fieldName){
        return TableQualifier.fullyQualify(this.getTablePrefix(), fieldName);
    }
}
