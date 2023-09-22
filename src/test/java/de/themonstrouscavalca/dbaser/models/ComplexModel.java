package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ModelPopulator;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class ComplexModel extends BasicIdentifiedModel{
    private final String TABLE_PREFIX = "complex";

    @Override
    public String getTablePrefix(){
        return TABLE_PREFIX;
    }

    private String textEntry;
    private Long longEntry;
    private Integer intEntry;
    private Double  doubleEntry;
    private Float floatEntry;
    private LocalDate dateEntry;
    private LocalTime timeEntry;
    private LocalDateTime datetimeEntry;
    private SimpleExampleUserModel userEntry;

    public String getTextEntry(){
        return textEntry;
    }

    public void setTextEntry(String textEntry){
        this.textEntry = textEntry;
    }

    public Long getLongEntry(){
        return longEntry;
    }

    public void setLongEntry(Long longEntry){
        this.longEntry = longEntry;
    }

    public Integer getIntEntry(){
        return intEntry;
    }

    public void setIntEntry(Integer intEntry){
        this.intEntry = intEntry;
    }

    public Double getDoubleEntry(){
        return doubleEntry;
    }

    public void setDoubleEntry(Double doubleEntry){
        this.doubleEntry = doubleEntry;
    }

    public Float getFloatEntry(){
        return floatEntry;
    }

    public void setFloatEntry(Float floatEntry){
        this.floatEntry = floatEntry;
    }

    public LocalDate getDateEntry(){
        return dateEntry;
    }

    public void setDateEntry(LocalDate dateEntry){
        this.dateEntry = dateEntry;
    }

    public LocalTime getTimeEntry(){
        return timeEntry;
    }

    public void setTimeEntry(LocalTime timeEntry){
        this.timeEntry = timeEntry;
    }

    public LocalDateTime getDatetimeEntry(){
        return datetimeEntry;
    }

    public void setDatetimeEntry(LocalDateTime datetimeEntry){
        this.datetimeEntry = datetimeEntry;
    }

    public SimpleExampleUserModel getUserEntry(){
        return userEntry;
    }

    public void setUserEntry(SimpleExampleUserModel userEntry){
        this.userEntry = userEntry;
    }

    @Override
    public IMapParameters exportToMap(){
        IMapParameters exportMap = this.baseExportToMap();
        exportMap.put("text_entry", this.getTextEntry());
        exportMap.put("long_entry", this.getLongEntry());
        exportMap.put("int_entry", this.getIntEntry());
        exportMap.put("double_entry", this.getDoubleEntry());
        exportMap.put("float_entry", this.getFloatEntry());
        exportMap.put("date_entry", this.getDateEntry());
        exportMap.put("time_entry", this.getTimeEntry());
        exportMap.put("datetime_entry", this.getDatetimeEntry());
        exportMap.put("user_entry", this.getUserEntry());
        return exportMap;
    }

    @Override
    protected void setRemainderFromResultSet(ResultSetTableAware rs) throws SQLException{
        /*
        if(rs.has(this.getTablePrefixedFieldName("text_entry"))){
            this.setTextEntry(rs.getString(this.getTablePrefixedFieldName("text_entry")));
        }
        if(rs.has(this.getTablePrefixedFieldName("long_entry"))){
            this.setLongEntry(rs.getLong(this.getTablePrefixedFieldName("long_entry")));
        }
        if(rs.has(this.getTablePrefixedFieldName("int_entry"))){
            this.setIntEntry(rs.getInt(this.getTablePrefixedFieldName("int_entry")));
        }
        if(rs.has(this.getTablePrefixedFieldName("double_entry"))){
            this.setDoubleEntry(rs.getDouble(this.getTablePrefixedFieldName("double_entry")));
        }
        if(rs.has(this.getTablePrefixedFieldName("float_entry"))){
            this.setFloatEntry(rs.getFloat(this.getTablePrefixedFieldName("float_entry")));
        }
        if(rs.has(this.getTablePrefixedFieldName("date_entry"))){
            this.setDateEntry(rs.getDate(this.getTablePrefixedFieldName("date_entry")).toLocalDate());
        }
        if(rs.has(this.getTablePrefixedFieldName("time_entry"))){
            this.setTimeEntry(rs.getTime(this.getTablePrefixedFieldName("time_entry")).toLocalTime());
        }
        if(rs.has(this.getTablePrefixedFieldName("datetime_entry"))){
            this.setDatetimeEntry(rs.getTimestamp(this.getTablePrefixedFieldName("datetime_entry")).toLocalDateTime());
        }
        if(rs.has(this.getTablePrefixedFieldName("user_entry"))){
            SimpleExampleUserModel user = new SimpleExampleUserModel();
            user.setId(rs.getLong(this.getTablePrefixedFieldName("user_entry")));
            this.setUserEntry(user);
        }*/

        //While the above is a valid way to process this, the ModelPopulator can help to make processing less verbose:
        ModelPopulator.stringFieldFromRS(this.getTablePrefixedFieldName("text_entry"), rs, this::setTextEntry);
        ModelPopulator.longFieldFromRS(this.getTablePrefixedFieldName("long_entry"), rs, this::setLongEntry);
        ModelPopulator.integerFieldFromRS(this.getTablePrefixedFieldName("int_entry"), rs, this::setIntEntry);
        ModelPopulator.doubleFieldFromRS(this.getTablePrefixedFieldName("double_entry"), rs, this::setDoubleEntry);
        ModelPopulator.floatFieldFromRS(this.getTablePrefixedFieldName("float_entry"), rs, this::setFloatEntry);
        ModelPopulator.localDateFieldFromRS(this.getTablePrefixedFieldName("date_entry"), rs, this::setDateEntry);
        ModelPopulator.timestampFieldFromRS(this.getTablePrefixedFieldName("time_entry"), rs,
                (t) -> this.setTimeEntry(t.toLocalDateTime().toLocalTime()));
        ModelPopulator.localDateTimeFieldFromRS(this.getTablePrefixedFieldName("datetime_entry"), rs, this::setDatetimeEntry);
        ModelPopulator.longFieldFromRS(this.getTablePrefixedFieldName("user_entry"), rs, (userId) -> {
            SimpleExampleUserModel user = new SimpleExampleUserModel();
            user.setId(userId);
            this.setUserEntry(user);
        });
    }
}
