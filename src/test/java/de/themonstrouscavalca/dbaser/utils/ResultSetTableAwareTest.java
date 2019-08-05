package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ResultSetTableAwareTest extends BaseTest{
    @Test
    public void typeLoading(){
        String insert = "INSERT INTO complex (id, text_entry, long_entry, int_entry, double_entry, float_entry, date_entry, " +
                "time_entry, datetime_entry) " +
                " values (?<id>, ?<text>, ?<long>, ?<int>, ?<double>, ?<float>, ?<date>, ?<time>, ?<datetime>)";
        String select = "SELECT * FROM complex WHERE id=1";

        LocalDateTime dateTime = LocalDateTime.of(1980, 2, 2, 7, 0, 0);
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        IMapParameters params = ParameterMapBuilder
                .of("id", 1L)
                .add("text", "TEXT")
                .add("long", 1234567890L)
                .add("int", 1234567)
                .add("double", 0.0000045)
                .add("float", 12312.5)
                .add("date", date)
                .add("time", time)
                .add("datetime", dateTime)
                .build();

        try(ExecuteQueries executor = new ExecuteQueries(db)){
            QueryBuilder sql = QueryBuilder.fromString(insert);
            executor.execute(sql, params);

            try(ResultSetOptional rso = executor.executeQuery(select, ParameterMap.empty())){
                if(rso.isPresent()){
                    ResultSetTableAware rs = rso.get();
                    if(rs.next()){
                        long id = rs.getLong("id");
                        assertEquals("ID doesn't match", 1L, id);
                        String text = rs.getString("text_entry");
                        assertEquals("TEXT doesn't match", "TEXT", text);
                        long longEntry = rs.getLong("long_entry");
                        assertEquals("LONG doesn't match", 1234567890L, longEntry);
                        int intEntry = rs.getInt("int_entry");
                        assertEquals("ID doesn't match", 1234567, intEntry);
                        double doubleEntry = rs.getDouble("double_entry");
                        assertEquals("DOUBLE doesn't match", 0.0000045f, doubleEntry, 0.1f);
                        float floatEntry = rs.getFloat("float_entry");
                        assertEquals("FLOAT doesn't match", 12312.5f, floatEntry, 0.1f);
                        LocalDate dateEntry = rs.getDate("date_entry").toLocalDate();
                        assertEquals("DATE doesn't match", date, dateEntry);
                        LocalTime timeEntry = rs.getTime("time_entry").toLocalTime();
                        assertEquals("TIME doesn't match", time, timeEntry);
                        LocalDateTime dateTimeEntry = rs.getTimestamp("datetime_entry").toLocalDateTime();
                        assertEquals("DATETIME doesn't match", dateTime, dateTimeEntry);
                    }
                }else{
                    fail("Unable to insert into database");
                }
            }
        }catch(SQLException e){
            fail("Unable to create executor" + e.getMessage());
            e.printStackTrace();
        }catch(QueryBuilderException e){
            fail("Query failed" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void delegateComplianceTest(){
        String sql = "SELECT 1 as entry";

        try(Connection connection = db.getConnection(); ResultSet results = connection.prepareStatement(sql).executeQuery()){
            ResultSetTableAware rsta = new ResultSetTableAware(results);
            if(results.next()){
                assertEquals("TableAwareResultSet disagreement boolean", results.getBoolean(1), rsta.getBoolean(1));
                assertEquals("TableAwareResultSet disagreement string", results.getString(1), rsta.getString(1));
                assertEquals("TableAwareResultSet disagreement float", results.getFloat(1), rsta.getFloat(1), 0.1);
                assertEquals("TableAwareResultSet disagreement double", results.getDouble(1), rsta.getDouble(1), 0.1);
                assertEquals("TableAwareResultSet disagreement long", results.getLong(1), rsta.getLong(1));
                assertEquals("TableAwareResultSet disagreement int", results.getInt(1), rsta.getInt(1));

                assertEquals("TableAwareResultSet disagreement big decimal", results.getBigDecimal(1), rsta.getBigDecimal(1));
                //assertEquals("TableAwareResultSet disagreement big decimal", results.getBigDecimal(1, 1), rsta.getBigDecimal(1, 1)); Not implemented in SQLLite
                assertEquals("TableAwareResultSet disagreement byte", results.getByte(1), rsta.getByte(1));
                assertTrue("TableAwareResultSet disagreement bytes", Arrays.equals(results.getBytes(1), rsta.getBytes(1)));
                assertEquals("TableAwareResultSet disagreement short", results.getShort(1), rsta.getShort(1));
                //assertEquals("TableAwareResultSet disagreement blob", results.getBlob(1), rsta.getBlob(1)); Not implemented in SQLLite

                assertEquals("TableAwareResultSet disagreement boolean", results.getBoolean("entry"), rsta.getBoolean("entry"));
                assertEquals("TableAwareResultSet disagreement string", results.getString("entry"), rsta.getString("entry"));
                assertEquals("TableAwareResultSet disagreement float", results.getFloat("entry"), rsta.getFloat("entry"), 0.1);
                assertEquals("TableAwareResultSet disagreement double", results.getDouble("entry"), rsta.getDouble("entry"), 0.1);
                assertEquals("TableAwareResultSet disagreement long", results.getLong("entry"), rsta.getLong("entry"));
                assertEquals("TableAwareResultSet disagreement int", results.getInt("entry"), rsta.getInt("entry"));
                assertEquals("TableAwareResultSet disagreement big decimal", results.getBigDecimal("entry"), rsta.getBigDecimal("entry"));
                assertEquals("TableAwareResultSet disagreement byte", results.getByte("entry"), rsta.getByte("entry"));
                assertTrue("TableAwareResultSet disagreement bytes", Arrays.equals(results.getBytes("entry"), rsta.getBytes("entry")));
                assertEquals("TableAwareResultSet disagreement short", results.getShort("entry"), rsta.getShort("entry"));

            }else{
                fail("Failed to return resultset");
            }
        }catch(SQLException e){
            e.printStackTrace();
            fail("Failed to run query");
        }
    }
}