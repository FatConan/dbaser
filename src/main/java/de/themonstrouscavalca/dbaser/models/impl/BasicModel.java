package de.themonstrouscavalca.dbaser.models.impl;

import de.themonstrouscavalca.dbaser.models.interfaces.IModel;
import de.themonstrouscavalca.dbaser.utils.TableQualifier;

public abstract class BasicModel implements IModel{
    private boolean tableAwarenessEnabled = true;

    public boolean isTableAwarenessEnabled(){
        return this.tableAwarenessEnabled;
    }

    public void setTableAwarenessEnabled(boolean enabled){
        this.tableAwarenessEnabled = enabled;
    }

    public String getTablePrefix(){
        return null;
    }

    public String getTablePrefixedFieldName(String fieldName){
        return TableQualifier.fullyQualify(this.getTablePrefix(), fieldName, this.isTableAwarenessEnabled());
    }
}
