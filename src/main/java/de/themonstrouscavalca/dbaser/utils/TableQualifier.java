package de.themonstrouscavalca.dbaser.utils;

/**
 * Created by ian on 25/07/17.
 */
public class TableQualifier{
    public static String fullyQualify(String table, String columnLabel){
        if(table != null && !table.isEmpty()){
            System.out.println("Looking for:" + String.format("%s.%s", table, columnLabel));
            return String.format("%s.%s", table, columnLabel);
        }
        return columnLabel;
    }
}
