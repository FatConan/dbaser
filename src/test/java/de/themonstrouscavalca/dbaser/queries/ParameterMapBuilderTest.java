package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ParameterMapBuilderTest{
    @Test
    public void parameterHandling(){
        IMapParameters testMap = (new ParameterMapBuilder())
            .add("LONG", 1L)
            .add("STRING", "string")
            .add("FLOAT", 1.9)
            .build();

        assertFalse("Parameter map is empty", testMap.isEmpty());
        assertTrue("Missing LONG key", testMap.has("LONG"));
        assertEquals("Values doesn't match original", 1L, testMap.get("LONG"));
        assertTrue("Missing LONG key", testMap.has("STRING"));
        assertEquals("Values doesn't match original", "string", testMap.get("STRING"));
        assertTrue("Missing LONG key", testMap.has("FLOAT"));
        assertEquals("Values doesn't match original", 1.9, testMap.get("FLOAT"));
        assertFalse("Found a key that shouldn't exist", testMap.has("MISS"));
    }

    @Test
    public void parameterExporting(){
        IMapParameters testMap = (new ParameterMapBuilder())
                .add("LONG", 1L)
                .add("STRING", "string")
                .add("FLOAT", 1.9)
                .build();

        Map<String, Object> exportMap = testMap.asMap();
        assertEquals("Exported map of unexpected length", 3, exportMap.size());
        assertTrue("Missing LONG key", exportMap.containsKey("LONG"));
        assertEquals("Values doesn't match original", 1L, exportMap.get("LONG"));
        assertTrue("Missing LONG key", exportMap.containsKey("STRING"));
        assertEquals("Values doesn't match original", "string", exportMap.get("STRING"));
        assertTrue("Missing LONG key", exportMap.containsKey("FLOAT"));
        assertEquals("Values doesn't match original", 1.9, exportMap.get("FLOAT"));
    }

    @Test
    public void parameterExportingUsingOf(){
        IMapParameters testMap = ParameterMapBuilder
                .of("LONG", 1L)
                .add("STRING", "string")
                .add("FLOAT", 1.9)
                .build();

        assertFalse("Parameter map is empty", testMap.isEmpty());
        assertTrue("Missing LONG key", testMap.has("LONG"));
        assertEquals("Values doesn't match original", 1L, testMap.get("LONG"));
        assertTrue("Missing LONG key", testMap.has("STRING"));
        assertEquals("Values doesn't match original", "string", testMap.get("STRING"));
        assertTrue("Missing LONG key", testMap.has("FLOAT"));
        assertEquals("Values doesn't match original", 1.9, testMap.get("FLOAT"));
        assertFalse("Found a key that shouldn't exist", testMap.has("MISS"));
    }
}
