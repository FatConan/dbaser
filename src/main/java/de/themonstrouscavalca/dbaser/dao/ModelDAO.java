package de.themonstrouscavalca.dbaser.dao;


import de.themonstrouscavalca.dbaser.dao.interfaces.IModelDAO;
import de.themonstrouscavalca.dbaser.dao.interfaces.IProvideConnection;
import de.themonstrouscavalca.dbaser.models.impl.IdentifiedModel;

public abstract class ModelDAO<T extends IdentifiedModel> extends BasicModelDAO<T> implements IModelDAO<T>{

    protected ModelDAO(IProvideConnection db){
        super(db);
    }

    @Override
    protected String selectSaveSQL(T entity, boolean forceInsert){
        if(entity.hasId() && !forceInsert){
            return this.getUpdateSQL();
        }
        return this.getInsertSQL();
    }
}
