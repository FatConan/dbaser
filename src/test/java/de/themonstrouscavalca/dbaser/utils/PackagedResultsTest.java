package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class PackagedResultsTest extends BaseTest{
    private PackagedResults packagedResults() throws SQLException{
        QueryBuilder qb = new QueryBuilder("SELECT 1");
        Connection connection = db.getConnection();
        PreparedStatement ps = qb.prepare(connection);
        ResultSet rs = ps.executeQuery();
        return new PackagedResults(connection, ps, rs);
    }

    @Test
    public void getConnection() throws Exception{
        PackagedResults prs = packagedResults();
        assertTrue("Connection is not set", prs.getConnection() != null);
        prs.close();
    }

    @Test
    public void getPreparedStatement() throws Exception{
        PackagedResults prs = packagedResults();
        assertTrue("PreparedStatement is not set", prs.getPreparedStatement() != null);
        prs.close();
    }

    @Test
    public void getResultSet() throws Exception{
        PackagedResults prs = packagedResults();
        assertTrue("ResultSet is not set", prs.getResultSet() != null);
        prs.close();
    }

    @Test
    public void close() throws Exception{
        PackagedResults prs = packagedResults();
        prs.close();
        assertTrue("ResultSet is not closed", prs.getResultSet().isClosed());
        assertTrue("PreparedStatement is not closed", prs.getPreparedStatement().isClosed());
        assertTrue("Connection is not closed", prs.getConnection().isClosed());
    }
}