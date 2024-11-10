package de.themonstrouscavalca.dbaser.utils;

import java.util.Optional;

public record ResponseAndError<T>(Optional<T> response, ProcessingError error){
public static <V> ResponseAndError<V> success(V entity){
    return new ResponseAndError<>(Optional.of(entity), null);
}

public boolean isSuccess(){
    return error == null;
}

public boolean isFailure(){
    return error != null;
}
}