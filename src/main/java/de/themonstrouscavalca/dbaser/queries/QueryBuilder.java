package de.themonstrouscavalca.dbaser.queries;

import de.themonstrouscavalca.dbaser.enums.interfaces.IEnumerateAgainstDB;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderException;
import de.themonstrouscavalca.dbaser.exceptions.QueryBuilderRuntimeException;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportAnId;
import de.themonstrouscavalca.dbaser.models.interfaces.IExportToMap;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The QueryBuilder class allows for the construction of SQL statements with named replacements. It offers some methods
 * to manipulate statements, make replacements and parameterise the statement from a Map or instance implementing the
 * IExportToMap interface.
 */
public class QueryBuilder {
    private static final String delimiter = ", ";
    private static final Pattern pattern = Pattern.compile("\\?\\<([^>]+)\\>|\\?\\[([^>]+)\\]");
    private static final Map<String, Object> eParams = new HashMap<>();

    /**
     * Return a singleton empty parameter set for use whenever an empty parameter set needs to be passed.
     * @return An empty singleton Map<String, Object> instance
     */
    public static Map<String, Object> emptyParams(){
        return eParams;
    }

    /* Keep track of the replacements being made when parameterising the queries */
    static class ReplacementCounter{
        private int counter = 1;
        int getCount(){
            return this.counter;
        }
        void increment(){
            this.counter += 1;
        }
    }

    private StringBuilder statement;
    private boolean finalised = false;

    /**
     * Initialise an empty QueryBuilder
     */
    public QueryBuilder(){
        this.statement = new StringBuilder();
    }

    /**
     * Initialise a QueryBuilder using the provided String as the initial statment,
     * @param statement An initial statement
     */
    public QueryBuilder(String statement){
        this.statement = new StringBuilder();
        this.statement.append(statement);
    }

    /**
     * Append a string to the current instance's statement and return the current instance.  This acts like a StringBuilder
     * appending the new statement to the internally stored statment and returning the current instance.
     * @param statement the String statment to append
     * @return the current instance
     */
    public QueryBuilder append(String statement){
        this.statement.append(statement);
        return this;
    }

    /**
     * Append a statement to the current query. This appends the statement from a provided QueryBuilder meaning that
     * statement manipulation can be performed using the QueryBuilder tools and then appended to another query.  This happens
     * in situ and returns the modified callee instance.
     * @param subBuilder A QueryBuilder instance holding a statement to append
     * @return this instance with the append made to its stored statement
     */
    public QueryBuilder append(QueryBuilder subBuilder){
        this.statement.append(subBuilder.statement);
        return this;
    }

    /**
     * Returns the currently stored statement. Statments are stored without parameter replacements having been performed
     * so they will still appear with the ?<parameterName> placeholders
     * @return A string representing the stored statement.
     */
    public String getStatement(){
        if(statement != null){
            return this.statement.toString();
        }
        return "";
    }

    /**
     * Generate a new QueryBuilder instance from the provided string, it's just a wrapping mechanisms for the QueryBuilder
     * constructor but can be used to make the code perhaps a little more explicit when creating them.
     * @param statement A SQL statment String
     * @return a new QueryBuilder instance
     */
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

    /**
     * Replace a placeholder within the current statement with another statement.
     * @param identifier the placeholder string to replace
     * @param replacement the string replacement
     * @return The current instance representing the statement with the replacement made
     */
    public QueryBuilder replaceClause(String identifier, String replacement){
        if(this.finalised){
            throw new QueryBuilderRuntimeException("Cannot perform replacement, QueryBuilder is already finalised");
        }
        this.statement = new StringBuilder(this.statement.toString().replace(identifier, replacement));
        return this;
    }

    /**
     * Replace a placeholder within the current statement with a statement held in the provided QueryBuilder
     * @param identifier the placeholder string to replace
     * @param replacement a QueryBuilder instance holding the replacement
     * @return The current instance representing the statement with the replacement made
     */
    public QueryBuilder replaceClause(String identifier, QueryBuilder replacement){
        if(this.finalised){
            throw new QueryBuilderRuntimeException("Cannot perform replacement, QueryBuilder is already finalised");
        }
        this.statement = new StringBuilder(this.statement.toString().replace(identifier, replacement.statement));
        return this;
    }

    /**
     * Joins a collection of QueryBuilder statements together using the provided delimiter and return a QueryBuilder instance
     * representing the joined statements
     * @param delimiter the delimiter String
     * @param queries the QueryBuilder instances to join
     * @return A QueryBuilder instance representing the joined statements
     */
    public static QueryBuilder join(String delimiter, QueryBuilder ... queries){
        return new QueryBuilder(String.join(delimiter, Arrays.stream(queries).map(q -> q.statement).collect(Collectors.toList())));
    }

    /**
     * Join a collection of QueryBuilder statements together using the default ", " delimiter
     * @param queries the QueryBuilder queries to be joined
     * @return A QueryBuilder representing the joined queries.
     */
    public static QueryBuilder join(QueryBuilder ... queries){
        return join(delimiter, queries);
    }

    /**
     * Add parameters to a PerparedStatment by determining the instance type of each named parameter replacement and then adding
     * them to the prepared statement using the best-fit method. This also iterates through the known replacements keeping track of indexes
     * and resolving those positions as it goes. This has special handling for instances that QueryBuilder
     * is aware of such as the IExportAnId models and IEnumerateAgainstDB enumerations, as well as the ability to expand collections
     * and recursively handle them.
     * @param ps The PreparedStatement to parameterise
     * @param param A Object, The parameter to add
     * @param index The current index to add the parameter
     * @throws SQLException
     */
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
            long id = ((IEnumerateAgainstDB)param).getId();
            if(id > 0) {
                ps.setLong(index.getCount(), ((IEnumerateAgainstDB) param).getId());
            }else{
                ps.setObject(index.getCount(), null);
            }
            index.increment();
        }else if (param instanceof IExportAnId) {
            long id = ((IExportAnId)param).getId();
            if(id > 0) {
                ps.setLong(index.getCount(), ((IExportAnId) param).getId());
            }else{
                ps.setObject(index.getCount(), null);
            }
            index.increment();
        }else{
            ps.setObject(index.getCount(), param);
            index.increment();
        }
    }

    /**
     * Parametise the current statement and add to a PreparedStatement batch than can later be executed with the executeBatch call.
     * @param ps The prepared statement to add the batch of parameters to
     * @param params The named parameter map to add
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void batchParameterise(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        this.parameterise(ps, params);
        ps.addBatch();
    }

    /**
     * An Americanized version of batchParameterise
     * @param ps
     * @param params
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void batchParameterize(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        this.batchParameterise(ps, params);
    }

    /**
     * Parameterise the current statement and add to a PreparedStatement batch that can later be executed with executeBatch call.
     * This version takes an IExportToMap model rather than a parameter map.
     * @param ps The PreparedStatement to add the batch of parameters to
     * @param model An IExportToMap model that will be used as the parameter source
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void batchParameterise(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        Map<String, Object> params = model.exportToMap();
        this.batchParameterise(ps, params);
    }

    /**
     * An Americanized version of batchParameterize
     * @param ps
     * @param model
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void batchParameterize(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        this.batchParameterise(ps, model);
    }

    /**
     * Parameterise the current statement from a map of named parameters
     * @param ps The PreparedStatement to parameterise
     * @param params The map of named parameters
     * @throws QueryBuilderException
     * @throws SQLException
     */
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

    /**
     * The Americanized version of parameterise
     * @param ps The prepared statement to parametrize
     * @param params The map of named parameterz
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void parameterize(PreparedStatement ps, Map<String, Object> params) throws QueryBuilderException, SQLException{
        this.parameterise(ps, params);
    }

    /**
     * Parameterise the current statement from an IExportToMap model
     * @param ps The PreparedStatement to parameterise
     * @param model The IExportToMap model to parameterise from
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void parameterise(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        Map<String, Object> params = model.exportToMap();
        this.parameterise(ps, params);
    }

    /**
     * The Americanised version of parameterise
     * @param ps The PreparedStatement to parameterize
     * @param model The IExportToMap model to parameterize from
     * @throws QueryBuilderException
     * @throws SQLException
     */
    public void parameterize(PreparedStatement ps, IExportToMap model) throws QueryBuilderException, SQLException{
        this.parameterise(ps, model);
    }
}

