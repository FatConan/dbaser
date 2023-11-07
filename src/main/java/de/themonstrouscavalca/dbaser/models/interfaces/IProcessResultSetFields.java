package de.themonstrouscavalca.dbaser.models.interfaces;

import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import de.themonstrouscavalca.dbaser.models.interfaces.fields.IPullFromResultSet;
import de.themonstrouscavalca.dbaser.models.interfaces.fields.IPullGenericFromResultSet;


import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IProcessResultSetFields{
    //Let's add some useful base functions for working with result sets
    default LocalDateTime localDateTimeFromField(String field, ResultSetTableAware rs) throws SQLException{
        Timestamp ts = rs.getTimestamp(field);
        if(ts != null){
            return ts.toLocalDateTime();
        }
        return null;
    }

    default LocalDate localDateFromField(String field, ResultSetTableAware rs) throws SQLException{
        Date date = rs.getDate(field);
        if(date != null){
            return date.toLocalDate();
        }
        return null;
    }

    default LocalTime localTimeFromField(String field, ResultSetTableAware rs) throws SQLException{
        Time time = rs.getTime(field);
        if(time != null){
            return time.toLocalTime();
        }
        return null;
    }

    default void timestampFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Timestamp> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getTimestamp(f));
        });
    }

    default void dateFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Timestamp> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getTimestamp(f));
        });
    }

    default void doubleFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Double> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getDouble(f));
        });
    }

    default void nullDoubleFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Double> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Double val = rs.getObject(f) != null ? rs.getDouble(f) : null;
            handler.apply(val);
        });
    }


    default void longFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Long> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getLong(f));
        });
    }

    default void nullLongFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Long> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Long val = rs.getObject(f) != null ? rs.getLong(f) : null;
            handler.apply(val);
        });
    }

    default void integerFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Integer> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getInt(f));
        });
    }

    default void nullIntegerFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Integer> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Integer val = rs.getObject(f) != null ? rs.getInt(f) : null;
            handler.apply(val);
        });
    }

    default void booleanFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Boolean> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getBoolean(f));
        });
    }

    default void nullBooleanFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Boolean> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Boolean val = rs.getObject(f) != null ? rs.getBoolean(f) : null;
            handler.apply(val);
        });
    }

    default void bigDecimalFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<BigDecimal> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getBigDecimal(f));
        });
    }

    default void nullBigDecimalFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<BigDecimal> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            BigDecimal val = rs.getObject(f) != null ? rs.getBigDecimal(f) : null;
            handler.apply(val);
        });
    }

    default void stringFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<String> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getString(f));
        });
    }

    default void localDateTimeFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<LocalDateTime> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            LocalDateTime ldt = localDateTimeFromField(f, rs);
            handler.apply(ldt);
        });
    }

    default void localDateFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<LocalDate> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            LocalDate ld = localDateFromField(f, rs);
            handler.apply(ld);
        });
    }

    default void localTimeFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<LocalTime> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            LocalTime lt = localTimeFromField(f, rs);
            handler.apply(lt);
        });
    }

    default <T extends Enum<T> & IEnumerateAgainstDB> void DBEnumFromRS(List<T> myEnum, String field,
                                                                        ResultSetTableAware rs,
                                                                        IPullGenericFromResultSet<T> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            long id = rs.getLong(f);
            T eval = myEnum.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
            handler.apply(eval);
        });
    }

    default void fieldFromRS(String field, ResultSetTableAware rs, IPullFromResultSet handler) throws SQLException{
        if(rs.has(field)){
            handler.apply(field);
        }
    }
}

