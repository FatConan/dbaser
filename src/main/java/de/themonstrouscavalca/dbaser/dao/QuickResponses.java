package de.themonstrouscavalca.dbaser.dao;

import de.themonstrouscavalca.dbaser.utils.ProcessingError;
import de.themonstrouscavalca.dbaser.utils.ProcessingErrorType;
import de.themonstrouscavalca.dbaser.utils.ResponseOrError;

import java.util.Optional;

public class QuickResponses{
    public static class ResponseOrErrorException extends RuntimeException{
        public ResponseOrErrorException(String message){
            super(message);
        }
    }

    @FunctionalInterface
    public interface ExceptionAction<E>{
        void act(E e);
    }

    //An error of some description occurred
    public static <V, E extends Exception> ResponseOrError<V> sqlError(String error, E e, ExceptionAction<E> action){
        return new ResponseOrError<V>(null, ProcessingErrorType.sqlException(
                String.format("%s: %s", error, e.getMessage())));
    }

    //We expected a result but didn't get one
    public static <V> ResponseOrError<V> missing(String error){
        return ResponseOrError.error(ProcessingErrorType.missing(error));
    }

    //We expected exactly one result, but got multiple
    public static <V> ResponseOrError<V> ambiguous(){
        return ResponseOrError.error(new ProcessingError(ProcessingErrorType.AMBIGUOUS,
                "Multiple results were returned but only one was expected"));
    }

    //We didn't get a result, but it wasn't wholly unexpected
    public static <V> ResponseOrError<V> noResult(){
        return ResponseOrError.success(null);
    }

    public static <V, U> ResponseOrError<V> repackageError(ResponseOrError<U> toPackage){
        if(toPackage.isFailure()){
            return ResponseOrError.error(toPackage.error());
        }
        throw new ResponseOrErrorException("Attempting to pull errors from a successful response");
    }
}
