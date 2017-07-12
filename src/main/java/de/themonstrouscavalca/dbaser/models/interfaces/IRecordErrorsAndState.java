package de.themonstrouscavalca.dbaser.models.interfaces;

import de.themonstrouscavalca.dbaser.enums.ErrorStatus;

import java.util.Optional;

public interface IRecordErrorsAndState{
    Optional<String> getError();
    void setError(String error);
    Optional<ErrorStatus> getErrorStatus();
    void setErrorStatus(ErrorStatus errorStatus);

    boolean isUpdated();
    void setUpdated(boolean updated);
    boolean isDeleted();
    void setDeleted(boolean deleted);
    boolean isCreated();
    void setCreated(boolean created);
}
