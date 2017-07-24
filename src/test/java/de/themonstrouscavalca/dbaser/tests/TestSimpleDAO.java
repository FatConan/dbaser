package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.dao.SimpleExampleUserDAO;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import org.junit.Test;

import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * These tests are designed to show how a simpleDAO implementation would work.
 */
public class TestSimpleDAO extends BaseTest{
    private final SimpleExampleUserDAO dao = new SimpleExampleUserDAO();

    @Test
    public void testDAOInsert(){
        SimpleExampleUserModel erica = new SimpleExampleUserModel();
        erica.setId(5L); //We're going to force the insert here
        erica.setName("Erica");
        erica.setJobTitle("Engineer");
        erica.setAge(30);

        dao.save(erica, true); //Force the insert because we're not using a sequence to provide keys
        List<SimpleExampleUserModel> models = dao.getList(new HashMap<>()); //Need to add a parameterless version too
        assertEquals("Erica hasn't been added to the database", 5, models.size());

        Map<String, Object> lookup = new HashMap<>();
        lookup.put("id", 5);
        erica = dao.get(lookup);
        assertNotNull(erica);
        assertEquals("erica name does not match", "Erica", erica.getName());
        assertEquals("erica job title doesn't match", "Engineer", erica.getJobTitle());
        assertEquals("erica age doesn't match", new Integer(30), erica.getAge());
    }

    @Test
    public void testDAOUpdate(){
        Map<String, Object> lookup = new HashMap<>();
        lookup.put("id", 3);

        SimpleExampleUserModel erica = dao.get(lookup);
        erica.setName("Fred");
        erica.setJobTitle("Fireman");

        dao.save(erica); //Update Claudia, to make her into Devon

        erica = dao.get(lookup);
        assertNotNull(erica);
        assertEquals("fred name does not match", "Fred", erica.getName());
        assertEquals("fred job title doesn't match", "Fireman", erica.getJobTitle());
        assertEquals("fred age doesn't match", new Integer(28), erica.getAge());
    }

    @Test
    public void testDAOdelete(){
        Map<String, Object> lookup = new HashMap<>();
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
