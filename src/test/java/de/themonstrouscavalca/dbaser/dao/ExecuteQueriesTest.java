package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.CollectedParameterMaps;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by ian on 4/28/18.
 */
public class ExecuteQueriesTest extends BaseTest{

    @Test
    public void executeBatchCollection() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)");

        SimpleExampleUserModel user1 = new SimpleExampleUserModel();
        user1.setId(6L);
        user1.setName("Fred");
        user1.setJobTitle("Flotist");
        user1.setAge(70);

        SimpleExampleUserModel user2 = new SimpleExampleUserModel();
        user2.setId(7L);
        user2.setName("Gordon");
        user2.setJobTitle("Guitarist");
        user2.setAge(32);

        SimpleExampleUserModel user3 = new SimpleExampleUserModel();
        user3.setId(5L);
        user3.setName("Erica");
        user3.setJobTitle("Ecologist");
        user3.setAge(23);

        CollectedParameterMaps collectedParams = CollectedParameterMaps.of(Arrays.asList(user1.exportToMap(), user2.exportToMap(), user3.exportToMap()));

        try(Connection connection = db.getConnection(); ExecuteQueries executor = new ExecuteQueries(connection)){
            ResultSetOptional rso = executor.executeBatchUpdate(insert, collectedParams);

            assertEquals("Incorrect number of executions", 3, rso.getExecuted().size());

            QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users WHERE id IN (5,6,7) ORDER BY id");
            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 5L, rs.getLong("id"));
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 6L, rs.getLong("id"));
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 7L, rs.getLong("id"));
            }
        }
    }

    @Test
    public void executeUpdate() throws Exception{
    }

    @Test
    public void executeUpdate1() throws Exception{
    }

    @Test
    public void executeUpdate2() throws Exception{
    }

    @Test
    public void executeUpdate3() throws Exception{
    }

    @Test
    public void executeQuery() throws Exception{
    }

    @Test
    public void executeQuery1() throws Exception{
    }

    @Test
    public void executeQuery2() throws Exception{
    }

    @Test
    public void executeQuery3() throws Exception{
    }

    @Test
    public void execute() throws Exception{
    }

    @Test
    public void execute1() throws Exception{
    }

    @Test
    public void execute2() throws Exception{
    }

    @Test
    public void execute3() throws Exception{
    }

    @Test
    public void close() throws Exception{
    }

}