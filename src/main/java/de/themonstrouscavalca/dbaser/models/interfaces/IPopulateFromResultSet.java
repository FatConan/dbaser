package de.themonstrouscavalca.dbaser.models.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IPopulateFromResultSet{
    String getTablePrefixedFieldName(String fieldName);
    void populateFromResultSet(ResultSet rs) throws SQLException;
}
