package de.themonstrouscavalca.dbaser.models.interfaces.fields;

import java.sql.SQLException;

@FunctionalInterface
public interface IPullGenericFromResultSet<T>{
    void apply(T value) throws SQLException;
}
