package de.themonstrouscavalca.dbaser.utils;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultSetOptional implements AutoCloseable{
    private Optional<ResultSetTableAware> resultSet = Optional.empty();
    private Collection<Optional<Integer>> executed = new ArrayList<>();
    private boolean error = false;
    private Exception exception;
    private String errorMsg;

    public static ResultSetOptional of(ResultSet resultSet){
        ResultSetOptional rso = new ResultSetOptional();
        rso.setResultSet(resultSet);
        return rso;
    }

    public ResultSetTableAware get(){
        return resultSet.orElse(null);
    }

    public Optional<ResultSetTableAware> getResultSet(){
        return this.resultSet;
    }

    public boolean isPresent(){
        return this.resultSet.isPresent();
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

    public Collection<Integer> getExecuted(){
        return executed.stream().map(i -> i.orElse(null)).collect(Collectors.toList());
    }

    public void setExecuted(Integer executed){
        this.executed = Collections.singletonList(Optional.ofNullable(executed));
    }

    public void setExecuted(int[] executed){
        List<Optional<Integer>> ex = new ArrayList<>();
        for(int i: executed){
           ex.add(Optional.of(i));
        }
        this.executed = ex;
    }

    @Override
    public void close(){
        if(this.isPresent()){
            ResultSetTableAware rs = this.get();
            rs.close();
        }
    }
}
