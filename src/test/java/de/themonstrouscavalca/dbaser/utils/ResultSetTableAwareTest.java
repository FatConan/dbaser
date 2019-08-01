package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.queries.ParameterMap;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
}