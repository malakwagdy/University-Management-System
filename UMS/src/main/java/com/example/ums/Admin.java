package com.example.ums;

public class Admin extends User {
    private String salary;

    public Admin(String username, String phoneNumber, String email, String password, String name, String salary) {
        super(username, phoneNumber, email, password, name);
        this.salary = salary;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
