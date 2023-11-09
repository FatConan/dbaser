package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.InvalidSQLiteDatabase;
import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportAnId;
import de.themonstrouscavalca.dbaser.utils.PackagedResults;
import org.junit.After;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class BaseTest{
    public class TestDateTimeCollection{
        public final LocalTime lt;
        public final LocalDate ld;
        public final LocalDateTime ldt;

        public TestDateTimeCollection(){
            this.lt = LocalTime.MIDNIGHT.truncatedTo(ChronoUnit.MILLIS);
            this.ld = LocalDate.now();
            this.ldt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        }
    }

    protected final SQLiteDatabase db;
    protected final InvalidSQLiteDatabase invalidDB;

    private static final String CREATE_TABLE_USERS = " CREATE TABLE users ( " +
            " id bigint key not null," +
            " name varchar(256) not null," +
            " job_title varchar(50) not null," +
            " age int not null," +
            " password_hash text null," +
            " password_salt text null ) ";

    private static final String CREATE_TABLE_GROUPS = " CREATE TABLE groups ( " +
            " id bigint key not null, " +
            " name varchar(256) not null )";

    private static final String CREATE_TABLE_USER_GROUPS = " CREATE TABLE user_groups ( " +
            " user_id bigint references users(id), " +
            " group_id bigint references groups(id) )";

    private static final String ADD_USERS = " INSERT INTO users (id, name, job_title, age) " +
            " VALUES (1, 'Alice', 'Architect', 30), (2, 'Bob', 'Banker', 47), " +
            " (3, 'Claudia', 'Commissioner', 28), (4, 'Derek', 'Dentist', 52)";

    private static final String ADD_GROUPS = " INSERT INTO groups (id, name) " +
            " VALUES (1, 'Group A'), (2, 'Group B'), " +
            " (3, 'Group C'), (4, 'Group D')";

    private static final String ADD_USER_GROUPS = " INSERT INTO user_groups (user_id, group_id) " +
            " VALUES (1, 1), (1, 2), (2, 3), (3, 4), " +
            "(4, 1), (4, 2), (4, 3), (4, 4)";

    private static final String CREATE_COMPLEX_TABLE = " CREATE TABLE complex ( " +
            " id bigint key not null, " +
            " text_entry varchar(256) null, " +
            " long_entry bigint null, " +
            " int_entry int null, " +
            " double_entry float8 null," +
            " float_entry float null, " +
            " date_entry date null ," +
            " time_entry time null, " +
            " datetime_entry timestamp null, " +
            " user_entry bigint null " +
            " ) ";

    public static class TestModel implements IExportAnId{
        private final Long id;

        public TestModel(Long id){
            this.id = id;
        }

        @Override
        public Long getId(){
            return this.id;
        }

        @Override
        public boolean hasId(){
            return this.getId() != null && this.getId() > 0;
        }
    }

    public enum TestEnum implements IEnumerateAgainstDB{
        ALICE(1L, "Alice"),
        BOB(2L, "Bob"),
        CLAUDIA(3L, "Claudia"),
        DEREk(4L, "Derek"),
        BROKEN(-1L, "BROKEN"),
        NULL(null, "NULL");

        private final Long id;
        private final String name;

        TestEnum(Long id, String name){
            this.id = id;
            this.name = name;
        }

        static TestEnum fromId(long id){
            for(TestEnum t: values()){
                if(t.getId() == id){
                    return t;
                }
            }
            return null;
        }

        static TestEnum fromName(String name){
            for(TestEnum t: values()){
                if(t.getName().equals(name)){
                    return t;
                }
            }
            return null;
        }

        @Override
        public long getId(){
            if(this.id != null){
                return this.id;
            }
            return 0;
        }

        @Override
        public String getName(){
            return this.name;
        }
    }

    protected PackagedResults simpleResultSet() throws SQLException{
        Connection connection = db.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users");
        return new PackagedResults(connection, ps, ps.executeQuery());
    }

    /**
     * Setup this test: instantiate a database and create the base tables and rows within it.
     */
    public BaseTest(){
        SQLiteDatabase db = new SQLiteDatabase();
        SQLiteDatabase.killDatabase(db);
        this.db = db;

        InvalidSQLiteDatabase invalidDB = new InvalidSQLiteDatabase();
        InvalidSQLiteDatabase.killDatabase(invalidDB);
        this.invalidDB = invalidDB;

        try(Connection c = db.getConnection()){
            try(Statement stmt = c.createStatement()){
                stmt.executeUpdate(CREATE_TABLE_USERS);
                stmt.executeUpdate(CREATE_TABLE_GROUPS);
                stmt.executeUpdate(CREATE_TABLE_USER_GROUPS);
                stmt.executeUpdate(CREATE_COMPLEX_TABLE);
            }
            try(PreparedStatement ps = c.prepareStatement(ADD_USERS)){
                ps.execute();
            }
            try(PreparedStatement ps = c.prepareStatement(ADD_GROUPS)){
                ps.execute();
            }
            try(PreparedStatement ps = c.prepareStatement(ADD_USER_GROUPS)){
                ps.execute();
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @After
    public void cleanup(){
        this.db.close();
        this.invalidDB.close();
        SQLiteDatabase.killDatabase(this.db);
        InvalidSQLiteDatabase.killDatabase(this.invalidDB);
    }
}
