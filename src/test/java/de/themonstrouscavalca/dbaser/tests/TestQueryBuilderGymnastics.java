package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestQueryBuilderGymnastics extends BaseTest {
    @Test
    public void testCompilation() {
        QueryBuilder query = new QueryBuilder("WITH cte AS ( " +
                " SELECT * FROM users WHERE name IN ('Alice', 'Bob')) ");
        QueryBuilder query2 = new QueryBuilder("SELECT * FROM cte WHERE id = ?<userId>");
        query.append(query2);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1L);

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = query.fullPrepare(c, params)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    assertEquals("Value of ID returned for name lookup 1", 1, rs.getLong("id"));
                } else {
                    assertTrue("No result set returned for name lookup 1", false);
                }
            }
        } catch (SQLException | QueryBuilder.QueryBuilderException e) {
            e.printStackTrace();
        }
    }
}
