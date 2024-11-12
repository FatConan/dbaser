package de.themonstrouscavalca.dbaser.dao.interfaces.identified;

import de.themonstrouscavalca.dbaser.models.interfaces.IUniquelyModel;

public interface IFullDAO<T extends IUniquelyModel> extends IReadDAO<T>, IWriteDAO<T>{

}
