package de.themonstrouscavalca.dbaser.models.interfaces;

import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IPopulateFromResultSet{
    void populateFromResultSet(ResultSetTableAware rs) throws SQLException;
}
