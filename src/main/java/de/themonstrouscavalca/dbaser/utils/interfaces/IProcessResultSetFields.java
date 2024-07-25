package de.themonstrouscavalca.dbaser.utils.interfaces;

import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

/**
 * An interface that defines a set of default methods for pulling data out of result sets. This interface can be used
 * by data handling classes to populate their fields from returned in resultsets from databases
 */
public interface IProcessResultSetFields{

    /**
     * Pull a named field value from a resulset (ResultSetTableAware wrapped) as a timestamp and then return the
     * value interprested as a LocalDateTime
     * @param field the name of the database field or column
     * @param rs the resultset
     * @return a LcoalDateTime from the received timestamp or null if interpretation fails.
     * @throws SQLException
     */
    default LocalDateTime localDateTimeFromField(String field, ResultSetTableAware rs) throws SQLException{
        Timestamp ts = rs.getTimestamp(field);
        if(ts != null){
            return ts.toLocalDateTime();
        }
        return null;
    }

    /** Pull a field value from a resultset as a java.sql.Date and return it as a LocalDate
     * @param field the name of the database field or column
     * @param rs the resultset
     * @return LocalDate representation of the retrieved date or null
     * @throws SQLException
     */
    default LocalDate localDateFromField(String field, ResultSetTableAware rs) throws SQLException{
        Date date = rs.getDate(field);
        if(date != null){
            return date.toLocalDate();
        }
        return null;
    }

    /** Pull a field value from a resultset as a java.sql.Time and return it as a LocalTime
     * @param field the name of the database field or column
     * @param rs the resultset
     * @return LocalTime representation of the retrieved time or null
     * @throws SQLException
     */
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

    default void floatFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Float> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getFloat(f));
        });
    }

    default void nullFloatFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Float> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Float val = rs.getObject(f) != null ? rs.getFloat(f) : null;
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

    default <T extends Enum<T> & IEnumerateAgainstDB> void DBEnumFromRS(Class<T> enumClazz, String field,
                                                                        ResultSetTableAware rs,
                                                                        IPullGenericFromResultSet<T> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            long id = rs.getLong(f);
            T eval = EnumSet.allOf(enumClazz).stream().filter(e -> e.getId() == id).findFirst().orElse(null);
            handler.apply(eval);
        });
    }

    default void fieldFromRS(String field, ResultSetTableAware rs, IPullFromResultSet handler) throws SQLException{
        if(rs.has(field)){
            handler.apply(field);
        }
    }
}

