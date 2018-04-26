package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUtilities extends BaseTest{
    private static final String QUERY = "select * from users";
    private static final String QUERY_2 = "select * from users u " +
            " LEFT JOIN user_groups ug " +
            " ON (u.id = ug.user_id) " +
            " LEFT JOIN groups g " +
            " ON (g.id = ug.group_id) ";

    @Test
    public void testResultChecker(){
        QueryBuilder query = new QueryBuilder(QUERY);

        try(Connection c = db.getConnection();
            PreparedStatement ps = query.fullPrepare(c, new HashMap<>());
            ResultSet rs = ps.executeQuery()){
            ResultSetChecker checker = new ResultSetChecker(rs);

            if(rs.next()){
                assertTrue(checker.has("id"));
                assertTrue(checker.has("name"));
                assertTrue(checker.has("job_title"));
                assertTrue(checker.has("age"));
                assertTrue(checker.has("password_hash"));
                assertTrue(checker.has("password_salt"));
                assertFalse(checker.has("name_miss"));
                assertFalse(checker.has("another_name_miss"));
                assertFalse(checker.has(""));
                assertFalse(checker.has(null));
            }else{
                assertTrue("No result set returned for testResultChecker", false);
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue("No result set returned for testResultChecker " + e.getMessage(), false);
        }
    }

    @Test
    public void testResultSetOptional(){
        QueryBuilder query = new QueryBuilder(QUERY);
        ResultSetOptional rsOptional = new ResultSetOptional();

        try(Connection c = db.getConnection();
            PreparedStatement ps = query.fullPrepare(c, new HashMap<>());
            ResultSet rs = ps.executeQuery()){
            rsOptional.setResultSet(rs);

            assertTrue(rsOptional.isPresent());
            ResultSet newRs = rsOptional.get();
            ResultSetChecker checker = new ResultSetChecker(newRs);

            if(rs.next()){
                assertTrue(checker.has("id"));
                assertTrue(checker.has("name"));
                assertTrue(checker.has("job_title"));
                assertTrue(checker.has("age"));
                assertTrue(checker.has("password_hash"));
                assertTrue(checker.has("password_salt"));
                assertFalse(checker.has("name_miss"));
                assertFalse(checker.has("another_name_miss"));
                assertFalse(checker.has(""));
                assertFalse(checker.has(null));
            }else{
                assertTrue("No result set returned for testResultChecker", false);
            }

        }catch(SQLException | QueryBuilderException e){
            assertTrue("No result set returned for testResultChecker " + e.getMessage(), false);
        }
    }

    @Test
    public void testResultSetTableAware(){
        QueryBuilder query = new QueryBuilder(QUERY_2);
        try(Connection c = db.getConnection();
            PreparedStatement ps = query.fullPrepare(c, new HashMap<>());
            ResultSetOptional rso = ResultSetOptional.of(ps.executeQuery())){

            if(rso.isPresent()){
                ResultSetTableAware rsta = rso.get();
                ResultSetChecker checker = new ResultSetChecker(rsta);

                while(rsta.next()){
                    assertTrue(checker.has("users.id"));
                    assertTrue(checker.has("users.name"));
                    assertTrue(checker.has("users.job_title"));
                    assertTrue(checker.has("users.age"));
                    assertTrue(checker.has("users.password_hash"));
                    assertTrue(checker.has("users.password_salt"));

                    assertTrue(checker.has("groups.id"));
                    assertTrue(checker.has("groups.name"));

                    assertFalse(checker.has("users.name_miss"));
                    assertFalse(checker.has("users.another_name_miss"));
                    assertFalse(checker.has(""));
                    assertFalse(checker.has(null));
                }
            }else{
                assertTrue("No result set returned for testResultSetTableAware ", false);
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue("No result set returned for testResultSetTableAware " + e.getMessage(), false);
        }
    }

    @Test
    public void testResultSetTableAwareDisabled(){
        QueryBuilder query = new QueryBuilder(QUERY_2);
        try(Connection c = db.getConnection();
            PreparedStatement ps = query.fullPrepare(c, new HashMap<>());
            ResultSet rs = ps.executeQuery()){

            ResultSetTableAware rsta = new ResultSetTableAware(rs);
            ResultSetChecker checker = new ResultSetChecker(rsta, Boolean.FALSE);

            while(rsta.next()){
                assertFalse(checker.has("users.id"));
                assertFalse(checker.has("users.name"));
                assertFalse(checker.has("users.job_title"));
                assertFalse(checker.has("users.age"));
                assertFalse(checker.has("users.password_hash"));
                assertFalse(checker.has("users.password_salt"));

                assertFalse(checker.has("groups.id"));
                assertFalse(checker.has("groups.name"));

                assertFalse(checker.has(""));
                assertFalse(checker.has(null));

                assertTrue(checker.has("id"));
                assertTrue(checker.has("name"));
                assertTrue(checker.has("job_title"));
                assertTrue(checker.has("age"));
                assertTrue(checker.has("password_hash"));
                assertTrue(checker.has("password_salt"));
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue("No result set returned for testResultSetTableAware " + e.getMessage(), false);
        }
    }
}
