package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ParameterMapTest{
    @Test
    public void emptyParams() throws Exception{
        IMapParameters empty1 = ParameterMap.empty();
        IMapParameters empty2 = ParameterMap.empty();

        assertTrue("Empty parameter map is null", empty1 != null);
        assertTrue("Empty parameter map is not empty", empty1.isEmpty());
        assertEquals("Empty parameter maps do not match", empty2, empty1);
        assertTrue("Empty parameter maps do not match", empty1 == empty2);
        empty1.put("TEST", "TEST");
        assertTrue("Empty parameter map is not empty", empty1.isEmpty());
        assertEquals("Empty parameter maps do not match", empty2, empty1);
    }

    @Test
    public void parameterHandling(){
        IMapParameters testMap = new ParameterMap();
        testMap.put("LONG", 1L);
        testMap.put("STRING", "string");
        testMap.put("FLOAT", 1.9);

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
        IMapParameters testMap = new ParameterMap();
        testMap.put("LONG", 1L);
        testMap.put("STRING", "string");
        testMap.put("FLOAT", 1.9);
        Map<String, Object> exportMap = testMap.asMap();
        assertEquals("Exported map of unexpected length", 3, exportMap.size());
        assertTrue("Missing LONG key", exportMap.containsKey("LONG"));
        assertEquals("Values doesn't match original", 1L, exportMap.get("LONG"));
        assertTrue("Missing LONG key", exportMap.containsKey("STRING"));
        assertEquals("Values doesn't match original", "string", exportMap.get("STRING"));
        assertTrue("Missing LONG key", exportMap.containsKey("FLOAT"));
        assertEquals("Values doesn't match original", 1.9, exportMap.get("FLOAT"));
    }
}
