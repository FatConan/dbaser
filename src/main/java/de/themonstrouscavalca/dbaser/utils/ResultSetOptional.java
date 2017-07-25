package de.themonstrouscavalca.dbaser.utils;

import java.sql.ResultSet;
import java.util.Optional;

public class ResultSetOptional implements AutoCloseable{
    private Optional<ResultSetTableAware> resultSet = Optional.empty();
    private boolean error = false;
    private Exception exception;
    private String errorMsg;

    public boolean isPresent(){
        return this.resultSet.isPresent();
    }

    public ResultSetTableAware get(){
        if(this.resultSet.isPresent()){
            return this.resultSet.get();
        }
        return null;
    }

    public void setResultSet(ResultSetTableAware resultSetTableAware){
        this.resultSet = Optional.ofNullable(resultSetTableAware);
    }
    public void setResultSet(ResultSet resultSet){
        this.resultSet = Optional.of(new ResultSetTableAware(resultSet));
    }

    public boolean isError(){
        return this.error;
    }

    public String getErrorMsg(){
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg){
        this.error = true;
        this.errorMsg = errorMsg;
    }

    public Exception getException(){
        return exception;
    }

    public void setException(Exception exception){
        this.error = true;
        this.errorMsg = exception.getMessage();
        this.exception = exception;
    }

    @Override
    public void close(){
        if(this.isPresent()){
            ResultSetTableAware rs = this.get();
            rs.close();
        }
    }
}
