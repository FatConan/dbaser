package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ian on 2/9/17.
 */
public class BaseTest {
    protected final SQLiteDatabase db = new SQLiteDatabase();

    private static final String CREATE_TABLE = " CREATE TABLE users( " +
            " id bigint key not null," +
            " name varchar(256) not null," +
            " job_title varchar(50) not null," +
            " age int not null," +
            " password_hash text null," +
            " password_salt text null ) ";

    private static final String ADD_USERS = " INSERT INTO users (id, name, job_title, age) " +
            " VALUES (1, 'Alice', 'Architect', 30), (2, 'Bob', 'Banker', 47), " +
            " (3, 'Claudia', 'Commissioner', 28), (4, 'Derek', 'Dentist', 52)";

    /**
     * Setup this test: instantiate a database and create the base tables and rows within it.
     */
    public BaseTest(){
        super();
        db.killDatabase();

        try(Connection c = db.getConnection()){
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
}
