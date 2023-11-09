package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.dao.ComplexDAO;
import de.themonstrouscavalca.dbaser.models.ComplexModel;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import de.themonstrouscavalca.dbaser.queries.ParameterMapBuilder;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestComplexDAO extends BaseTest{
    private final ComplexDAO dao = new ComplexDAO();

    private final LocalTime lt = LocalTime.MIDNIGHT;
    private final LocalDate ld = LocalDate.of(2023, 11, 2);
    private final LocalDateTime ldt = LocalDateTime.of(2023, 11, 13, 4, 14, 17);
    private final SimpleExampleUserModel user;
    {
        SimpleExampleUserModel simpleUser = new SimpleExampleUserModel();
        simpleUser.setId(1L);
        user = simpleUser;
    }
    /*private final LocalTime lt = null;
    private final LocalDate ld = null;
    private final LocalDateTime ldt = null;
    private final SimpleExampleUserModel user = null;*/


    @Test
    public void insertComplex(){
        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(Float.valueOf("4.0"));
        model.setDateEntry(ld);
        model.setTimeEntry(lt);
        model.setDatetimeEntry(ldt);
        model.setUserEntry(user);

        dao.save(model, true); //Force an insert

        Optional<ComplexModel> modelOpt = dao.get(ParameterMapBuilder.of("id", 1).build());
        assertTrue("Model is not present", modelOpt.isPresent());
        modelOpt.ifPresent(m -> {
            assertEquals("Saved model ID doesn't match", 1L, m.getId().longValue());
        });

        ComplexModel model2 = new ComplexModel();
        model2.setId(2L);
        model2.setTextEntry("Text Entry 2");
        model2.setLongEntry(3L);
        model2.setIntEntry(null);
        model2.setDoubleEntry(null);
        model2.setFloatEntry(null);
        model2.setDateEntry(ld);
        model2.setTimeEntry(lt);
        model2.setDatetimeEntry(ldt);
        model2.setUserEntry(user);

        dao.save(model2, true); //Force an insert

        Optional<ComplexModel> modelOpt2 = dao.get(ParameterMapBuilder.of("id", 2).build());
        assertTrue("Model2 is not present", modelOpt2.isPresent());
        modelOpt2.ifPresent(m -> {
            assertEquals("Saved model2 ID doesn't match", 2L, m.getId().longValue());
        });
    }

    @Test
    public void testComplexModelPopulation(){
        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(4L);
        model.setIntEntry(5);
        model.setDoubleEntry(6.0);
        model.setFloatEntry(Float.valueOf("7.0"));
        model.setDateEntry(ld);
        model.setTimeEntry(lt);
        model.setDatetimeEntry(ldt);
        model.setUserEntry(user);

        dao.save(model, true);

        ComplexModel model2 = new ComplexModel();
        model2.setId(2L);
        model2.setTextEntry("Text Entry 3");
        model2.setLongEntry(8L);
        model2.setIntEntry(null);
        model2.setDoubleEntry(null);
        model2.setFloatEntry(null);
        model2.setDateEntry(ld);
        model2.setTimeEntry(lt);
        model2.setDatetimeEntry(ldt);
        model2.setUserEntry(user);

        dao.save(model2, true);

        List<ComplexModel> models = dao.getList();
        assertEquals("Unexpected number of models", 2, models.size());

        Optional<ComplexModel> modelOpt = dao.get(ParameterMapBuilder.of("id", 1L).build());
        assertTrue("Model not present", modelOpt.isPresent());
        modelOpt.ifPresent(m -> {
            assertEquals("Model ID doesn't match", 1L, m.getId().longValue());
            assertEquals("Model Text Entry doesn't match", "Text Entry", m.getTextEntry());
            assertEquals("Model long value doesn't match", 4L, m.getLongEntry().longValue());
            assertEquals("Model int value doesn't match", 5, m.getIntEntry().intValue());
            assertEquals("Model double value doesn't match", 6.0, m.getDoubleEntry(), 0.1);
            assertEquals("Model float value doesn't match", 7.0f, m.getFloatEntry(), 0.1);
            assertEquals("Model date value doesn't match", ld, m.getDateEntry());
            assertEquals("Model time value doesn't match", lt, m.getTimeEntry());
            assertEquals("Model datetime value doesn't match", ldt, m.getDatetimeEntry());
            assertEquals("Model user ID value doesn't match", user.getId().longValue(), m.getUserEntry().getId().longValue());
        });

        Optional<ComplexModel> modelOpt2 = dao.get(ParameterMapBuilder.of("id", 2L).build());
        assertTrue("Model2 is not present", modelOpt2.isPresent());
        modelOpt2.ifPresent(m -> {
            assertEquals("Model2 ID doesn't match", 2L, m.getId().longValue());
            assertEquals("Model2 Text Entry doesn't match", "Text Entry 3", m.getTextEntry());
            assertEquals("Model2 long value doesn't match", 8L, m.getLongEntry().longValue());
            assertNull("Model2 int value doesn't match", m.getIntEntry());
            assertNull("Model2 double value doesn't match", m.getDoubleEntry());
            assertNull("Model2 float value doesn't match", m.getFloatEntry());
            assertEquals("Model2 date value doesn't match", ld, m.getDateEntry());
            assertEquals("Model2 time value doesn't match", lt, m.getTimeEntry());
            assertEquals("Model2 datetime value doesn't match", ldt, m.getDatetimeEntry());
            assertEquals("Model2 user ID value doesn't match", user.getId().longValue(), m.getUserEntry().getId().longValue());
        });
    }
}
