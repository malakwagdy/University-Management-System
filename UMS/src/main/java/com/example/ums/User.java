package com.example.ums;

import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.database.PropertyName;

public class User {
    private String email;
    private String password;
    private String phoneNumber;
    private String name;

    public User() {
    }

    public User( String phoneNumber, String email, String password, String name) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.name = name;
    }
    static FirestoreManager fm = FirestoreManager.getInstance();


    public void Login(String email, String Password) throws LoginException {

        if (email == null) {
            throw new LoginException("Email is incorrect.");
        }
        User user = fm.getAdmin(email);

        if (user == null) {
            user = fm.getInstructor(email);
            if (user != null) {
                if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {
                    LoginController.isInstructor = true;

                } else {
                    throw new LoginException("Password is incorrect.");
                }
            } else  {
                user = fm.getStudent(email);
                if (user != null) {
                    if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

                        LoginController.isStudent = true;

                    } else {
                        throw new LoginException("Password is incorrect.");
                    }
                }else  {
                    user = fm.getHR(email);
                    if (user != null) {
                        if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

                            LoginController.isHr = true;

                        } else {
                            throw new LoginException("Password is incorrect.");
                        }
                    }else {
                        user = fm.getParent(email);
                        if (user != null) {
                            if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

                                LoginController.isParent = true;

                            } else {
                                throw new LoginException("Password is incorrect.");
                            }
                        }else{
                            throw new LoginException("Email is incorrect.");
                        }
                    }
                }
            }
        }else{
            if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

                //LoginController.isAdmin = true;
            }else {
                throw new LoginException("Password is incorrect.");
            }
        }
        //GlobalData.setCurrentlyLoggedIN(email)
    }
    
    @PropertyName("password")
    public String getPassword() {
        return password;
    }
    @PropertyName("password")
    public void setPassword(String password) {
        this.password = password;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }
    @PropertyName("name")
    public void setName(String role) {
        this.name = name;
    }
    @PropertyName("email")
    public String getEmail() {
        return email;
    }
    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }
    @PropertyName("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @PropertyName("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }
}