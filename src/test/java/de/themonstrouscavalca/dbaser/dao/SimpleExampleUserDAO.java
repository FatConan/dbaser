package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.models.SimpleExampleGroupModel;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

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
            " VALUES (?<id>, ?<name>, ?<job_title>, ?<age>, ?<password_hash>, ?<password_salt>)";

    private static final String UPDATE_SQL = "UPDATE users SET name=?<name>, job_title=?<job_title>, age=?<age>, " +
            " password_hash=?<password_hash>, password_salt=?<password_salt> " +
            " WHERE id=?<id>";

    private static final String DELETE_SQL = "DELETE FROM users WHERE id=?<id>";

    private static final String SELECT_LIST_SQL = "SELECT * FROM users";

    private static final String SELECT_WITH_GROUPS = "SELECT users.*, groups.* " +
            " FROM users " +
            " JOIN user_groups " +
            " ON (users.id = user_groups.user_id) " +
            " JOIN groups " +
            " ON (groups.id = user_groups.group_id) ";

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

    public Collection<SimpleExampleUserModel> getUsersAndGroups(){
        List<SimpleExampleUserModel> results = new ArrayList<>();
        Map<Long, SimpleExampleUserModel> userMap = new HashMap<>();
        Map<Long, SimpleExampleGroupModel> groupMap = new HashMap<>();
        try(ExecuteQueries executor = new ExecuteQueries(connectionProvider)){
            try(ResultSetOptional rso = executor.executeQuery(SELECT_WITH_GROUPS, QueryBuilder.emptyParams())){
                if(rso.isPresent()){
                    ResultSetTableAware rs = rso.get();
                    while(rs.next()){

                        SimpleExampleUserModel entity = this.createInstance();
                        entity.populateFromResultSet(rs);
                        if(!userMap.containsKey(entity.getId())){
                            userMap.put(entity.getId(), entity);
                            results.add(entity);
                        }else{
                            entity = userMap.get(entity.getId());
                        }

                        SimpleExampleGroupModel group = new SimpleExampleGroupModel();
                        group.populateFromResultSet(rs);
                        if(!groupMap.containsKey(group.getId())){
                            groupMap.put(group.getId(), group);
                        }else{
                            group = groupMap.get(group.getId());
                        }

                        if(!entity.getGroups().contains(group)){
                            entity.addGroup(group);
                        }
                    }
                }
            }
        }catch(SQLException | QueryBuilderException e){
            e.printStackTrace();
        }
        return results;
    }

    /*
        Last we define the mechanism for creating new instances of the SimpleExampleUserModel
     */
    @Override
    public SimpleExampleUserModel createInstance(){
        return new SimpleExampleUserModel();
    }
}
