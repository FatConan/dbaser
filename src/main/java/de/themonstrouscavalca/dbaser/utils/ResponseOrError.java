package de.themonstrouscavalca.dbaser.utils;

import java.util.Optional;

public class ResponseOrError<T>{
    private final Either<Optional<T>, ProcessingError> internalEither;

    public static <V> ResponseOrError<V> success(V entity){
        return new ResponseOrError<>(entity, null);
    }

    public static <V> ResponseOrError<V> error(ProcessingError error){
        return new ResponseOrError<>(null, error);
    }

    public ResponseOrError(T entity, ProcessingError error){
        if(entity != null){
            this.internalEither = Either.withLeft(Optional.of(entity));
        }else{
            this.internalEither = Either.withRight(error);
        }
    }

    public Optional<T> response(){
        return this.internalEither.left();
    }

    public ProcessingError error(){
        return this.internalEither.right();
    }

    public boolean isSuccess(){
        return this.internalEither.isLeft();
    }

    public boolean isFailure(){
        return this.internalEither.isRight();
    }
}