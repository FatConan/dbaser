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
    private Logger logger = LoggerFactory.getLogger(ResultSetChecker.class);

    /* Internal maps and sets for seeking and resolving column names*/
    private Set<String> columns = new HashSet<>();
    private Map<String, Integer> columnMap = new HashMap<>();

    /**
     * The ResultSetChecker provides functionality for mapping table-qualified names to their indexes and offering methods
     * for testing for their presence from ResultSet instances.
     * @param rs The ResultSet to check
     * @param tableQualified A toggle for determining whether a table-qualified version of the mapping should be generated
     * @throws SQLException Bubbling possible SQLExceptions from the metadata calls on the provided ResultSet
     */
    public ResultSetChecker(ResultSet rs, Boolean tableQualified) throws SQLException{
        if(rs != null){
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            /*Loop through the column labels and add them and (if requested) their table-qualified versions to the
            the map alongside the current column index */
            for(int i = 1; i < numberOfColumns + 1; i++){
                String columnLabel = rsMetaData.getColumnLabel(i);
                String tableName = rsMetaData.getTableName(i);
                this.addColumnRecord(columnLabel, i);
                if(tableQualified){
                    this.addColumnRecord(TableQualifier.fullyQualify(tableName, columnLabel), i);
                }
            }

            /*Loop through the column names and add them and (if requested) their table-qualified versions to the
            the map alongside the current column index */
            for(int i = 1; i < numberOfColumns + 1; i++){
                String columnName = rsMetaData.getColumnName(i);
                String tableName = rsMetaData.getTableName(i);
                this.addColumnRecord(columnName, i);
                if(tableQualified){
                    this.addColumnRecord(TableQualifier.fullyQualify(tableName, columnName), i);
                }
            }

            /* Grab the key set and store it internally so we can quickly test existence */
            this.columns = this.columnMap.keySet();
        }
    }

    /**
     * Wrap a ResultSet with the ResultSet checker functionality. By default the table-qualified name mapping will be used.
     * @param rs The ResultSet to check
     * @throws SQLException Bubbling possible SQLExceptions from the metadata calls on the provided ResultSet
     */
    public ResultSetChecker(ResultSet rs) throws SQLException{
        this(rs, Boolean.TRUE);
    }

    /**
     * Add a record to the map, if an entry for the provided identifier (a column name or label, fully qualified for otherwise)
     * already exists do nothing, otherwise add the mapping entry.
     * @param identifier A string column name
     * @param index The index of the corresponding column
     */
    private void addColumnRecord(String identifier, Integer index){
        if(!this.columnMap.containsKey(identifier)){
            this.columnMap.put(identifier, index);
        }
    }

    /**
     * An alias for seek to preserve backwards compatibility
     * @param columnName A String column name or label (table-qualified or otherwise) to check for existence in the map
     * @return A boolean result of that check (true indicates existence)
     */
    public boolean has(String columnName){
        return seek(columnName);
    }

    /**
     * Check for the existence of the provided column name or label (table-qualified or otherwise) in the prepared map of columns from the
     * provided ResultSet.
     * @param tableQualifiedField A String column name or label (table-qualified or otherwise) to check for existence in the map
     * @return A boolean result of that check (true indicates existence)
     */
    public Boolean seek(String tableQualifiedField){
        Boolean found = this.columns.contains(tableQualifiedField);
        logger.debug("Looking for {} and found {}", tableQualifiedField, found);
        return found;
    }

    /** Returns the integer index of a provided column name or label (table-qualified or otherwise) or null should one not exist.
     *
     * @param tableQualifiedField A column name or label (table-qualified or otherwise)
     * @return the corresponding Integer column index
     */
    public Integer resolve(String tableQualifiedField){
        Integer columnId = this.columnMap.getOrDefault(tableQualifiedField, null);
        logger.debug("Resolved {} to row {}", tableQualifiedField, columnId);
        return columnId;
    }
}

