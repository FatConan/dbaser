package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.models.interfaces.IRecordErrorsAndState;

public record ProcessingError(ProcessingErrorType errorType, String errorMessage) implements IRecordErrorsAndState{

}
