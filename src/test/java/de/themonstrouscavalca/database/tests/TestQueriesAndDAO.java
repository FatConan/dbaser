package de.themonstrouscavalca.database.tests;

import de.themonstrouscavalca.database.SQLiteDatabase;
import de.themonstrouscavalca.database.dao.SimpleExampleUserDAO;
import de.themonstrouscavalca.database.models.SimpleExampleUserModel;
import de.themonstrouscavalca.database.queries.QueryBuilder;
import org.junit.Test;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestQueriesAndDAO{
    private final SimpleExampleUserDAO dao = new SimpleExampleUserDAO();

    private final String CREATE_TABLE = " CREATE TABLE users( " +
            " id bigint key not null," +
            " name varchar(256) not null," +
            " job_title varchar(50) not null," +
            " age int not null," +
            " password_hash text null," +
            " password_salt text null ) ";

    private final String ADD_USERS = " INSERT INTO users (id, name, job_title, age) " +
            " VALUES (1, 'Alice', 'Architect', 30), (2, 'Bob', 'Banker', 47) ";

    public TestQueriesAndDAO(){
        super();
        SQLiteDatabase.killDatabase();

        try(Connection c = SQLiteDatabase.getDatabaseConnection()){
            try(Statement stmt = c.createStatement()){
                stmt.executeUpdate(CREATE_TABLE);
            }
            try(PreparedStatement ps = c.prepareStatement(ADD_USERS)){
                ps.execute();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testQuery(){
        String sqlByName = "SELECT id FROM users WHERE name = ?<name>";
        String sqlByAge = "SELECT id FROM users WHERE age = ?<age>";
        String sqlByAgeAndName = "SELECT id FROM users WHERE name = ?<name> AND age = ?<age>";

        QueryBuilder qByName = new QueryBuilder(sqlByName);
        QueryBuilder qByAge = new QueryBuilder(sqlByAge);
        QueryBuilder qByAgeAndName = new QueryBuilder(sqlByAgeAndName);

        Map<String, Object> lookupAlice = new HashMap<>();
        lookupAlice.put("name", "Alice"); //Not used in age lookup, but can be included in map
        lookupAlice.put("age", 30);

        Map<String, Object> lookupBob = new HashMap<>();
        lookupBob.put("name", "Bob");
        lookupBob.put("age", 47); //Not used in name lookup, but can be included in map

        Map<String, Object> mismatchedNameAndAge = new HashMap<>();
        mismatchedNameAndAge.put("name", "Bob");
        mismatchedNameAndAge.put("age", 30); //Not used in name lookup, but can be included in map


        try(Connection c = SQLiteDatabase.getDatabaseConnection()){
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
    public void testModelAndDAO(){
        SimpleExampleUserModel claudia = new SimpleExampleUserModel();
        claudia.setId(3L); //We're going to force the insert here
        claudia.setName("Claudia");
        claudia.setJobTitle("Commissioner");
        claudia.setAge(28);

        dao.save(claudia, true); //Force the insert because we're not using a sequence to provide keys
        List<SimpleExampleUserModel> models = dao.getList(new HashMap<>()); //Need to add a parameterless version too
        assertEquals("Claudia hasn't been added to the database", 3, models.size());
    }
}
