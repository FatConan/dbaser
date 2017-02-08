package de.themonstrouscavalca.dbaser.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ResultSetChecker{
    private Set<String> columns = new HashSet<>();

    public ResultSetChecker(ResultSet rs) throws SQLException{
        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        for (int i = 1; i < numberOfColumns + 1; i++) {
            String columnName = rsMetaData.getColumnLabel(i);
            this.columns.add(columnName);
        }

        for (int i = 1; i < numberOfColumns + 1; i++) {
            String columnName = rsMetaData.getColumnName(i);
            this.columns.add(columnName);
        }
    }

    public boolean has(String columnName){
        return columns.contains(columnName);
    }
}

