package de.themonstrouscavalca.database.models.interfaces;

public interface IModel extends IPopulateFromResultSet, IExportToMap{
    boolean hasErrors();

}
