package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.dao.SimpleExampleUserDAO;
import de.themonstrouscavalca.dbaser.models.EmptyModel;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * These tests are designed to show how a simpleDAO implementation would work.
 */
public class TestSimpleDAO extends BaseTest{
    private final SimpleExampleUserDAO dao = new SimpleExampleUserDAO();

    @Test
    public void testEmptyModel(){
        EmptyModel emptyModel = new EmptyModel();
        assertNull("Basic model doesn't have null prefix", emptyModel.getTablePrefix());
    }

    @Test
    public void testSimpleExampleUserModel(){
        SimpleExampleUserModel erica = new SimpleExampleUserModel();
        erica.setId(5L); //We're going to force the insert here
        erica.setName("Erica");
        erica.setJobTitle("Engineer");
        erica.setAge(30);

        erica.setTableAwarenessEnabled(false);
        assertFalse("Table awareness is not set correctly", erica.isTableAwarenessEnabled());
        erica.setTableAwarenessEnabled(true);
        assertTrue("Table awareness is not set correctly", erica.isTableAwarenessEnabled());

        assertEquals("Table prefix is not correct", "users", erica.getTablePrefix());

        assertEquals("Prefixed field is not correct", "users.id", erica.getTablePrefixedFieldName("id"));
        assertEquals("Prefixed field is not correct", "users.name", erica.getTablePrefixedFieldName("name"));
        assertEquals("Prefixed field is not correct", "users.age", erica.getTablePrefixedFieldName("age"));
    }

    @Test
    public void testDAOList(){
        //private static final String ADD_USERS = " INSERT INTO users (id, name, job_title, age) " +
        //" VALUES (1, 'Alice', 'Architect', 30), (2, 'Bob', 'Banker', 47), " +
        //        " (3, 'Claudia', 'Commissioner', 28), (4, 'Derek', 'Dentist', 52)";
        List<SimpleExampleUserModel> models = dao.getList();
        SimpleExampleUserModel model = models.get(0);
        assertEquals("Model doesn't match expectation", Long.valueOf(1L), model.getId());
        assertEquals("Model doesn't match expectation", "Alice", model.getName());
        assertEquals("Model doesn't match expectation", "Architect", model.getJobTitle());
        assertEquals("Model doesn't match expectation", Integer.valueOf(30), model.getAge());

        model = models.get(1);
        assertEquals("Model doesn't match expectation", Long.valueOf(2L), model.getId());
        assertEquals("Model doesn't match expectation", "Bob", model.getName());
        assertEquals("Model doesn't match expectation", "Banker", model.getJobTitle());
        assertEquals("Model doesn't match expectation", Integer.valueOf(47), model.getAge());

        model = models.get(2);
        assertEquals("Model doesn't match expectation", Long.valueOf(3L), model.getId());
        assertEquals("Model doesn't match expectation", "Claudia", model.getName());
        assertEquals("Model doesn't match expectation", "Commissioner", model.getJobTitle());
        assertEquals("Model doesn't match expectation", Integer.valueOf(28), model.getAge());
    }

    @Test
    public void testDAOInsert(){
        SimpleExampleUserModel erica = new SimpleExampleUserModel();
        erica.setId(5L); //We're going to force the insert here
        erica.setName("Erica");
        erica.setJobTitle("Engineer");
        erica.setAge(30);

        dao.save(erica, true); //Force the insert because we're not using a sequence to provide keys
        List<SimpleExampleUserModel> models = dao.getList();
        assertEquals("Erica hasn't been added to the database", 5, models.size());

        IMapParameters lookup = new ParameterMap();
        lookup.put("id", 5);
        erica = dao.get(lookup);
        assertNotNull(erica);
        assertEquals("erica name does not match", "Erica", erica.getName());
        assertEquals("erica job title doesn't match", "Engineer", erica.getJobTitle());
        assertEquals("erica age doesn't match", Integer.valueOf(30), erica.getAge());
    }

    @Test
    public void testDAOUpdate(){
        IMapParameters lookup = new ParameterMap();
        lookup.put("id", 3);

        SimpleExampleUserModel erica = dao.get(lookup);
        erica.setName("Fred");
        erica.setJobTitle("Fireman");

        dao.save(erica);

        erica = dao.get(lookup);
        assertNotNull(erica);
        assertEquals("fred name does not match", "Fred", erica.getName());
        assertEquals("fred job title doesn't match", "Fireman", erica.getJobTitle());
        assertEquals("fred age doesn't match", Integer.valueOf(28), erica.getAge());
    }

    @Test
    public void testDAOdelete(){
        IMapParameters lookup = new ParameterMap();
        lookup.put("id", 4);
        dao.delete(lookup);

        SimpleExampleUserModel wasDerek = dao.get(lookup);
        assertNull(wasDerek.getId());
    }

    @Test
    public void testDAOGetGroups(){
        Map<Long, List<Long>> userGroupMap = new HashMap<>();
        userGroupMap.put(1L, Arrays.asList(1L, 2L));
        userGroupMap.put(2L, Arrays.asList(3L));
        userGroupMap.put(3L, Arrays.asList(4L));
        userGroupMap.put(4L, Arrays.asList(1L, 2L, 3L, 4L));

        Collection<SimpleExampleUserModel> entries = dao.getUsersAndGroups();
        assertEquals("Entry count mismatch", 4, entries.size());
        for(SimpleExampleUserModel entry: entries){
            List<Long> expectedGroups = userGroupMap.get(entry.getId());
            assertEquals("Group count mismatch", expectedGroups.size(), entry.getGroups().size());
        }
    }
}
