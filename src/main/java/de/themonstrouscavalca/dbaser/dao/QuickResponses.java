package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.utils.ProcessingError;
import de.themonstrouscavalca.dbaser.utils.ProcessingErrorType;
import de.themonstrouscavalca.dbaser.utils.ResponseAndError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuickResponses{
    public static class ResponseAndErrorException extends RuntimeException{
        public ResponseAndErrorException(String message){
            super(message);
        }
    }

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

    //We expected exactly one result, but got multiple
    public static <V> ResponseAndError<V> ambiguous(){
        return new ResponseAndError<>(Optional.empty(), new ProcessingError(ProcessingErrorType.AMBIGUOUS,
                "Multiple results were returned but only one was expected"));
    }

    //We didn't get a result, but it wasn't wholly unexpected
    public static <V> ResponseAndError<V> noResult(){
        return ResponseAndError.success(null);
    }

    public static <V, U> ResponseAndError<V> repackageError(ResponseAndError<U> toPackage){
        if(toPackage.isFailure()){
            return new ResponseAndError<>(null, toPackage.error());
        }
        throw new ResponseAndErrorException("Attempting to pull errors from a successful response");
    }
}
