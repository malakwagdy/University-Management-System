package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Admin extends User {
    private Float salary;

    public Admin( String phoneNumber, String email, String password, String name, Float salary) {
        super( phoneNumber, email, password, name);
        this.salary = salary;
    }
    @PropertyName("salary")
    public Float getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(Float salary) {
        this.salary = salary;
    }
}
