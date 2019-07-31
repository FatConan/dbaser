package de.themonstrouscavalca.dbaser.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class TableQualifierTest{
    @Test
    public void fullyQualify() throws Exception{
        String tableName = "TABLE";
        String columnName = "COLUMN";
        assertEquals("Full table name qualification doesn't match", "TABLE.COLUMN", TableQualifier.fullyQualify(tableName, columnName));
        assertEquals("Full table name qualification doesn't match", "TABLE.COLUMN", TableQualifier.fullyQualify(tableName, columnName, true));
        assertEquals("Incorrectly qualifying with table name when disabled", "COLUMN", TableQualifier.fullyQualify(tableName, columnName, false));
        assertEquals("Incorrectly qualifying with table name when table is null", "COLUMN", TableQualifier.fullyQualify(null, columnName));
    }
}