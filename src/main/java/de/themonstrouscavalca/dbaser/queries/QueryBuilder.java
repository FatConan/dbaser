package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportAnId;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The QueryBuilder class allows for the construction of SQL statements with named replacements.
 */
public class QueryBuilder {
    private static final Pattern pattern = Pattern.compile("\\?\\<([^>]+)\\>|\\?\\[([^>]+)\\]");

    private static final Map<String, Object> eParams = new HashMap<>();

    public static final Map<String, Object> emptyParams(){
        return eParams;
    }

    public static class QueryBuilderException extends Exception{
        public QueryBuilderException(String message) {
            super(message);
        }
    }

    private class ReplacementCounter{
        private int counter = 1;
        public int getCount(){
            return this.counter;
        }
        public void increment(){
            this.counter += 1;
        }
    }

    private StringBuilder statement;
    private boolean finalised = false;

    public QueryBuilder(){
        this.statement = new StringBuilder();
    }

    public QueryBuilder(String statement){
        this.statement = new StringBuilder();
        this.statement.append(statement);
    }

    public QueryBuilder append(String statement){
        this.statement.append(statement);
        return this;
    }

    public QueryBuilder append(QueryBuilder subBuilder){
        this.statement.append(subBuilder.statement);
        return this;
    }

    public static QueryBuilder fromString(String statement){
        QueryBuilder builder = new QueryBuilder(statement);
        return builder;
    }

    /**
     * Prepare a statement with the assumption that there will be a 1:1 relation between the named replacements and the
     * values needing parameterising (i.e. none of the named replacements represents a collection)
     * @param connection
     * @return A prepared statement representing the current SQL
     * @throws SQLException
     */
    public PreparedStatement prepare(Connection connection) throws SQLException{
        return prepare(connection, new HashMap<>());
    }

    /**
     * Prepare a statement allowing for a non 1:1 relationship between named replacements and parameter values.  However
     * the parameters need to be provided to determine this.
     * @param connection - A database connection
     * @param params - A String->Object map of named parameters
     * @return A prepared statement representing the current SQL
     * @throws SQLException
     */
    public PreparedStatement prepare(Connection connection, Map<String, Object> params) throws SQLException{
        this.finalised = true;
        String finalString = this.statement.toString();
        Matcher matcher = pattern.matcher(finalString);
        StringBuffer resultString = new StringBuffer();

        while (matcher.find()){
            Object param = params.get(matcher.group(1));
            if(param instanceof Collection<?>){
                Collection<?> values = (Collection<?>) param;
                String join = "?";
                StringBuilder replacement = new StringBuilder();
                for(int i = 0; i < values.size(); i++){
                    replacement.append(join);
                    join = ",?";
                }
                matcher.appendReplacement(resultString, replacement.toString());
            }else if (param instanceof Object[]){
                Object[] values = (Object[])param;
                String join = "?";
                StringBuilder replacement = new StringBuilder();
                for(int i=0; i < values.length; i++) {
                    replacement.append(join);
                    join = ",?";
                }
                matcher.appendReplacement(resultString, replacement.toString());
            }else{
                matcher.appendReplacement(resultString, "?");
            }
        }

        //Add the regex match tail
        matcher.appendTail(resultString);

        //If we don't end with a semi-colon be sure to append one
        int length = resultString.length();
        if(resultString.charAt(length-1) != ';'){
            resultString.append(';');
        }
        return connection.prepareStatement(resultString.toString());
    }

    /**
     * Prepare a statement allowing for a 1:1 or non 1:1 relationship between named replacements and parameter values
     * And then parameterise a PreparedStatement against those same provided params.
     * @param connection
     * @param params
     * @return A prepared statement representing the current SQL
     * @throws SQLException
     */
    public PreparedStatement fullPrepare(Connection connection, Map<String, Object> params) throws SQLException, QueryBuilderException{
        PreparedStatement ps = this.prepare(connection, params);
        this.parameterise(ps, params);
        return ps;
    }

    public QueryBuilder replaceClause(String identifier, String replacement){
        return new QueryBuilder(this.statement.toString().replace(identifier, replacement));
    }

    public QueryBuilder replaceClause(String identifier, QueryBuilder replacement){
        return new QueryBuilder(this.statement.toString().replace(identifier, replacement.statement));
    }

    private void addParameter(PreparedStatement ps, Object param, ReplacementCounter index) throws SQLException{
        if(param instanceof Collection<?>){
            for(Object subEntry : (Collection<?>) param){
                this.addParameter(ps, subEntry, index);
            }
        }else if(param instanceof Object[]){
            for(Object subEntry : (Object[]) param){
                this.addParameter(ps, subEntry, index);
            }
        }else if(param instanceof LocalTime){
            ps.setTime(index.getCount(), Time.valueOf((LocalTime) param));
            index.increment();
        }else if(param instanceof LocalDate){
            ps.setDate(index.getCount(), Date.valueOf((LocalDate) param));
            index.increment();
        }else if(param instanceof LocalDateTime){
            ps.setTimestamp(index.getCount(), Timestamp.valueOf((LocalDateTime) param));
            index.increment();
        }else if(param instanceof IEnumerateAgainstDB){
            if(param == null){
                ps.setObject(index.getCount(), param);
            }else{
                long id = ((IEnumerateAgainstDB)param).getId();
                if(id > 0) {
                    ps.setLong(index.getCount(), ((IEnumerateAgainstDB) param).getId());
                }else{
                    ps.setObject(index.getCount(), null);
                }
            }
            index.increment();
        }else if (param instanceof IExportAnId) {
            if(param == null){
                ps.setObject(index.getCount(), param);
            }else{
                long id = ((IExportAnId)param).getId();
                if(id > 0) {
                    ps.setLong(index.getCount(), ((IExportAnId) param).getId());
                }else{
                    ps.setObject(index.getCount(), null);
                }
            }
            index.increment();
        }else{
            ps.setObject(index.getCount(), param);
            index.increment();
        }
    }

    public void batchParameterise(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException {
        this.parameterise(ps, params);
        ps.addBatch();
    }

    public void batchParameterize(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException {
        this.batchParameterise(ps, params);
    }

    public void batchParameterise(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        Map<String, Object> params = model.exportToMap();
        this.batchParameterise(ps, params);
    }

    public void batchParameterize(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        this.batchParameterise(ps, model);
    }

    public void parameterise(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        if(this.finalised){
            String finalString = this.statement.toString();
            Matcher matcher = pattern.matcher(finalString);
            ReplacementCounter index = new ReplacementCounter();
            while (matcher.find()) {
                Object param = params.get(matcher.group(1));
                this.addParameter(ps, param, index);
            }
        }else{
            throw new QueryBuilderException("QueryBuilder Object not finalised");
        }
    }

    public void parameterize(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        this.parameterise(ps, params);
    }


    public void parameterise(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        Map<String, Object> params = model.exportToMap();
        this.parameterise(ps, params);
    }

    public void parameterize(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        this.parameterise(ps, model);
    }
}

