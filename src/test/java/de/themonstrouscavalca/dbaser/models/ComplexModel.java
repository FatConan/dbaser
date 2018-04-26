package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;

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
    public Map<String, Object> exportToMap(){
        Map<String, Object> exportMap = this.baseExportToMap();
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
    protected void setRemainderFromResultSet(ResultSetChecker checker, ResultSet rs) throws SQLException{
        if(checker.has(this.getTablePrefixedFieldName("text_entry"))){
            this.setTextEntry(rs.getString(this.getTablePrefixedFieldName("text_entry")));
        }
        if(checker.has(this.getTablePrefixedFieldName("long_entry"))){
            this.setLongEntry(rs.getLong(this.getTablePrefixedFieldName("long_entry")));
        }
        if(checker.has(this.getTablePrefixedFieldName("int_entry"))){
            this.setIntEntry(rs.getInt(this.getTablePrefixedFieldName("int_entry")));
        }
        if(checker.has(this.getTablePrefixedFieldName("double_entry"))){
            this.setDoubleEntry(rs.getDouble(this.getTablePrefixedFieldName("double_entry")));
        }
        if(checker.has(this.getTablePrefixedFieldName("float_entry"))){
            this.setFloatEntry(rs.getFloat(this.getTablePrefixedFieldName("float_entry")));
        }
        if(checker.has(this.getTablePrefixedFieldName("date_entry"))){
            this.setDateEntry(rs.getDate(this.getTablePrefixedFieldName("date_entry")).toLocalDate());
        }
        if(checker.has(this.getTablePrefixedFieldName("time_entry"))){
            this.setTimeEntry(rs.getTime(this.getTablePrefixedFieldName("time_entry")).toLocalTime());
        }
        if(checker.has(this.getTablePrefixedFieldName("datetime_entry"))){
            this.setDatetimeEntry(rs.getTimestamp(this.getTablePrefixedFieldName("datetime_entry")).toLocalDateTime());
        }
        if(checker.has(this.getTablePrefixedFieldName("user_entry"))){
            SimpleExampleUserModel user = new SimpleExampleUserModel();
            user.setId(rs.getLong(this.getTablePrefixedFieldName("user_entry")));
            this.setUserEntry(user);
        }
    }
}
