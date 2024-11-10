package de.themonstrouscavalca.dbaser.utils;

public enum ProcessingErrorType{
    MISSING,
    SQL_EXCEPTION;

    public static ProcessingError missing(String msg){
        return new ProcessingError(MISSING, msg);
    }

    public static ProcessingError sqlException(String msg){
        return new ProcessingError(SQL_EXCEPTION, msg);
    }

    public boolean isMissing(){
        return this == MISSING;
    }
}
