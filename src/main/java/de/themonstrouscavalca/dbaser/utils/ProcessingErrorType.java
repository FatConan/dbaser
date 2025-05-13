package de.themonstrouscavalca.dbaser.utils;

public enum ProcessingErrorType{
    CONNECTION_FAILURE("Database connection error"),
    MISSING("Missing database entry"),
    AMBIGUOUS("Ambiguous result"),
    SQL_EXCEPTION("SQL exception");

    private final String description;
    ProcessingErrorType(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static ProcessingError connectionFailure(String msg){
        return new ProcessingError(CONNECTION_FAILURE, msg);
    }

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
