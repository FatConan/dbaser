package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;
import de.themonstrouscavalca.dbaser.utils.interfaces.PersistenceMethod;

public record PersistenceExecutor<T>(PersistenceMethod<T> method, PersistenceMode mode){
    public static <V extends IExportToMap> PersistenceExecutor<V> executorCall(){
        return new PersistenceExecutor<>((executor, entity, sql) -> executor.executeUpdate(sql, entity),
                PersistenceMode.NO_RESULT_EXPECTED);
    }

    public static <V extends IExportToMap>  PersistenceExecutor<V> queryingExecutorCall(){
        return new PersistenceExecutor<>((executor, entity, sql) -> executor.executeQuery(sql, entity),
                PersistenceMode.EXPECT_RESULT);
    }
}

