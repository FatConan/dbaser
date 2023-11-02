package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.CollectedParameterMaps;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.*;

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
    public void executeBatchCollection1() throws Exception{
        String insert = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)";

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
    public void executeBatchCollection2() throws Exception{
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

        try(Connection connection = db.getConnection(); ExecuteQueries executor = new ExecuteQueries(connection)){
            ResultSetOptional rso = executor.executeBatchUpdate(insert, Arrays.asList(user1, user2, user3));

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
    public void executeBatchCollection3() throws Exception{
        String insert = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)";

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

        try(Connection connection = db.getConnection(); ExecuteQueries executor = new ExecuteQueries(connection)){
            ResultSetOptional rso = executor.executeBatchUpdate(insert, Arrays.asList(user1, user2, user3));

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
    public void executeTransaction() throws Exception{
        String insert = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)";

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

        try(ExecuteQueries executor = new ExecuteQueries(db, true)){
            executor.execute(insert, user1);
            executor.execute(insert, user2);
            executor.execute(insert, user3);
            executor.commit();
        }

        try(Connection connection = db.getConnection()){
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
    public void executeTransactionWithRollback() throws Exception{
        String insert = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)";

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

        try(ExecuteQueries executor = new ExecuteQueries(db, true)){
            executor.execute(insert, user1);
            executor.execute(insert, user2);
            executor.execute(insert, user3);
            executor.rollback();
        }

        try(Connection connection = db.getConnection()){
            QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users WHERE id IN (5,6,7) ORDER BY id");
            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertFalse("Result set was returned", rs.next());
            }
        }
    }

    @Test
    public void executeUpdateDirectFromDatabaseProvider() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)");

        SimpleExampleUserModel user1 = new SimpleExampleUserModel();
        user1.setId(6L);
        user1.setName("Fred");
        user1.setJobTitle("Flotist");
        user1.setAge(70);

        try(Connection connection = db.getConnection(); ExecuteQueries executor = new ExecuteQueries(db)){
            executor.execute(insert, user1);

            QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users WHERE id IN (5,6,7) ORDER BY id");
            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 6L, rs.getLong("id"));
            }
        }
    }

    @Test
    public void attemptInsertionReadonly() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
                " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)");

        SimpleExampleUserModel user1 = new SimpleExampleUserModel();
        user1.setId(6L);
        user1.setName("Fred");
        user1.setJobTitle("Flotist");
        user1.setAge(70);

        try(ExecuteQueries executor = new ExecuteQueries(invalidDB)){
            try{
                executor.execute(insert, user1);
                fail("An exception should be thrown here that we should catch for a successful run of this test");
            }catch(SQLException e){
                //This should pass
            }
        }
    }
}