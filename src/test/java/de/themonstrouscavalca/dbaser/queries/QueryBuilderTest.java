package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderRuntimeException;
import de.themonstrouscavalca.dbaser.models.ComplexModel;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import static org.junit.Assert.*;

public class QueryBuilderTest extends BaseTest{
    @Test
    public void emptyParams() throws Exception{
        assertTrue("Empty params is not a HashMap", QueryBuilder.emptyParams() instanceof ParameterMap);
        assertTrue("Empty params in not empty", QueryBuilder.emptyParams().isEmpty());
        IMapParameters empty1 = QueryBuilder.emptyParams();
        IMapParameters empty2 = QueryBuilder.emptyParams();
        assertEquals("No a singlteton entry", empty1, empty2);
    }

    @Test
    public void append() throws Exception{
        QueryBuilder query = new QueryBuilder();
        query.append("QUERY");
        assertEquals("Checking statement appended", "QUERY", query.getStatement());
        query.append("QUERY");
        assertEquals("Checking statement appended", "QUERYQUERY", query.getStatement());
    }

    @Test
    public void append1() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("QUERY");
        QueryBuilder query2 = QueryBuilder.fromString("QUERY2");
        query.append(query2);
        assertEquals("Checking builder appending", "QUERYQUERY2", query.getStatement());
    }

    @Test
    public void getStatement() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("QUERY");
        assertEquals("Check get statement", "QUERY", query.getStatement());
        query = new QueryBuilder("QUERY");
        assertEquals("Check get statement", "QUERY", query.getStatement());
    }

    @Test
    public void fromString() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("QUERY");
        assertEquals("Check get statement", "QUERY", query.getStatement());
    }

    @Test
    public void prepare() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users");
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.prepare(connection)){
            assertNotNull("Check returned type", ps);
        }
    }

    @Test
    public void prepare1() throws Exception{
        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);
        user.setName("Alice");

        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users WHERE id=?<id> AND name=?<name>");
        IMapParameters params = new ParameterMap();
        params.put("id", 1L);
        params.put("name", "Alice");
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.prepare(connection, params)){
            query.parameterise(ps, params);

            ParameterMetaData metadata = ps.getParameterMetaData();
            assertEquals("Check Parameter Count", 2, metadata.getParameterCount());
            assertEquals("Check Parameter Type 1", "BIGINT", metadata.getParameterTypeName(1));
            assertEquals("Check Parameter Type 2", "VARCHAR", metadata.getParameterTypeName(2));

            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertEquals("Check correct entry failed", "Alice", rs.getString("name"));
            }
        }
    }

    @Test
    public void fullPrepare() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM users WHERE id IN (?<ids>) ORDER BY id");
        IMapParameters params = new ParameterMap();
        params.put("ids", Arrays.asList(1L, 2L, 3L));
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.fullPrepare(connection, params)){

            ParameterMetaData metadata = ps.getParameterMetaData();
            assertEquals("Check Parameter Count", 3, metadata.getParameterCount());

            query.parameterise(ps, params);
            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 1L, rs.getLong("id"));
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 2L, rs.getLong("id"));
                assertTrue("Result set not returned", rs.next());
                assertEquals("Checking users returned failed", 3L, rs.getLong("id"));
            }
        }
    }

    @Test
    public void toStringTest(){
        String sql = "SELECT THIS FROM THAT;";
        QueryBuilder query = QueryBuilder.fromString(sql);
        assertEquals("SQL strings don't match", sql, query.getStatement());
        assertEquals("SQL strings don't match", sql, query.toString());
        assertEquals("Statement and to strings don't match", query.toString(), query.getStatement());
    }

    @Test
    public void nullTest(){
        QueryBuilder query = QueryBuilder.fromString(null);
        assertEquals("Null statement not returning empty string what requested", "", query.getStatement());
    }

    @Test
    public void finalisationTest() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("SELECT 'REPLACEMENT';");
        try(Connection connection = db.getConnection()){
            query.prepare(connection, ParameterMap.empty());
            try{
                query.replaceClause("'REPLACEMENT'", "1");
                fail("We replaced on a prepared query without an exception");
            }catch(QueryBuilderRuntimeException e){
                assertEquals("Unexpected exception message", "Cannot perform replacement, QueryBuilder is already finalised", e.getMessage());
            }catch(Exception e){
                fail("Unexpected exception");
            }
        }

        try(Connection connection = db.getConnection()){
            query.prepare(connection, ParameterMap.empty());
            try{
                query.replaceClause("'REPLACEMENT'", QueryBuilder.fromString("1"));
                fail("We replaced on a prepared query without an exception");
            }catch(QueryBuilderRuntimeException e){
                assertEquals("Unexpected exception message", "Cannot perform replacement, QueryBuilder is already finalised", e.getMessage());
            }catch(Exception e){
                fail("Unexpected exception");
            }
        }

        query = QueryBuilder.fromString("SELECT 'REPLACEMENT';");
        try(Connection connection = db.getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT 1");
            try{
                query.parameterise(ps, ParameterMap.empty());
                fail("We parameterised a non-finalised query without an exception");
            }catch(QueryBuilderException e){
                assertEquals("Unexpected exception message", "QueryBuilder Object not finalised", e.getMessage());
            }catch(Exception e){
                fail("Unexpected exception");
            }
        }
    }

    @Test
    public void negativeIdModel() throws Exception{
        QueryBuilder query = QueryBuilder.fromString("SELECT ?<enumerated> as id");
        IMapParameters params = new ParameterMapBuilder().add("enumerated", TestEnum.BROKEN).build();
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.fullPrepare(connection, params)){
            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertNull("Checking users returned failed", rs.getObject("id"));
            }
        }

        query = QueryBuilder.fromString("SELECT ?<enumerated> as id");
        params = new ParameterMapBuilder().add("enumerated", TestEnum.NULL).build();
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.fullPrepare(connection, params)){
            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertNull("Checking users returned failed", rs.getObject("id"));
            }
        }

        query = QueryBuilder.fromString("SELECT ?<enumerated> as id");
        params = new ParameterMapBuilder().add("enumerated", new TestModel(-1L)).build();
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.fullPrepare(connection, params)){
            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertNull("Checking users returned failed", rs.getObject("id"));
            }
        }

        query = QueryBuilder.fromString("SELECT ?<enumerated> as id");
        params = new ParameterMapBuilder().add("enumerated", new TestModel(null)).build();
        try(Connection connection = db.getConnection();
            PreparedStatement ps = query.fullPrepare(connection, params)){
            try(ResultSet rs = ps.executeQuery()){
                assertTrue("Result set not returned", rs.next());
                assertNull("Checking users returned failed", rs.getObject("id"));
            }
        }
    }

    @Test
    public void replaceClause() throws Exception{
        QueryBuilder query1 = QueryBuilder.fromString("SELECT * FROM [[ table ]]");
        QueryBuilder query = query1.replaceClause("[[ table ]]", "users");
        assertEquals("Replacement Failed", "SELECT * FROM users", query.getStatement());
    }

    @Test
    public void replaceClause1() throws Exception{
        QueryBuilder query1 = QueryBuilder.fromString("SELECT * FROM [[ table ]]");
        QueryBuilder query2 = QueryBuilder.fromString("users");
        QueryBuilder query = query1.replaceClause("[[ table ]]", query2);
        assertEquals("Replacement Failed", "SELECT * FROM users", query.getStatement());
    }

    @Test
    public void join() throws Exception{
        QueryBuilder query1 = QueryBuilder.fromString("QUERY1");
        QueryBuilder query2 = QueryBuilder.fromString("QUERY2");
        QueryBuilder query3 = QueryBuilder.fromString("QUERY3");
        QueryBuilder query4 = QueryBuilder.fromString("QUERY4");
        QueryBuilder query = QueryBuilder.join("||",query1, query2, query3, query4);
        assertEquals("Joined failed", "QUERY1||QUERY2||QUERY3||QUERY4", query.getStatement());
    }

    @Test
    public void join1() throws Exception{
        QueryBuilder query1 = QueryBuilder.fromString("QUERY1");
        QueryBuilder query2 = QueryBuilder.fromString("QUERY2");
        QueryBuilder query3 = QueryBuilder.fromString("QUERY3");
        QueryBuilder query4 = QueryBuilder.fromString("QUERY4");
        QueryBuilder query = QueryBuilder.join(query1, query2, query3, query4);
        assertEquals("Joined failed", "QUERY1, QUERY2, QUERY3, QUERY4", query.getStatement());
    }

    @Test
    public void batchParameterise() throws Exception{
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

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.batchParameterise(ps, user1);
                insert.batchParameterise(ps, user2);
                insert.batchParameterise(ps, user3);
                ps.executeBatch();
            }

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
    public void batchParameterize() throws Exception{
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

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.batchParameterise(ps, user1.exportToMap());
                insert.batchParameterise(ps, user2.exportToMap());
                insert.batchParameterise(ps, user3.exportToMap());
                ps.executeBatch();
            }

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
    public void batchParameterise1() throws Exception{
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

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.batchParameterize(ps, user1.exportToMap());
                insert.batchParameterize(ps, user2.exportToMap());
                insert.batchParameterize(ps, user3.exportToMap());
                ps.executeBatch();
            }

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
    public void batchParameterize1() throws Exception{
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

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.batchParameterize(ps, user1);
                insert.batchParameterize(ps, user2);
                insert.batchParameterize(ps, user3);
                ps.executeBatch();
            }

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
    public void parameterise() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO complex (id, text_entry, long_entry, " +
                " int_entry, double_entry, float_entry, date_entry, time_entry, datetime_entry, user_entry) " +
                " VALUES (?<id>, ?<text_entry>, ?<long_entry>, ?<int_entry>, ?<double_entry>, ?<float_entry>, " +
                "   ?<date_entry>, ?<time_entry>, ?<datetime_entry>, ?<user_entry>)");

        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM complex WHERE id=1");

        TestDateTimeCollection testDatesAndTimes = new TestDateTimeCollection();

        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);

        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(Float.valueOf("4.0"));
        model.setDateEntry(testDatesAndTimes.ld);
        model.setTimeEntry(testDatesAndTimes.lt);
        model.setDatetimeEntry(testDatesAndTimes.ldt);
        model.setUserEntry(user);

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.parameterise(ps, model);
                ps.executeUpdate();
            }

            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("No result set returned", rs.next());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("id"));
                assertEquals("ResultSet doesn't match", "Text Entry", rs.getString("text_entry"));
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("long_entry"));
                assertEquals("ResultSet doesn't match", 2, rs.getInt("int_entry"));
                assertEquals("ResultSet doesn't match", 3.0, rs.getDouble("double_entry"),  0.1);
                assertEquals("ResultSet doesn't match", 4.0, rs.getFloat("float_entry"), 0.1);
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ld, rs.getDate("date_entry").toLocalDate());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.lt, rs.getTime("time_entry").toLocalTime());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ldt, rs.getTimestamp("datetime_entry").toLocalDateTime());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("user_entry"));
            }
        }
    }

    @Test
    public void parameterize() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO complex (id, text_entry, long_entry, " +
                " int_entry, double_entry, float_entry, date_entry, time_entry, datetime_entry, user_entry) " +
                " VALUES (?<id>, ?<text_entry>, ?<long_entry>, ?<int_entry>, ?<double_entry>, ?<float_entry>, " +
                "   ?<date_entry>, ?<time_entry>, ?<datetime_entry>, ?<user_entry>)");

        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM complex WHERE id=1");
        TestDateTimeCollection testDatesAndTimes = new TestDateTimeCollection();


        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);

        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(Float.valueOf("4.0"));
        model.setDateEntry(testDatesAndTimes.ld);
        model.setTimeEntry(testDatesAndTimes.lt);
        model.setDatetimeEntry(testDatesAndTimes.ldt);
        model.setUserEntry(user);

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.parameterize(ps, model);
                ps.executeUpdate();
            }

            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("No result set returned", rs.next());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("id"));
                assertEquals("ResultSet doesn't match", "Text Entry", rs.getString("text_entry"));
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("long_entry"));
                assertEquals("ResultSet doesn't match", 2, rs.getInt("int_entry"));
                assertEquals("ResultSet doesn't match", 3.0, rs.getDouble("double_entry"), 0.1);
                assertEquals("ResultSet doesn't match", 4.0, rs.getFloat("float_entry"), 0.1);
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ld, rs.getDate("date_entry").toLocalDate());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.lt, rs.getTime("time_entry").toLocalTime());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ldt, rs.getTimestamp("datetime_entry").toLocalDateTime());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("user_entry"));
            }
        }
    }

    @Test
    public void parameterise1() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO complex (id, text_entry, long_entry, " +
                " int_entry, double_entry, float_entry, date_entry, time_entry, datetime_entry, user_entry) " +
                " VALUES (?<id>, ?<text_entry>, ?<long_entry>, ?<int_entry>, ?<double_entry>, ?<float_entry>, " +
                "   ?<date_entry>, ?<time_entry>, ?<datetime_entry>, ?<user_entry>)");

        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM complex WHERE id=1");
        TestDateTimeCollection testDatesAndTimes = new TestDateTimeCollection();


        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);

        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(Float.valueOf("4.0"));
        model.setDateEntry(testDatesAndTimes.ld);
        model.setTimeEntry(testDatesAndTimes.lt);
        model.setDatetimeEntry(testDatesAndTimes.ldt);
        model.setUserEntry(user);

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.parameterise(ps, model.exportToMap());
                ps.executeUpdate();
            }

            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("No result set returned", rs.next());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("id"));
                assertEquals("ResultSet doesn't match", "Text Entry", rs.getString("text_entry"));
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("long_entry"));
                assertEquals("ResultSet doesn't match", 2, rs.getInt("int_entry"));
                assertEquals("ResultSet doesn't match", 3.0, rs.getDouble("double_entry"), 0.1);
                assertEquals("ResultSet doesn't match", 4.0, rs.getFloat("float_entry"), 0.1);
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ld, rs.getDate("date_entry").toLocalDate());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.lt, rs.getTime("time_entry").toLocalTime());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ldt, rs.getTimestamp("datetime_entry").toLocalDateTime());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("user_entry"));
            }
        }
    }

    @Test
    public void parameterize1() throws Exception{
        QueryBuilder insert = QueryBuilder.fromString("INSERT INTO complex (id, text_entry, long_entry, " +
                " int_entry, double_entry, float_entry, date_entry, time_entry, datetime_entry, user_entry) " +
                " VALUES (?<id>, ?<text_entry>, ?<long_entry>, ?<int_entry>, ?<double_entry>, ?<float_entry>, " +
                "   ?<date_entry>, ?<time_entry>, ?<datetime_entry>, ?<user_entry>)");

        QueryBuilder query = QueryBuilder.fromString("SELECT * FROM complex WHERE id=1");
        TestDateTimeCollection testDatesAndTimes = new TestDateTimeCollection();

        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);

        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(Float.valueOf("4.0"));
        model.setDateEntry(testDatesAndTimes.ld);
        model.setTimeEntry(testDatesAndTimes.lt);
        model.setDatetimeEntry(testDatesAndTimes.ldt);
        model.setUserEntry(user);

        try(Connection connection = db.getConnection()){
            try(PreparedStatement ps = insert.prepare(connection)){
                insert.parameterize(ps, model.exportToMap());
                ps.executeUpdate();
            }

            try(PreparedStatement ps = query.prepare(connection);
                ResultSet rs = ps.executeQuery()){
                assertTrue("No result set returned", rs.next());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("id"));
                assertEquals("ResultSet doesn't match", "Text Entry", rs.getString("text_entry"));
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("long_entry"));
                assertEquals("ResultSet doesn't match", 2, rs.getInt("int_entry"));
                assertEquals("ResultSet doesn't match", 3.0, rs.getDouble("double_entry"), 0.1);
                assertEquals("ResultSet doesn't match", 4.0, rs.getFloat("float_entry"), 0.1);
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ld, rs.getDate("date_entry").toLocalDate());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.lt, rs.getTime("time_entry").toLocalTime());
                assertEquals("ResultSet doesn't match", testDatesAndTimes.ldt, rs.getTimestamp("datetime_entry").toLocalDateTime());
                assertEquals("ResultSet doesn't match", 1L, rs.getLong("user_entry"));
            }
        }
    }
}