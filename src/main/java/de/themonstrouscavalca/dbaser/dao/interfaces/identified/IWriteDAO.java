package de.themonstrouscavalca.dbaser.dao.interfaces.identified;

import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

import java.sql.Connection;

public interface IWriteDAO<T extends IUniquelyModel> extends de.themonstrouscavalca.dbaser.dao.interfaces.basic.IWriteDAO<T>{
    ResponseOrError<T> save(T entity);
    ResponseOrError<T> save(Connection connection, T entity);

    ResponseOrError<Boolean> delete(long id);
    ResponseOrError<Boolean> delete(Connection connection, long id);
}
