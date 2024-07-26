package de.themonstrouscavalca.dbaser.utils.interfaces;

import de.themonstrouscavalca.dbaser.dao.ExecuteQueries;
import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import de.themonstrouscavalca.dbaser.queries.QueryBuilder;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.tests.BaseTest;
import de.themonstrouscavalca.dbaser.utils.PackagedResults;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class IProcessResultSetFieldsTest extends BaseTest{
    static final LocalDateTime DATETIME = LocalDateTime.of(1980, 2, 2, 7, 0, 0);
    static final LocalDate DATE = DATETIME.toLocalDate();
    static final LocalTime TIME = DATETIME.toLocalTime();
    static final String TEXT = "TEXT";
    static final Long LONG = 1234567890L;
    static final Integer INTEGER = 1234567;
    static final Double DOUBLE = 0.0000045;
    static final Float FLOAT = 12312.5f;
    static final Long ID = 1L;

    enum TestEnum implements IEnumerateAgainstDB{
        A(LONG),
        B(2L),
        C(3L),
        D(4L);

        private final Long id;

        TestEnum(Long id){
            this.id = id;
        }

        static TestEnum fromId(long id){
            for(TestEnum t : values()){
                if(t.getId() == id){
                    return t;
                }
            }
            return null;
        }

        static TestEnum fromName(String name){
            for(TestEnum t : values()){
                if(t.getName().equals(name)){
                    return t;
                }
            }
            return null;
        }

        @Override
        public long getId(){
            return id;
        }

        @Override
        public String getName(){
            return name();
        }
    }

    @Before
    public void addEntry(){
        String insert = "INSERT INTO complex (id, text_entry, long_entry, int_entry, double_entry, float_entry, date_entry, " +
                "time_entry, datetime_entry) " +
                " values (?<id>, ?<text>, ?<long>, ?<int>, ?<double>, ?<float>, ?<date>, ?<time>, ?<datetime>)";
        String select = "SELECT * FROM complex WHERE id=1";

        IMapParameters params = ParameterMapBuilder
                .of("id", ID)
                .add("text", TEXT)
                .add("long", LONG)
                .add("int", INTEGER)
                .add("double", DOUBLE)
                .add("float", FLOAT)
                .add("date", DATE)
                .add("time", TIME)
                .add("datetime", DATETIME)
                .build();

        try(ExecuteQueries executor = new ExecuteQueries(db)){
            QueryBuilder sql = QueryBuilder.fromString(insert);
            executor.execute(sql, params);
        }catch(SQLException e){
            fail("Unable to create executor" + e.getMessage());
            e.printStackTrace();
        }catch(QueryBuilderException e){
            fail("Query failed" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class Processor implements IProcessResultSetFields{

    }

    @Test
    public void localDateTimeFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().localDateTimeFieldFromRS("datetime_entry", rs, (v) -> assertEquals("Datetimes do not match", v, DATETIME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void localDateFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().localDateFieldFromRS("date_entry", rs, (v) -> assertEquals("Dates do not match", v, DATE));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void localTimeFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().localTimeFieldFromRS("time_entry", rs, (v) -> assertEquals("Times do not match", v, TIME));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /*@Test
    public void timestampFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().localDateFieldFromRS("datetime", rs, (v) -> assertEquals("Dates do not match", v, DATETIME.toEpochSecond(null)));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void dateFieldFromRS(){
    }*/

    @Test
    public void doubleFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().doubleFieldFromRS("double_entry", rs, (v) -> assertEquals("Doubles do not match", v, DOUBLE));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void nullDoubleFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().nullDoubleFieldFromRS("double_entry", rs, (v) -> assertEquals("Doubles do not match", v, DOUBLE));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void floatFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().floatFieldFromRS("float_entry", rs, (v) -> assertEquals("Floats do not match", v, FLOAT));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void nullFloatFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().nullFloatFieldFromRS("float_entry", rs, (v) -> assertEquals("Floats do not match", v, FLOAT));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void longFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().longFieldFromRS("long_entry", rs, (v) -> assertEquals("Longs do not match", v, LONG));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void nullLongFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().nullLongFieldFromRS("long_entry", rs, (v) -> assertEquals("Longs do not match", v, LONG));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void integerFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().integerFieldFromRS("int_entry", rs, (v) -> assertEquals("Integers do not match", v, INTEGER));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void nullIntegerFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().nullIntegerFieldFromRS("int_entry", rs, (v) -> assertEquals("Longs do not match", v, INTEGER));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void booleanFieldFromRS(){

    }

    @Test
    public void nullBooleanFieldFromRS(){

    }

    @Test
    public void bigDecimalFieldFromRS(){
    }

    @Test
    public void nullBigDecimalFieldFromRS(){
    }

    @Test
    public void stringFieldFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().stringFieldFromRS("text_entry", rs, (v) -> assertEquals("Strings do not match", v, TEXT));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void DBEnumListFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().DBEnumFromRS(Arrays.asList(TestEnum.values()), "long_entry", rs, (v) -> assertEquals("Enums do not match", v, TestEnum.A));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void DBEnumClassFromRS(){
        try(PackagedResults packagedResults = complexResultSet();
            ResultSetTableAware rs = packagedResults.getResultSetTableAware()){
            new Processor().DBEnumFromRS(TestEnum.class, "long_entry", rs, (v) -> assertEquals("Enums do not match", v, TestEnum.A));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}