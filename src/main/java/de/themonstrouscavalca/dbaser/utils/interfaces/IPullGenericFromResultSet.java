package de.themonstrouscavalca.dbaser.utils.interfaces;

import java.sql.SQLException;

@FunctionalInterface
public interface IPullGenericFromResultSet<T>{
    void apply(T t) throws SQLException;
}
