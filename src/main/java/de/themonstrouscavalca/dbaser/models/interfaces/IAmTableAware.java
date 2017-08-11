package de.themonstrouscavalca.dbaser.models.interfaces;

public interface IAmTableAware{
    boolean isTableAwarenessEnabled();
    void setTableAwarenessEnabled(boolean enabled);

    String getTablePrefix();
    String getTablePrefixedFieldName(String fieldName);
}
