package de.themonstrouscavalca.dbaser.dao.interfaces.identified;

import de.themonstrouscavalca.dbaser.models.impl.IdentifiedModel;
import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;

import java.sql.Connection;

public interface IReadDAO<T extends IUniquelyModel> extends
        de.themonstrouscavalca.dbaser.dao.interfaces.basic.IReadDAO<T>{

    ResponseAndError<T> get(long id);
    ResponseAndError<T> get(Connection connection, long id);
}
