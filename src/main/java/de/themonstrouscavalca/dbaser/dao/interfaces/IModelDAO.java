package de.themonstrouscavalca.dbaser.dao.interfaces;

import de.themonstrouscavalca.dbaser.dao.interfaces.identified.IFullDAO;
import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;

public interface IModelDAO<T extends IUniquelyModel> extends IFullDAO<T>{

}
