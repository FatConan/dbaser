package de.themonstrouscavalca.dbaser.utils.interfaces;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.utils.ResultSetOptional;

import java.sql.SQLException;

/*
    Certain database (for example Postgres) support returning results from an insert or update using the returning
    syntax, but others require separate calls. Those will throw errors when calling executeQuery on a prepared insert or update.
    To counter that we need to find a way to be able to specify this at (at least) the DAO level.
*/
@FunctionalInterface
public interface PersistenceMethod<V>{
    ResultSetOptional execute(ExecuteQueries executor, V entity, String sql) throws SQLException, QueryBuilderException;
}
