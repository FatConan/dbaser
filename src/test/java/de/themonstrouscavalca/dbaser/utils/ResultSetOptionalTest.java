package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.ResultSet;

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
            ResultSetOptional rso = new  ResultSetOptional();
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
            ResultSetOptional rso = new  ResultSetOptional();
            rso.setResultSet(rsta);
            assertTrue("Result set is missing", rso.isPresent());
            assertTrue("Not returning a table aware result set", rso.get() != null);
            assertEquals("ResultSets don't match", rs, rso.get().getDelegate());
        }
    }

    @Test
    public void isError() throws Exception{
    }

    @Test
    public void getErrorMsg() throws Exception{
    }

    @Test
    public void setErrorMsg() throws Exception{
    }

    @Test
    public void getException() throws Exception{
    }

    @Test
    public void setException() throws Exception{
    }

    @Test
    public void getExecuted() throws Exception{
    }

    @Test
    public void setExecuted() throws Exception{
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