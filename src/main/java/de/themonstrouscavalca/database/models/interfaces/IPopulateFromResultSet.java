package de.themonstrouscavalca.database.models.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IPopulateFromResultSet{
    void populateFromResultSet(ResultSet rs) throws SQLException;
}
