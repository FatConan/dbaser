package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.utils.interfaces.IProcessResultSetFields;

/**
 * ModelPopulator is a helper class designed to make handling ResultSetTableAware result sets in a clean fashion, allowing
 * for minimal, but understandable code and providing a base that can be extended for project specific circumstances.
 */
public class ModelPopulator implements IProcessResultSetFields{
    private static final ModelPopulator INSTANCE = new ModelPopulator();

    public static ModelPopulator getInstance(){
        return INSTANCE;
    }
}
