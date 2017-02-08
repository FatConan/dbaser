package de.themonstrouscavalca.database.dao.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IProvideConnection{
    public Connection getConnection() throws SQLException;
}
