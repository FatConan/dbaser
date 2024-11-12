package de.themonstrouscavalca.dbaser.dao.interfaces.identified;

import de.themonstrouscavalca.dbaser.models.impl.IdentifiedModel;
import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;

public interface IWriteDAO<T extends IUniquelyModel> extends de.themonstrouscavalca.dbaser.dao.interfaces.basic.IWriteDAO<T>{
    ResponseAndError<T> save(T entity);
    ResponseAndError<T> save(Connection connection, T entity);

    ResponseAndError<Boolean> delete(long id);
    ResponseAndError<Boolean> delete(Connection connection, long id);
}
