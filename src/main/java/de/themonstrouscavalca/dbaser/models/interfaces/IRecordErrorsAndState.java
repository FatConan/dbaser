package de.themonstrouscavalca.dbaser.models.interfaces;

import de.themonstrouscavalca.dbaser.utils.ProcessingErrorType;

public interface IRecordErrorsAndState{
    ProcessingErrorType errorType();
    String errorMessage();
}
