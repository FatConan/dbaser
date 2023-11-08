package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.ResultSet;

import static org.junit.Assert.*;

public class ResultSetCheckerTest extends BaseTest{
    @Test
    public void seek() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            assertTrue("Result set not returned", rs.next());
            ResultSetChecker checker = new ResultSetChecker(rs, Boolean.FALSE);
            assertTrue("Checker missing column", checker.seek("id"));
            assertFalse("Checker has column", checker.seek("users.id"));
            checker = new ResultSetChecker(rs);
            assertTrue("Checker missing column", checker.seek("id"));
            assertTrue("Checker missing column", checker.seek("users.id"));
        }
    }

    @Test
    public void resolve() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            assertTrue("Result set not returned", rs.next());

            ResultSetChecker checker = new ResultSetChecker(rs, Boolean.FALSE);
            assertEquals("Column not matched", Integer.valueOf(1), checker.resolve("id"));
            assertEquals("Column not matched", Integer.valueOf(2), checker.resolve("name"));
            assertEquals("Column not matched", Integer.valueOf(3), checker.resolve("job_title"));
            assertEquals("Column not matched", Integer.valueOf(4), checker.resolve("age"));
            assertEquals("Column not matched", Integer.valueOf(5), checker.resolve("password_hash"));
            assertEquals("Column not matched", Integer.valueOf(6), checker.resolve("password_salt"));

            checker = new ResultSetChecker(rs, Boolean.TRUE);
            assertEquals("Column not matched", Integer.valueOf(1), checker.resolve("users.id"));
            assertEquals("Column not matched", Integer.valueOf(2), checker.resolve("users.name"));
            assertEquals("Column not matched", Integer.valueOf(3), checker.resolve("users.job_title"));
            assertEquals("Column not matched", Integer.valueOf(4), checker.resolve("users.age"));
            assertEquals("Column not matched", Integer.valueOf(5), checker.resolve("users.password_hash"));
            assertEquals("Column not matched", Integer.valueOf(6), checker.resolve("users.password_salt"));
        }
    }

    @Test
    public void has() throws Exception{
        try(PackagedResults prs = this.simpleResultSet()){
            ResultSet rs = prs.getResultSet();
            assertTrue("Result set not returned", rs.next());
            ResultSetChecker checker = new ResultSetChecker(rs, Boolean.FALSE);
            assertTrue("Checker missing column", checker.has("id"));
            assertFalse("Checker has column", checker.has("users.id"));
            checker = new ResultSetChecker(rs);
            assertTrue("Checker missing column", checker.has("id"));
            assertTrue("Checker missing column", checker.has("users.id"));
        }
    }
}