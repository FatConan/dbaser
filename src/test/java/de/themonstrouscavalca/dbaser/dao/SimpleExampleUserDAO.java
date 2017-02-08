package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;

public class SimpleExampleUserDAO extends BasicIdentifiedModelDAO<SimpleExampleUserModel>{
    public SimpleExampleUserDAO(){
        this.connectionProvider = new SQLiteDatabase();
    }

    private final String SELECT_SPECIFIC_SQL = "SELECT * " +
            " FROM users " +
            " WHERE id = ?<id> ";

    private final String INSERT_SQL = "INSERT INTO users (id, name, job_title, age, password_hash, password_salt) " +
            " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>) ";

    private final String UPDATE_SQL = "UPDATE users SET name=?<name>, job_title=?<job_title>, age=?<age>, " +
            " password_hash=?<password_hash>, password_salt=?<password_salt> " +
            " WHERE id=?<id> ";

    private final String DELETE_SQL = "DELETE FROM users WHERE id=?<id>";

    private final String SELECT_LIST_SQL = "SELECT * FROM users";

    @Override
    protected String getSelectSpecificSQL(){
        return this.SELECT_SPECIFIC_SQL;
    }

    @Override
    protected String getSelectListSQL(){
        return this.SELECT_LIST_SQL;
    }

    @Override
    protected String getUpdateSQL(){
        return this.UPDATE_SQL;
    }

    @Override
    protected String getInsertSQL(){
        return this.INSERT_SQL;
    }

    @Override
    protected String getDeleteSQL(){
        return this.DELETE_SQL;
    }

    @Override
    public SimpleExampleUserModel createInstance(){
        return new SimpleExampleUserModel();
    }
}
