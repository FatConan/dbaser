package de.themonstrouscavalca.dbaser.utils;

public class TableQualifier{
    public static String fullyQualify(String table, String columnLabel){
        if(table != null && !table.isEmpty()){
            return String.format("%s.%s", table, columnLabel);
        }
        return columnLabel;
    }
}
