package com.example.ums;

public class HR extends User {
    private String salary;
    private String departmentName;

    public HR(String username, String phoneNumber, String email, String password, String name, String salary, String departmentName) {
        super(username, phoneNumber, email, password, name);
        this.salary = salary;
        this.departmentName = departmentName;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
