package de.themonstrouscavalca.dbaser.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResultSetChecker{
    Logger logger = LoggerFactory.getLogger(ResultSetChecker.class);

    private Set<String> columns = new HashSet<>();
    private Map<String, Integer> columnMap = new HashMap<>();

    public ResultSetChecker(ResultSet rs, Boolean tableQualified) throws SQLException{
        if(rs != null){
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            for(int i = 1; i < numberOfColumns + 1; i++){
                String columnLabel = rsMetaData.getColumnLabel(i);
                String tableName = rsMetaData.getTableName(i);
                this.columnMap.put(columnLabel, i);
                if(tableQualified){
                    this.columnMap.put(TableQualifier.fullyQualify(tableName, columnLabel), i);
                }
            }

            for(int i = 1; i < numberOfColumns + 1; i++){
                String columnName = rsMetaData.getColumnName(i);
                String tableName = rsMetaData.getTableName(i);
                this.columnMap.put(columnName, i);
                if(tableQualified){
                    this.columnMap.put(TableQualifier.fullyQualify(tableName, columnName), i);
                }
            }

            this.columns = this.columnMap.keySet();
        }
    }

    public ResultSetChecker(ResultSet rs) throws SQLException{
        this(rs, Boolean.TRUE);
    }

    /* This is now just a compatibility wrapper for seek */
    public boolean has(String columnName){
        return seek(columnName);
    }

    public Boolean seek(String tableQualifiedField){
        Boolean found = this.columns.contains(tableQualifiedField);
        logger.debug("Looking for {} and found {}", tableQualifiedField, found);
        return found;
    }

    public Integer resolve(String tableQualifiedField){
        Integer columnId = this.columnMap.getOrDefault(tableQualifiedField, null);
        logger.debug("Resolved {} to row {}", tableQualifiedField, columnId);
        return columnId;
    }
}

