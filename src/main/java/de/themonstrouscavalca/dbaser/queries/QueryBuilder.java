package de.themonstrouscavalca.dbaser.queries;

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
    private Pattern pattern = Pattern.compile("\\?\\<([^>]+)\\>|\\?\\[([^>]+)\\]");
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
     * @param connection
     * @return A prepared statement representing the current SQL
     * @throws SQLException
     */
    public PreparedStatement prepare(Connection connection, Map<String, Object> params) throws SQLException{
        this.finalised = true;
        String finalString = this.statement.toString();
        Matcher matcher = this.pattern.matcher(finalString);
        StringBuffer resultString = new StringBuffer();

        while (matcher.find()){
            Object param = params.get(matcher.group(1));
            if(param instanceof Collection<?>){
                Collection<?> values = (Collection<?>) param;
                String join = "?";
                String replacement = "";
                for(int i = 0; i < values.size(); i++){
                    replacement = replacement + join;
                    join = ",?";
                }
                matcher.appendReplacement(resultString, replacement);
            }else if (param instanceof Object[]){
                Object[] values = (Object[])param;
                String join = "?";
                String replacement = "";
                for(int i=0; i<values.length; i++){
                    replacement = replacement + join;
                    join = ",?";
                }
                matcher.appendReplacement(resultString, replacement);
            }else{
                matcher.appendReplacement(resultString, "?");
            }
        }

        matcher.appendTail(resultString);
        return connection.prepareStatement(resultString.toString());
    }

    public PreparedStatement fullPrepare(Connection connection, Map<String, Object> params) throws SQLException, QueryBuilderException{
        PreparedStatement ps = this.prepare(connection, params);
        this.parameterise(ps, params);
        return ps;
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
        }else if(param instanceof LocalDate){
            ps.setDate(index.getCount(), Date.valueOf((LocalDate) param));
        }else if(param instanceof LocalDateTime){
            ps.setTimestamp(index.getCount(), Timestamp.valueOf((LocalDateTime) param));
        }else{
            ps.setObject(index.getCount(), param);
        }
        index.increment();
    }

    public void parameterise(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        if(this.finalised){
            String finalString = this.statement.toString();
            Matcher matcher = this.pattern.matcher(finalString);
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
