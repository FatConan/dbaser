package de.themonstrouscavalca.dbaser.models.interfaces;

public interface IUniquelyModel extends IExportAnId, IModel{
    void setId(Long id);
}
