package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CollectedParameterMapsTest{
    @Test
    public void parameterHandling(){
        IMapParameters testMap = new ParameterMap();
        testMap.put("LONG", 1L);
        testMap.put("STRING", "string");
        testMap.put("FLOAT", 1.9);

        IMapParameters testMap2 = new ParameterMap();
        testMap2.put("LONG", 4L);
        testMap2.put("STRING", "string2");
        testMap2.put("FLOAT", 67.3);

        IMapParameters testMap3 = new ParameterMap();
        testMap3.put("LONG", 19789L);
        testMap3.put("STRING", "string3");
        testMap3.put("FLOAT", 0.0002);

        CollectedParameterMaps collectedParameterMaps = new CollectedParameterMaps();
        collectedParameterMaps.add(testMap);
        collectedParameterMaps.add(testMap2);
        collectedParameterMaps.add(testMap3);

        List<IMapParameters> parameters = (List<IMapParameters>) collectedParameterMaps.get();

        assertNotNull("Null collection returned", parameters);
        assertEquals("Collected maps length differs", 3, parameters.size());
        assertEquals("Values do not match", testMap, parameters.get(0));
        assertEquals("Values do not match", testMap2, parameters.get(1));
        assertEquals("Values do not match", testMap3, parameters.get(2));
    }

    @Test
    public void parameterMassHandling(){
        IMapParameters testMap = new ParameterMap();
        testMap.put("LONG", 1L);
        testMap.put("STRING", "string");
        testMap.put("FLOAT", 1.9);

        IMapParameters testMap2 = new ParameterMap();
        testMap2.put("LONG", 4L);
        testMap2.put("STRING", "string2");
        testMap2.put("FLOAT", 67.3);

        IMapParameters testMap3 = new ParameterMap();
        testMap3.put("LONG", 19789L);
        testMap3.put("STRING", "string3");
        testMap3.put("FLOAT", 0.0002);

        CollectedParameterMaps collectedParameterMaps = CollectedParameterMaps.of(Arrays.asList(testMap, testMap2, testMap3));

        List<IMapParameters> parameters = (List<IMapParameters>) collectedParameterMaps.get();

        assertNotNull("Null collection returned", parameters);
        assertEquals("Collected maps length differs", 3, parameters.size());
        assertEquals("Values do not match", testMap, parameters.get(0));
        assertEquals("Values do not match", testMap2, parameters.get(1));
        assertEquals("Values do not match", testMap3, parameters.get(2));
    }
}
