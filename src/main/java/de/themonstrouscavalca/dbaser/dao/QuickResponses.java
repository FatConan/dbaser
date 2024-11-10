package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.utils.ProcessingErrorType;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuickResponses{
    @FunctionalInterface
    public interface ExceptionAction<E>{
        void act(E e);
    }

    //An error of some description occurred
    public static <V, E extends Exception> ResponseAndError<V> sqlError(String error, E e, ExceptionAction<E> action){
        return new ResponseAndError<>(Optional.empty(), ProcessingErrorType.sqlException(error + e.getMessage()));
    }

    //We expected a result but didn't get one
    public static <V> ResponseAndError<V> missing(String error){
        return new ResponseAndError<>(Optional.empty(), ProcessingErrorType.missing(error));
    }

    //We didn't get a result, but it wasn't wholly unexpected
    public static <V> ResponseAndError<V> noResult(){
        return ResponseAndError.success(null);
    }
}
