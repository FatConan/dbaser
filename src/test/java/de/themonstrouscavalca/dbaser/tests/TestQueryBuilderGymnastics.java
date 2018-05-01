package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestQueryBuilderGymnastics extends BaseTest {
    private static final String CTE_STRUCTURE = " WITH [[ CTES ]] [[ SELECT ]]";
    private static final String CTE_1 = "cte_1 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice', 'Bob')) ";
    private static final String CTE_2 = "cte_2 AS ( " +
            " SELECT * FROM users WHERE name IN ('Bob')) ";
    private static final String CTE_3 = "cte_3 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice')) ";
    private static final String CTE_4 = "cte_4 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice', 'Bob', 'Chris')) ";
    private static final String CTE_5 = "cte_5 AS ( " +
            " SELECT * FROM users [[ WHERE_CLAUSE ]]) ";
    private static final String WHERE_CLAUSE = "WHERE name IN ('Alice', 'Bob')";
    private static final String SELECT = "SELECT * FROM [[ CTE_NAME ]] WHERE id = ?<userId>";

    private static final String CHECK = " WITH " +
            "cte_1 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice', 'Bob')) " +
            "," +
            "cte_2 AS ( " +
            " SELECT * FROM users WHERE name IN ('Bob')) " +
            "," +
            "cte_3 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice')) " +
            "," +
            "cte_4 AS ( " +
            " SELECT * FROM users WHERE name IN ('Alice', 'Bob', 'Chris')) " +
            " SELECT * FROM cte_1 WHERE id = ?<userId>";

    @Test
    public void testCTECompilation(){
        QueryBuilder cte1 = new QueryBuilder(CTE_1);
        QueryBuilder cte2 = QueryBuilder.fromString(CTE_2);
        QueryBuilder cte3 = new QueryBuilder(CTE_3);
        QueryBuilder cte4 = new QueryBuilder(CTE_4);
        QueryBuilder select = new QueryBuilder(SELECT).replaceClause("[[ CTE_NAME ]]", "cte_1");

        QueryBuilder ctes = QueryBuilder.join(",", cte1, cte2, cte3, cte4);
        QueryBuilder complete = QueryBuilder.fromString(CTE_STRUCTURE);
        complete = complete.replaceClause("[[ CTES ]]", ctes).replaceClause("[[ SELECT ]]", select);

        assertNotNull(complete);
        assertEquals("Constructed CTE query doesn't match expected", complete.getStatement(), CHECK);
    }

    @Test
    public void testCompilation() {
        QueryBuilder query = QueryBuilder.fromString(CTE_STRUCTURE);
        query = query.replaceClause("[[ CTES ]]", CTE_5);
        QueryBuilder query2 = new QueryBuilder(SELECT).replaceClause("[[ CTE_NAME ]]", "cte_5");
        QueryBuilder whereQuery = QueryBuilder.fromString(WHERE_CLAUSE);

        query = query.replaceClause("[[ SELECT ]]", query2);
        QueryBuilder query3 = query.replaceClause("[[ WHERE_CLAUSE ]]", whereQuery);
        QueryBuilder query4 = query.replaceClause("[[ WHERE_CLAUSE ]]", "WHERE name IN ('Alice', 'Bob')");

        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = query3.fullPrepare(c, params)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    assertEquals("Value of ID returned for name lookup 1", 1, rs.getLong("id"));
                } else {
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
        } catch (SQLException | QueryBuilderException e) {
            assertTrue(e.getMessage(),false);
        }

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = query4.fullPrepare(c, params)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    assertEquals("Value of ID returned for name lookup 1", 1, rs.getLong("id"));
                } else {
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
        } catch (SQLException | QueryBuilderException e) {
            assertTrue(e.getMessage(),false);
        }
    }
}
