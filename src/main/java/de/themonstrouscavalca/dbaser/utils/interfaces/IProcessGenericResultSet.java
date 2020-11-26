package de.themonstrouscavalca.dbaser.utils.interfaces;

import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

@FunctionalInterface
public interface IProcessGenericResultSet<T>{
    void apply(String qualifiedField, ResultSetTableAware rs, IPullGenericFromResultSet<T> handler);
}
