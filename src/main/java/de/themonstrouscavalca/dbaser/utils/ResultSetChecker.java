package de.themonstrouscavalca.dbaser.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ResultSetChecker{
    private Set<String> columns = new HashSet<>();

    public ResultSetChecker(ResultSet rs, Boolean tableQualified) throws SQLException{
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        for(int i = 1; i < numberOfColumns + 1; i++){
            String columnLabel = rsMetaData.getColumnLabel(i);
            String tableName = rsMetaData.getTableName(i);
            this.columns.add(columnLabel);
            if(tableQualified){
                this.columns.add(TableQualifier.fullyQualify(tableName, columnLabel));
            }
        }

        for(int i = 1; i < numberOfColumns + 1; i++){
            String columnName = rsMetaData.getColumnName(i);
            String tableName = rsMetaData.getTableName(i);
            this.columns.add(columnName);
            if(tableQualified){
                this.columns.add(TableQualifier.fullyQualify(tableName, columnName));
            }
        }
    }

    public ResultSetChecker(ResultSet rs) throws SQLException{
        this(rs, Boolean.TRUE);
    }

    public boolean has(String columnName){
        return columns.contains(columnName);
    }
}

