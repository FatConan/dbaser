package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.models.interfaces.fields.IPullFromResultSet;
import de.themonstrouscavalca.dbaser.queries.interfaces.IMapParameters;
import de.themonstrouscavalca.dbaser.utils.ModelPopulator;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;
import de.themonstrouscavalca.dbaser.utils.ResultSetTableAware;
import de.themonstrouscavalca.dbaser.utils.interfaces.IProcessResultSetFields;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

public class ComplexModel extends BasicIdentifiedModel{
    @Override
    public String getTablePrefix(){
        return "complex";
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

    //We can override the fieldFromRS method to automatically namespace the lookups
    @Override
    public void fieldFromRS(String field, ResultSetTableAware rs, IPullFromResultSet handler) throws SQLException{
        String f = this.getTablePrefixedFieldName(field);
        super.fieldFromRS(f, rs, handler);
    }

    @Override
    protected void setRemainderFromResultSet(ResultSetTableAware rs) throws SQLException{
        //While the above is a valid way to process this, however we can manipulate the IProcessResultSetFields help to make processing less verbose:
        this.stringFieldFromRS("text_entry", rs, this::setTextEntry);
        this.nullLongFieldFromRS("long_entry", rs, this::setLongEntry);
        this.nullIntegerFieldFromRS("int_entry", rs, this::setIntEntry);
        this.nullDoubleFieldFromRS("double_entry", rs, this::setDoubleEntry);
        this.nullFloatFieldFromRS("float_entry", rs, this::setFloatEntry);
        this.localDateFieldFromRS("date_entry", rs, this::setDateEntry);
        this.localTimeFieldFromRS("time_entry", rs, this::setTimeEntry);
        this.localDateTimeFieldFromRS("datetime_entry", rs, this::setDatetimeEntry);
        this.longFieldFromRS("user_entry", rs, (userId) -> {
            SimpleExampleUserModel user = new SimpleExampleUserModel();
            user.setId(userId);
            this.setUserEntry(user);
        });
    }
}
