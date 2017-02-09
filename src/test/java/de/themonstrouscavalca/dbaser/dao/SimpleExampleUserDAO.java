package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;

/**
 * The SimpleExampleUserDAO shows a possible use of the BasicIdentifiedModelDAO. In this
 * example I've written some basic SQL that is sufficient to add, update, delete and retrieve entries
 * from a SQLite database (as you're responsible for constructing the SQL statements, they cannot
 * always be database agnostic).  In a production version against an SQLite database, we'd likely override
 * the save method to fetch last inserted ids and the like.
 *
 * In this example, the DAO can read and write SimpleExampleModel objects.
 */
public class SimpleExampleUserDAO extends BasicIdentifiedModelDAO<SimpleExampleUserModel>{

    /**
     * The constructor in this case is used to set up the IProvideConnection instance (In this case a
     * Provider that connects to a test SQLite database.
     */
    public SimpleExampleUserDAO(){
        this.connectionProvider = new SQLiteDatabase();
    }

    /* These simple queries provide the basic, select, select all, insert, update and delete mechanisms */
    private static final String SELECT_SPECIFIC_SQL = "SELECT * " +
            " FROM users " +
            " WHERE id = ?<id> ";

    private static final String INSERT_SQL = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
            " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>) ";

    private static final String UPDATE_SQL = "UPDATE users SET name=?<name>, job_title=?<job_title>, age=?<age>, " +
            " password_hash=?<password_hash>, password_salt=?<password_salt> " +
            " WHERE id=?<id> ";

    private static final String DELETE_SQL = "DELETE FROM users WHERE id=?<id>";

    private static final String SELECT_LIST_SQL = "SELECT * FROM users";

    /*
        Here I'm using the default implementations of the get, getList, save and delete methods and so
        all we're doing is returning the defined SQL strings for each of the required actions.
     */
    @Override
    protected String getSelectSpecificSQL(){
        return SELECT_SPECIFIC_SQL;
    }

    @Override
    protected String getSelectListSQL(){
        return SELECT_LIST_SQL;
    }

    @Override
    protected String getUpdateSQL(){
        return UPDATE_SQL;
    }

    @Override
    protected String getInsertSQL(){
        return INSERT_SQL;
    }

    @Override
    protected String getDeleteSQL(){
        return DELETE_SQL;
    }

    /*
        Last we define the mechanism for creating new instances of the SimpleExampleUserModel
     */
    @Override
    public SimpleExampleUserModel createInstance(){
        return new SimpleExampleUserModel();
    }
}
