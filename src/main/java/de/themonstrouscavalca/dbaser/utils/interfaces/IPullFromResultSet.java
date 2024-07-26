package de.themonstrouscavalca.dbaser.utils.interfaces;

import java.sql.SQLException;

@FunctionalInterface
public interface IPullFromResultSet{
    void apply(String field) throws SQLException;
}
