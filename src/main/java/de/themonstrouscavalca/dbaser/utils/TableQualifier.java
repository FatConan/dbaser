package de.themonstrouscavalca.dbaser.utils;

public class TableQualifier{
    private static final String QUALIFIED_NAME_FORMAT = "%s.%s";

    public static String fullyQualify(String table, String columnLabel, boolean enabled){
        if(enabled){
            return fullyQualify(table, columnLabel);
        }
        return fullyQualify(null, columnLabel);
    }

    public static String fullyQualify(String table, String columnLabel){
        if(table != null && !table.isEmpty()){
            return String.format(QUALIFIED_NAME_FORMAT, table, columnLabel);
        }
        return columnLabel;
    }
}
