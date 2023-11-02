package de.themonstrouscavalca.dbaser;

public class InvalidSQLiteDatabase extends SQLiteDatabase{
    private static final String DB_FILE = "invalid_test.db";

    @Override
    protected String getUrl(){
        return String.format(SQLITE_URL_FORMAT, DB_FILE);
    }

    @Override
    protected String getDBFile(){
        return DB_FILE;
    }
}
