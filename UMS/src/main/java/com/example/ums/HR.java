package com.example.ums;

import com.google.firebase.database.PropertyName;

public class HR extends User {
    private Float salary;
    private String departmentName;

    public HR(String username, String phoneNumber, String email, String password, String name, Float salary, String departmentName) {
        super(username, phoneNumber, email, password, name);
        this.salary = salary;
        this.departmentName = departmentName;
    }
    @PropertyName("salary")
    public Float getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(Float salary) {
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
