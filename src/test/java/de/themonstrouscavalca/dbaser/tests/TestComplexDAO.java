package de.themonstrouscavalca.dbaser.tests;

import de.themonstrouscavalca.dbaser.dao.ComplexDAO;
import de.themonstrouscavalca.dbaser.models.ComplexModel;
import de.themonstrouscavalca.dbaser.models.SimpleExampleUserModel;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertNotNull;

public class TestComplexDAO extends BaseTest{
    private ComplexDAO dao = new ComplexDAO();

    @Test
    public void insertComplex(){
        LocalTime lt = LocalTime.MIDNIGHT;
        LocalDate ld = LocalDate.now();
        LocalDateTime ldt = LocalDateTime.now();

        SimpleExampleUserModel user = new SimpleExampleUserModel();
        user.setId(1L);

        ComplexModel model = new ComplexModel();
        model.setId(1L);
        model.setTextEntry("Text Entry");
        model.setLongEntry(1L);
        model.setIntEntry(2);
        model.setDoubleEntry(3.0);
        model.setFloatEntry(new Float("4.0"));
        model.setDateEntry(ld);
        model.setTimeEntry(lt);
        model.setDatetimeEntry(ldt);
        model.setUserEntry(user);

        model = dao.save(model);
        assertNotNull(model);

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

        model2 = dao.save(model2);
        assertNotNull(model2);
    }
}
