package de.themonstrouscavalca.dbaser.models;

import de.themonstrouscavalca.dbaser.models.impl.BasicIdentifiedModel;
import de.themonstrouscavalca.dbaser.utils.ResultSetChecker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SimpleExampleUserModel extends BasicIdentifiedModel{
    private String name;
    private String jobTitle;
    private Integer age;
    private String passwordHash;
    private String passwordSalt;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getJobTitle(){
        return jobTitle;
    }

    public void setJobTitle(String jobTitle){
        this.jobTitle = jobTitle;
    }

    public Integer getAge(){
        return age;
    }

    public void setAge(Integer age){
        this.age = age;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt(){
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt){
        this.passwordSalt = passwordSalt;
    }

    public Map<String, Object> exportToMap(){
        Map<String, Object> exportMap = this.baseExportToMap();
        exportMap.put("name", this.getName());
        exportMap.put("job_title", this.getJobTitle());
        exportMap.put("age", this.getAge());
        exportMap.put("password_hash", this.getPasswordHash());
        exportMap.put("password_salt", this.getPasswordSalt());
        return exportMap;
    }

    @Override
    protected void setRemainderFromResultSet(ResultSetChecker checker, ResultSet rs) throws SQLException{
        if(checker.has("name")){
            this.setName(rs.getString("name"));
        }

        if(checker.has("job_title")){
            this.setJobTitle(rs.getString("job_title"));
        }

        if(checker.has("age")){
            this.setAge(rs.getInt("age"));
        }

        if(checker.has("password_hash")){
            this.setPasswordHash(rs.getString("password_hash"));
        }

        if(checker.has("password_salt")){
            this.setPasswordSalt(rs.getString("password_salt"));
        }
    }
}
