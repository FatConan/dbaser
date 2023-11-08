package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderRuntimeException;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ResultSetOptionalTest extends BaseTest{

    @Test
    public void isPresent() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            ResultSetOptional rso = ResultSetOptional.of(rs);
            assertTrue("Result set is missing", rso.isPresent());
            assertFalse("Result set is erroneous", rso.isError());
        }
    }

    @Test
    public void of() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            ResultSetOptional rso = ResultSetOptional.of(rs);
            assertTrue("Result set is missing", rso.isPresent());
            assertTrue("Not returning a table aware result set", rso.get() != null);
            assertEquals("ResultSets don't match", rs, rso.get().getDelegate());
        }
    }

    @Test
    public void setResultSet() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            ResultSetOptional rso = new ResultSetOptional();
            rso.setResultSet(rs);
            assertTrue("Result set is missing", rso.isPresent());
            assertTrue("Not returning a table aware result set", rso.get() != null);
            assertEquals("ResultSets don't match", rs, rso.get().getDelegate());
        }
    }

    @Test
    public void setResultSet1() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            ResultSetTableAware rsta = new ResultSetTableAware(rs);
            ResultSetOptional rso = new ResultSetOptional();
            rso.setResultSet(rsta);
            assertTrue("Result set is missing", rso.isPresent());
            assertTrue("Not returning a table aware result set", rso.get() != null);
            assertEquals("ResultSets don't match", rs, rso.get().getDelegate());
        }
    }

    @Test
    public void isError() throws Exception{
        ResultSetOptional rso = ResultSetOptional.of(null);
        rso.setException(new QueryBuilderException("Fake exception message"));
        assertTrue("ResultSetOptional not reporting as erroneous", rso.isError());
    }

    @Test
    public void getErrorMsg() throws Exception{
        ResultSetOptional rso = ResultSetOptional.of(null);
        rso.setException(new QueryBuilderException("Fake exception message"));
        assertEquals("ResultSetOptional not retuning message", "Fake exception message", rso.getErrorMsg());
    }

    @Test
    public void setErrorMsg() throws Exception{
        ResultSetOptional rso = ResultSetOptional.of(null);
        rso.setErrorMsg(new QueryBuilderRuntimeException("Fake exception message").getMessage());
        assertEquals("ResultSetOptional not retuning message", "Fake exception message", rso.getErrorMsg());
    }

    @Test
    public void getException() throws Exception{
        QueryBuilderException err = new QueryBuilderException("Fake exception message");
        ResultSetOptional rso = ResultSetOptional.of(null);
        rso.setException(err);
        assertEquals("ResultSetOptional not reporting as erroneous", err, rso.getException());
    }

    @Test
    public void getExecuted() throws Exception{
        ResultSetOptional rso = ResultSetOptional.of(null);
        rso.setExecuted(2);
        assertEquals("Executed values don't match", Integer.valueOf(2), new ArrayList<>(rso.getExecuted()).get(0)
        );
    }

    @Test
    public void close() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            ResultSetOptional rso = ResultSetOptional.of(rs);
            assertTrue("Result set is missing", rso.isPresent());
            assertTrue("Not returning a table aware result set", rso.get() != null);
            assertEquals("ResultSets don't match", rs, rso.get().getDelegate());

            rso.close();
            assertTrue("Hasn't closed result set", rs.isClosed());
        }
    }
}