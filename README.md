# dbaser
__dbaser__ is a collection of tools designed to make interacting with databases easier. 
It is not a fully fledged ORM in the mould of something like [Hibernate](http://hibernate.org/), 
but rather a collection of helpers, classes and interfaces designed to aid developers in constructing their own
 DAO objects in a manner that best suits their own practices.
 
One of the aims of __dbaser__ is to allow developers to take advantage of lower level SQL capabilities, it provides
mechanisms to better handle complex parameterised queries, and provides simple interfaces that may be built against
that allow the population DAO objects that can accommodate SQL queries that are as simple, or as complex as the 
developer requires them to be.

This project is the result of encountering numerous instances in which working with stricter ORM models has become 
 either impractical or unwieldy in the light of the underlying data structures and/or database queries. 

## Key Components
### QueryBuilder
The QueryBuilder class is a simple wrapper around the basic PreparedStatement mechanisms that allow for easier 
management of parameterisation and dynamic query construction.  It is the cornerstone of the project and a number
 of the other interfaces have been designed specifically with its use in mind.

QueryBuilder allows the developer to use named parameter replacements and removes the need for the 
developer to keep track of the positional arguments normally required when parameterising PreparedStatements.
 
For example, selecting a user form the users table would normally require:

```java
String sql = "SELECT * FROM users WHERE user_id = ?";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setLong(1, 10L);
```

Using the QueryBuilder, this would look something like:

```java
QueryBuilder query = new QueryBuilder("SELECT * FROM users WHERE user_id = ?<user_id>");
Map<String, Object> params = new HashMap<>();
params.put("user_id", 10L);

PreparedStatement ps = query.fullPrepare(connection, params);
```

This might not look like it's helping a great deal, actually in the most basic of cases the QueryBuilder may be more 
of a hindrance than a help, however in more advanced queries, using the QueryBuilder can make dealing with 
queries far easier to parse.

Consider something a little more convoluted:

```java
String sql = "WITH age_cte(user_id) AS ( " + 
    " SELECT user_id " +
    " FROM users " +
    " WHERE age > ? " + 
    " AND age < ? ) "+
    " SELECT * FROM users u " +
    " JOIN age_cte ac " +
    " ON (ac.user_id = user_id) " +
    " JOIN user_groups g " +
    " ON (u.user_id = g.user_id) " +
    " WHERE g.group_id IN (?, ?, ?, ?) ";
    
PreparedStatement ps = connection.prepareStatement(sql);
ps.setLong(1, 20L);
ps.setLong(2, 40L);
ps.setLong(3, 5L);
ps.setLong(4, 11L);
ps.setLong(5, 6L);
ps.setLong(6, 9L);
```
We now need to keep track of the indices of the parameters as we move through the query. If we want to filter 
against more group IDs we need to change the query to add more parameter placeholders (we might end up 
adding something that generates that "in" clause based on a list of provided IDs as well as inserting them 
as parameters at the correct indices).
 
With the QueryBuilder, however, we don't need to worry about those concerns:
```java
 QueryBuilder query = new QueryBuilder("WITH age_cte(user_id) AS ( " + 
     " SELECT user_id " +
     " FROM users " +
     " WHERE age > ?<min_age> " + 
     " AND age < ?<max_age> ) "+
     " SELECT * FROM users u " +
     " JOIN age_cte ac " +
     " ON (ac.user_id = user_id) " +
     " JOIN user_groups g " +
     " ON (u.user_id = g.user_id) " +
     " WHERE g.group_id IN (?<group_ids>) ");
 
 Map<String, Object> params = new HashMap<>();
 params.put("min_age", 20L);
 params.put("max_age", 40L);
 params.put("group_ids", Arrays.asList(5L, 11L, 6L, 9L));
 
 PreparedStatement ps = query.fullPrepare(connection, params);
```
So the QueryBuilder allows the dev to construct more semantically meaningful queries and takes the hassle out 
of handling collections of elements (such as the "IN" clause in the above statement). In addition to this, 
QueryBuilder acts similarly to StringBuilders allowing the construction and combination of queries, 
for example, to take advantage of that common table expression we just defined:
 
```java
QueryBuilder query = new QueryBuilder("WITH age_cte(user_id) AS ( " + 
     " SELECT user_id " +
     " FROM users " +
     " WHERE age > ?<min_age> " + 
     " AND age < ?<max_age> ) "+
     " SELECT * FROM users u " +
     " JOIN age_cte ac " +
     " ON (ac.user_id = user_id) " +
     " JOIN user_groups g " +
     " ON (u.user_id = g.user_id) " +
     " WHERE g.group_id IN (?<group_ids>) ");
     
query.append("SELECT * FROM cte WHERE user_id = ?<user_id>");

Map<String, Object> params = new HashMap<>();
params.put("min_age", 20L);
params.put("max_age", 40L);
params.put("group_ids", Arrays.asList(5L, 11L, 6L, 9L));
params.put("user_id", 1L);
 
PreparedStatement ps = query.fullPrepare(connection, params);
```
While the example above simply breaks up the CTE and select statement and joins them together, the QueryBuilder offers 
tools that allow for the replacement of elements within a query allowing the reuse and collation of a number of 
CTEs and sub queries.

```java
final String SQL_STRUCTURE = " WITH [[ CTES ]] SELECT * FROM all_data ";

QueryBuilder CTE_ALL_USERS = new QueryBuilder(" user as ( " +
    " SELECT * from users " +
    " ) ");

QueryBuilder CTE_USERS_AGE_LIMITED = new QueryBuilder(" user as ( " +
    " SELECT user_id " +
    " FROM users " +
    " WHERE age > ?<min_age> " + 
    " AND age < ?<max_age> ) ");

QueryBuilder ALL_DATA_CTE = new QueryBuilder(" all_data AS ( " + 
     " SELECT u.*, g.*
     " FROM users " +
     " LEFT JOIN user_groups ug " +
     " ON (u.user_id = g.user_id) " +
     " LEFT JOIN groups g " +
     " ON (ug.group_id = g.group_id) " +
     " ) ";

QueryBuilder constructed = QueryBuilder.fromString(SQL_STRUCTURE).replace("[[ CTES ]]", QueryBuilider.join(",", CTE_ALL_USERS, ALL_DATA_CTE));

Map<String, Object> params = new HashMap<>();
params.put("min_age", 20L);
params.put("max_age", 40L);
params.put("group_ids", Arrays.asList(5L, 11L, 6L, 9L));
params.put("user_id", 1L);
 
PreparedStatement ps = constructed.fullPrepare(connection, params);
```
Though a convoluted example, the above shows how a query can be built in which parts 
can be substituted during the build process and how, by swapping **CTE_USERS_AGE_LIMITED** for 
**CTE_ALL_USERS** in the **constructed** instantiation line, a different query that can be generate that may still be 
parameterised by a common parameter set. This all aids in the construction of complex queries containing dynamically 
generated sub-queries, clauses and common table expressions. 

###ResultSetTableAware
An awkward bit of nomenclature, but this roughly translates as 
ResultSet (Table Aware), that is, a standard ResultSet that has been modified
to generate a mapping of column names that are "aware" of the respective tables from 
which they are selected.

Designed as a useful mechanism for both disambiguating column names when multiple tables 
contain columns of the same name and for handling partial selections when populating models.

The **ResultSetTableAware** wraps a standard **ResultSet** and utilises the **ResultSetChecker** to augment the
abilities of a ResultSet.

```java
ResultSet rs = preparedStatement.executeQuery();
ResultSetTableAware rsta = new ResultSetTableAware(rs);
```
The **ResultSetTableAware** provides all the mechanisms that the **ResultSet** offers, and for the most part just
calls down to the native ResultSet methods when asked. Where it differs is in accessing columns by name. When 
a column is accessed by name the ResultSetTableAware refers to its internal map of table-aware names to resolve the 
column index of the requested name and passed that down to the underlying ResultSet.

```java
//This looks up "id" in the column map and returns the FIRST corresponding index
Long id = rsta.getLong("id");

//This explicitly looks for the id column of the "table" table
Long id = rsta.getLong("table.id");

//Disambiguates the id column and grabs the index of it from the "table_two" table.
Long secondId = rsta.getLong("table_two.id");
```

In the example above you can see how we can access the column of a specific table using familiar "." accessor notation.
 