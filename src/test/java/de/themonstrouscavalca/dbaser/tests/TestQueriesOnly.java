package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestQueriesOnly extends BaseTest{
    final String sqlById = "SELECT id FROM users WHERE id = ?<id>";
    final String sqlByName = "SELECT id FROM users WHERE name = ?<name>";
    final String sqlByAge = "SELECT id FROM users WHERE age = ?<age>";
    final String sqlByAgeAndName = "SELECT id FROM users WHERE name = ?<name> AND age = ?<age>";
    final String sqlAllById = "SELECT * FROM users WHERE id = ?<id>";
    final String multipleSelect = "SELECT * FROM users WHERE id IN (?<ids>)";
    final String sqlMultipleWithSelect = "WITH cte(id, age) AS ( " +
            "  SELECT id, age FROM users WHERE age IN(?<ages>) " +
            " ) " +
            " SELECT * " +
            " FROM users JOIN cte " +
            " ON(cte.id = users.id) " +
            " WHERE users.age IN (?<ages>) ";

    final String sqlMaskingWithSelect = "WITH cte AS ( " +
            "  SELECT * FROM users WHERE age IN(?<ages>) " +
            " ) " +
            " SELECT * " +
            " FROM cte";

    final String sqlInsertUsers = " INSERT INTO users (id, name, job_title, age) " +
            " VALUES (5, 'Eric', 'Engineer', 26), (6, 'Fran', 'Filmmaker', 57) ";
    final String sqlCountUsers = "SELECT COUNT(*) as user_total FROM users";

    QueryBuilder qById = new QueryBuilder(sqlById);
    QueryBuilder qByName = new QueryBuilder(sqlByName);
    QueryBuilder qAllById = new QueryBuilder(sqlAllById);
    QueryBuilder qByAge = new QueryBuilder(sqlByAge);
    QueryBuilder qByAgeAndName = new QueryBuilder(sqlByAgeAndName);
    QueryBuilder qMultipleSelect = new QueryBuilder(multipleSelect);
    QueryBuilder qMultipleWithSelect = new QueryBuilder(sqlMultipleWithSelect);
    QueryBuilder qMaskingWithSelect = new QueryBuilder(sqlMaskingWithSelect);
    QueryBuilder qInsertUsers = new QueryBuilder(sqlInsertUsers);
    QueryBuilder qCountUsers = new QueryBuilder(sqlCountUsers);

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

    Map<String, Object> multipleSelectParams = new HashMap<>();
    {
        multipleSelectParams.put("ids", new Long[]{1L, 2L, 3L});
    }

    Map<String, Object> multipleWithSelectParams = new HashMap<>();
    {
        multipleWithSelectParams.put("ages", new Long[]{-1L, 30L});
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

            try(PreparedStatement ps = qMultipleWithSelect.fullPrepare(c, multipleWithSelectParams)){
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age and name with CTE lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age and name with CTE lookup 1", false);
                }
            }

        }catch(SQLException | QueryBuilderException e){
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

            try(PreparedStatement ps = qByAgeAndName.prepare(c)){
                qByAgeAndName.parameterise(ps, mismatchedNameAndAge);
                ResultSet rs = ps.executeQuery();
                assertEquals("Result set returned from mismatched parameters for age and name lookup 2", rs.next(), false);
            }

            try(PreparedStatement ps = qMultipleWithSelect.prepare(c, multipleWithSelectParams)){
                qMultipleWithSelect.parameterise(ps, multipleWithSelectParams);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    assertEquals("Value of ID returned for age and name with CTE lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for age and name with CTE lookup 1", false);
                }
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }

    private SimpleExampleUserModel checkResultSetOptional(ResultSetOptional rso){
        SimpleExampleUserModel model = new SimpleExampleUserModel();

        try{
            if(rso.isPresent()){
                ResultSetTableAware rsta = rso.get();
                if(rsta.next()){
                    ResultSetChecker checker = new ResultSetChecker(rsta);
                    assertTrue(checker.has("users.id"));
                    model.populateFromResultSet(rsta);
                }else{
                    assertTrue("No results from query", false);
                }
            }else{
                assertTrue("No results from query", false);
            }
        }catch(SQLException e){
            assertTrue("No results from query", false);
        }

        return model;
    }


    @Test
    public void testExecutorMechanisms() throws SQLException, QueryBuilderException{
        final String SELECT = "SELECT * FROM users WHERE id = ?<id>";
        final String UPDATE = "UPDATE users SET name=?<name> WHERE id=?<id>";
        final String DELETE = "DELETE FROM users WHERE id=?<id>";
        final String INDEX = "CREATE INDEX user_idx ON users(name)";
        final String DEINDEX = "DROP INDEX user_idx";


        QueryBuilder qSELECT = QueryBuilder.fromString(SELECT);
        QueryBuilder qUPDATE = QueryBuilder.fromString(UPDATE);
        QueryBuilder qDELETE = QueryBuilder.fromString(DELETE);
        QueryBuilder qINDEX = QueryBuilder.fromString(INDEX);
        QueryBuilder qDEINDEX = QueryBuilder.fromString(DEINDEX);

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1);
        params.put("name", "Alicia");

        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);
        user.setName("Alicia");

        ExecuteQueries<SimpleExampleUserModel> execute = new ExecuteQueries<>(db);

        checkResultSetOptional(execute.executeQuery(SELECT, params));
        checkResultSetOptional(execute.executeQuery(SELECT, user));
        checkResultSetOptional(execute.executeQuery(qSELECT, params));
        checkResultSetOptional(execute.executeQuery(qSELECT, user));

        execute.executeUpdate(UPDATE, params);
        SimpleExampleUserModel model = checkResultSetOptional(execute.executeQuery(SELECT, params));
        assertEquals("Returned name doesn't match", "Alicia", model.getName());

        execute.executeUpdate(UPDATE, user);
        model = checkResultSetOptional(execute.executeQuery(SELECT, user));
        assertEquals("Returned name doesn't match", user.getName(), model.getName());

        execute.executeUpdate(qUPDATE, params);
        model = checkResultSetOptional(execute.executeQuery(qSELECT, params));
        assertEquals("Returned name doesn't match", "Alicia", model.getName());

        execute.executeUpdate(qUPDATE, user);
        model = checkResultSetOptional(execute.executeQuery(qSELECT, user));
        assertEquals("Returned name doesn't match", user.getName(), model.getName());

        /*execute.execute(INDEX, params);
        execute.execute(DEINDEX, user);
        execute.execute(qINDEX, params);
        execute.execute(qDEINDEX, user);*/
    }


    @Test
    public void testTransactionalConnection(){
        try(Connection c = db.getTransactionalConnection()){
            try(PreparedStatement ps = qInsertUsers.prepare(c)){
                ps.executeUpdate();
                db.rollbackAndRestore(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue("Failure when inserting users and rolling back", false);
        }

        try(Connection c = db.getConnection()) {
            try(PreparedStatement ps = qCountUsers.prepare(c); ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    assertEquals(4, rs.getInt("user_total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            assertTrue("No result set returned from user count", false);
        }
    }

    @Test
    public void testMulitpleReplacements(){
        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = qMultipleSelect.prepare(c, multipleSelectParams)){
                qMultipleSelect.parameterise(ps, multipleSelectParams);
                ResultSet rs = ps.executeQuery();
                List<Long> returned = new ArrayList<>();
                while(rs.next()){
                   returned.add(rs.getLong("id"));
                }
                assertEquals("Incorrect number of results returned", 3, returned.size());
                assertTrue("Incorrect ID returned", returned.contains(1L));
                assertTrue("Incorrect ID returned", returned.contains(2L));
                assertTrue("Incorrect ID returned", returned.contains(3L));
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testEnumQueries(){
        Map<String, Object> params = new HashMap<>();
        params.put("id", TestEnum.ALICE);

        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = qAllById.prepare(c)){
                qAllById.parameterise(ps, params);
                ResultSet rs = ps.executeQuery();
                ResultSetChecker checker = new ResultSetChecker(rs);
                if(rs.next()){
                    if(checker.has("id")){
                        TestEnum testEnum = TestEnum.fromId(rs.getLong("id"));
                        assertEquals("Enum from ID", TestEnum.ALICE, testEnum);
                    }else{
                        assertTrue("No result set returned for name lookup 1", false);
                    }
                    if(checker.has("name")){
                        TestEnum testEnum = TestEnum.fromName(rs.getString("name"));
                        assertEquals("Enum from name", TestEnum.ALICE, testEnum);
                    }else{
                        assertTrue("No result set returned for name lookup 1", false);
                    }
                    assertEquals("Value of ID returned for name lookup 1", 1, rs.getLong("id"));
                }else{
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testCTEtablespace(){
        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = qMaskingWithSelect.fullPrepare(c, multipleWithSelectParams)){
                try(ResultSet rs = new ResultSetTableAware(ps.executeQuery())){
                    boolean result = false;
                    while(rs.next()){
                        result = true;
                        assertEquals("Value of ID returned for masked lookup 1", 1, rs.getLong("id"));
                        SimpleExampleUserModel m = new SimpleExampleUserModel();
                        m.populateFromResultSet(rs);
                        assertNotEquals("MaskedWithSelect able to populate table aware model", null, m.getId());
                        assertNotEquals("MaskedWithSelect able to populate table aware model", 0L, m.getId().longValue());
                    }
                    assertTrue("No result set returned for masked lookup 1", result);
                }
            }
        }catch(SQLException | QueryBuilderException e){
            assertTrue(e.getMessage(), false);
        }
    }
}
