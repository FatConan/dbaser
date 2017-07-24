# dbaser
__dbaser__ is a collection of tools designed to make interacting with databases easier. 
It is not a fully fledged ORM in the mould of something like [Hibernate](http://hibernate.org/), 
but rather a collection of helpers, base classes and interfaces designed to aide developers in constructing their own
 DAO objects in a manner that best suits their own practices.
 
One of the aims of __dbaser__ is to allow developers to take advantage of lower level SQL capabilities, it provides
mechanisms to better handle complex parameterised queries, and provides simple interfaces that may be built against
that allow the population DAO objects that can accommodate SQL queries that are as simple, or as complex as the 
developer requires them to be.

This project is the result of encountering numerous instances in which working with stricter ORM models has become 
 either impractical or unwieldy in the light of the underlying data structures and/or database queries. 

## Key Components
### QueryBuilder
The QueryBuilder class is a simple wrapper around the basic PreparedStatement mechanisms that allow for easier management 
of parameterisation and dynamic query construction.  It is the cornerstone of the project and a number of the other interfaces
have been designed specifically with its use in mind.

QueryBuilder allows the developer to use named parameter replacements and removed the need for the developer to keep track 
of the positional arguments normally required when parameterising PreparedStatements.
 
For example:
Selecting a user form the users table would normally require:
```
String sql = "SELECT * FROM users WHERE user_id = ?";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setLong(1, 10L);
```

Using the QueryBuilder, this would look something like:

```
QueryBuilder query = new QueryBuilder("SELECT * FROM users WHERE user_id = ?<user_id>");
Map<String, Object> params = new HashMap<>();
params.put("user_id", 10L);

PreparedStatement ps = query.fullPrepare(connection, params);
```

This might not look like it's helping a great deal, in and actually in the most basic a cases the QueryBuilder may be more 
of a hindrance than a help, however in more advanced queries, using the QueryBuilder can make dealing with queries far easier to parse:

Consider something a little more convoluted:
```
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
We now need to keep track of the indices of the parameters as we move through the query. If we want to filter against more group ids
 we need to change the query to add more parameter placeholders (we might end up adding something that generates that in clause based 
 on what's provided and then add something that keeps track of the index to add the corresponding parameters at the correct locations).
 
 With the QueryBuilder, however, we don't need to worry about those concerns:
 ```
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
 
 