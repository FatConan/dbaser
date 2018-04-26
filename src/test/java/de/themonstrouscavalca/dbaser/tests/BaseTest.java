package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseTest {
    protected final SQLiteDatabase db = new SQLiteDatabase();

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
            " datetime_entry timestamp null " +
            " ) ";

    public enum TestEnum implements IEnumerateAgainstDB{
        ALICE(1L, "Alice"),
        BOB(2L, "Bob"),
        CLAUDIA(3L, "Claudia"),
        DEREk(4L, "Derek");

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
            return this.id;
        }

        @Override
        public String getName(){
            return this.name;
        }
    }

    /**
     * Setup this test: instantiate a database and create the base tables and rows within it.
     */
    public BaseTest(){
        super();
        db.killDatabase();

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
}
