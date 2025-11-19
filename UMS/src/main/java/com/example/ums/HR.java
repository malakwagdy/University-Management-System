package com.example.ums;

import com.google.firebase.database.PropertyName;

public class HR extends User {
    private String salary;
    private String departmentName;


    public HR() {
        super();
    }

    public HR( String phoneNumber, String email, String password, String name, String salary, String departmentName) {
        super( phoneNumber, email, password, name);
        this.salary = salary;
        this.departmentName = departmentName;
    }
    @PropertyName("salary")
    public String getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(String salary) {
        this.salary = salary;
    }
    @PropertyName("departmentName")
    public String getDepartmentName() {
        return departmentName;
    }
    @PropertyName("departmentName")
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
