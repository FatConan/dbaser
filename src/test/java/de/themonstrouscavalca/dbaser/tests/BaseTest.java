package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;

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
