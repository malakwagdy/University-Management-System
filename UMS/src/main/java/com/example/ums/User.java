package com.example.ums;

public class User {
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String name;

    public User(String username, String phoneNumber, String email, String password, String name) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String role) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
