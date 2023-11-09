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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class TestComplexDAO extends BaseTest{
    private final ComplexDAO dao = new ComplexDAO();

    private final LocalTime lt = LocalTime.MIDNIGHT;
    private final LocalDate ld = LocalDate.of(2023, 11, 2);
    private final LocalDateTime ldt = LocalDateTime.of(2023, 11, 13, 4, 14, 17);
    private final  SimpleExampleUserModel user;
    {
        SimpleExampleUserModel simpleUser = new SimpleExampleUserModel();
        simpleUser.setId(1L);
        user = simpleUser;
    }

    private static final String ADD_EMPTY_COMPLEX_ENTRIES = " INSERT INTO complex (id) " +
            " VALUES (1), (2)";

    public TestComplexDAO(){
        super();
        try(Connection c = db.getConnection()){
            try(PreparedStatement ps = c.prepareStatement(ADD_EMPTY_COMPLEX_ENTRIES)){
                ps.execute();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

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

        model = dao.save(model);
        assertNotNull(model);

        model = dao.get(ParameterMapBuilder.of("id", 1L).build());
        assert(model.getId().equals(1L));

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

        model2 = dao.get(ParameterMapBuilder.of("id", 2).build());
        assert(model2.getId().equals(2L));
    }

    @Test
    public void testComplexModelPopulation(){
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

        model = dao.save(model);
        assertNotNull(model);
        assert(model.getId().equals(1L));

        model = dao.get(ParameterMapBuilder.of("id", 1).build());
        assertNotNull(model);
        assert(model.getId() == 1L);
        assert(model.getTextEntry().equals("Text Entry"));
        assert(model.getLongEntry() == 1L);
        assert(model.getIntEntry() == 2);
        assert(model.getDoubleEntry().equals(3.0));
        assert(model.getFloatEntry().equals(Float.valueOf("4.0")));
        assert(model.getDateEntry().equals(ld));
        assert(model.getTimeEntry().equals(lt));
        assert(model.getDatetimeEntry().equals(ldt));
        assert(model.getUserEntry().getId().equals(user.getId()));

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

        model2 = dao.get(ParameterMapBuilder.of("id", 2).build());
        assertNotNull(model2);
        assert(model2.getId() == 2L);
        assert(model2.getTextEntry().equals("Text Entry 2"));
        assert(model2.getLongEntry() == 3L);
        assert(model2.getIntEntry() == null);
        assert(model2.getDoubleEntry() == null);
        assert(model2.getFloatEntry() == null);
        assert(model2.getDateEntry().equals(ld));
        assert(model2.getTimeEntry().equals(lt));
        assert(model2.getDatetimeEntry().equals(ldt));
        assert(model2.getUserEntry().getId().equals(user.getId()));
    }
}
