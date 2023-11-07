package de.themonstrouscavalca.dbaser.models.interfaces.fields;

import java.sql.SQLException;

@FunctionalInterface
public interface IPullFromResultSet{
    void apply(String field) throws SQLException;
}
