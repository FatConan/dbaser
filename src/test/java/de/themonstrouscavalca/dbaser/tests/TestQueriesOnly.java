package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ian on 2/9/17.
 */
public class TestQueriesOnly extends BaseTest{
    String sqlByName = "SELECT id FROM users WHERE name = ?<name>";
    String sqlByAge = "SELECT id FROM users WHERE age = ?<age>";
    String sqlByAgeAndName = "SELECT id FROM users WHERE name = ?<name> AND age = ?<age>";

    QueryBuilder qByName = new QueryBuilder(sqlByName);
    QueryBuilder qByAge = new QueryBuilder(sqlByAge);
    QueryBuilder qByAgeAndName = new QueryBuilder(sqlByAgeAndName);

    Map<String, Object> lookupAlice = new HashMap<>();
    {
        lookupAlice.put("name", "Alice"); //Not used in age lookup, but can be included in map
        lookupAlice.put("age", 30);
    }

    Map<String, Object> lookupBob = new HashMap<>();
    {
        lookupBob.put("name", "Bob");
        lookupBob.put("age", 47); //Not used in name lookup, but can be included in map
    }

    Map<String, Object> mismatchedNameAndAge = new HashMap<>();
    {
        mismatchedNameAndAge.put("name", "Bob");
        mismatchedNameAndAge.put("age", 30); //Not used in name lookup, but can be included in map
    }

    @Test
    public void testFullPrepare(){
        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = qByName.fullPrepare(c, lookupBob)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for name lookup 1", 2, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
            try(PreparedStatement ps = qByAge.fullPrepare(c, lookupAlice)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age lookup 1", false);
                }
            }

            try(PreparedStatement ps = qByName.fullPrepare(c, lookupAlice)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for name lookup 2", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for name lookup 2", false);
                }
            }
            try(PreparedStatement ps = qByAge.fullPrepare(c, lookupBob)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age lookup 2", 2, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age lookup 2", false);
                }
            }

            try(PreparedStatement ps = qByAgeAndName.fullPrepare(c, lookupAlice)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age and name lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age and name lookup 1", false);
                }
            }
            try(PreparedStatement ps = qByAgeAndName.fullPrepare(c, mismatchedNameAndAge)){
                ResultSet rs = ps.executeQuery();
                assertEquals("Result set returned from mismatched parameters for age and name lookup 2", rs.next(), false);
            }
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testBasicPrepare(){
        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = qByName.prepare(c)){
                qByName.parameterise(ps, lookupBob);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for name lookup 1", 2, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
            try(PreparedStatement ps = qByAge.prepare(c)){
                qByAge.parameterize(ps, lookupAlice);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age lookup 1", false);
                }
            }

            try(PreparedStatement ps = qByName.prepare(c)){
                qByName.parameterize(ps, lookupAlice);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for name lookup 2", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for name lookup 2", false);
                }
            }
            try(PreparedStatement ps = qByAge.prepare(c)){
                qByAge.parameterise(ps, lookupBob);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age lookup 2", 2, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age lookup 2", false);
                }
            }

            try(PreparedStatement ps = qByAgeAndName.prepare(c)){
                qByAgeAndName.parameterise(ps, lookupAlice);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age and name lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age and name lookup 1", false);
                }
            }
            try(PreparedStatement ps = qByAgeAndName.fullPrepare(c, mismatchedNameAndAge)){
                ResultSet rs = ps.executeQuery();
                assertEquals("Result set returned from mismatched parameters for age and name lookup 2", rs.next(), false);
            }
        }catch(SQLException | QueryBuilder.QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }
}
