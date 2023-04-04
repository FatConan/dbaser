package de.themonstrouscavalca.dbaser.utils;

import de.themonstrouscavalca.dbaser.utils.interfaces.IPullGenericFromResultSet;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ModelPopulator is a helper class designed to make handling ResultSetTableAware result sets in a clean fashion, allowing
 * for minimal, but understandable code and providing a base that can be extended for project specific circumstances.
 */
public class ModelPopulator{
    public static LocalDateTime localDateTimeFromField(String field, ResultSetTableAware rs) throws SQLException{
        Timestamp ts = rs.getTimestamp(field);
        if(ts != null){
            return ts.toLocalDateTime();
        }
        return null;
    }

    public static LocalDate localDateFromField(String field, ResultSetTableAware rs) throws SQLException{
        Date date = rs.getDate(field);
        if(date != null){
            return date.toLocalDate();
        }
        return null;
    }

    public static void timestampFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Timestamp> handler)  throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getTimestamp(f));
        });
    }

    public static void dateFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Timestamp> handler)  throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getTimestamp(f));
        });
    }

    public static void longFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Long> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getLong(f));
        });
    }

    public static void nullLongFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Long> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Long val = rs.getObject(f) != null ? rs.getLong(f) : null;
            handler.apply(val);
        });
    }

    public static void integerFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Integer> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getInt(f));
        });
    }

    public static void nullIntegerFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Integer> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Integer val = rs.getObject(f) != null ? rs.getInt(f) : null;
            handler.apply(val);
        });
    }

    public static void booleanFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Boolean> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getBoolean(f));
        });
    }

    public static void nullBooleanFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<Boolean> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            Boolean val = rs.getObject(f) != null ? rs.getBoolean(f) : null;
            handler.apply(val);
        });
    }

    public static void stringFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<String> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            handler.apply(rs.getString(f));
        });
    }

    public static void localDateTimeFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<LocalDateTime> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            LocalDateTime ldt = localDateTimeFromField(f, rs);
            handler.apply(ldt);
        });
    }

    public static void localDateFieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<LocalDate> handler) throws SQLException{
        fieldFromRS(field, rs, (f) -> {
            LocalDate ld = localDateFromField(f, rs);
            handler.apply(ld);
        });
    }

    public static void fieldFromRS(String field, ResultSetTableAware rs, IPullGenericFromResultSet<String> handler) throws SQLException{
        fieldFromRS(null, field, rs, handler);
    }

    public static void fieldFromRS(String table, String field, ResultSetTableAware rs, IPullGenericFromResultSet<String> handler) throws SQLException{
        String qualifiedField = TableQualifier.fullyQualify(table, field);
        if(rs.has(qualifiedField)){
            handler.apply(qualifiedField);
        }
    }
}
