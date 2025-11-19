package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Admin extends User {
    private String salary;

    public Admin() {
        super();
    }

    public Admin(String phoneNumber, String email, String password, String name, String salary) {
        super(phoneNumber, email, password, name);
        this.salary = salary;
    }
    @PropertyName("salary")
    public String getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(String salary) {
        this.salary = salary;
    }
}
