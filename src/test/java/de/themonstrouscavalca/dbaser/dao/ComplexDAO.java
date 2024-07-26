package de.themonstrouscavalca.dbaser.dao;


import de.themonstrouscavalca.dbaser.SQLiteDatabase;
import de.themonstrouscavalca.dbaser.models.ComplexModel;

public class ComplexDAO extends BasicIdentifiedModelDAO<ComplexModel>{
    public ComplexDAO(){
        this.connectionProvider = new SQLiteDatabase();
    }

    private static final String SELECT_SPECIFIC_SQL = "SELECT * " +
            " FROM complex " +
            " WHERE id = ?<id> ";

    private static final String INSERT_SQL = "INSERT INTO complex (id, text_entry, long_entry, " +
            " int_entry, double_entry, float_entry, date_entry, time_entry, datetime_entry, user_entry) " +
            " VALUES (?<id>, ?<text_entry>, ?<long_entry>, ?<int_entry>, ?<double_entry>, ?<float_entry>, " +
            "   ?<date_entry>, ?<time_entry>, ?<datetime_entry>, ?<user_entry>)";

    private static final String UPDATE_SQL = "UPDATE complex SET " +
            " text_entry=?<text_entry>, " +
            " long_entry=?<long_entry>, " +
            " int_entry=?<int_entry>, " +
            " double_entry=?<double_entry>, " +
            " float_entry=?<float_entry>, " +
            " date_entry=?<date_entry>, " +
            " time_entry=?<time_entry>, " +
            " datetime_entry=?<datetime_entry>, " +
            " user_entry=?<user_entry> " +
            " WHERE id = ?<id> ";

    private static final String DELETE_SQL = "DELETE FROM complex WHERE id = ?<id>";

    private static final String SELECT_LIST_SQL = "SELECT * FROM complex";

    @Override
    protected String getSelectSpecificSQL(){
        return SELECT_SPECIFIC_SQL;
    }

    @Override
    protected String getSelectListSQL(){
        return SELECT_LIST_SQL;
    }

    @Override
    protected String getUpdateSQL(){
        return UPDATE_SQL;
    }

    @Override
    protected String getInsertSQL(){
        return INSERT_SQL;
    }

    @Override
    protected String getDeleteSQL(){
        return DELETE_SQL;
    }

    @Override
    public ComplexModel createInstance(){
        return new ComplexModel();
    }
}
