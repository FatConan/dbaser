package de.themonstrouscavalca.dbaser.dao.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IProvideConnection{
    Connection getConnection() throws SQLException;
    Connection getTransactionalConnection() throws SQLException;
    void commitAndRestore(Connection connection) throws SQLException;
    void rollbackAndRestore(Connection connection) throws SQLException;
}
