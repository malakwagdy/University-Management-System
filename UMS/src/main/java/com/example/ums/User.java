package com.example.ums;

import java.sql.SQLException;

import com.google.firebase.database.PropertyName;

public class User {
    private String email;
    private String password;
    private String phoneNumber;
    private String name;
    private String id;
    private String type;
    private String dateOfBirth;

    public User() {
    }

    public User( String id, String type, String phoneNumber, String email, String password, String name) {
        this(id, type, phoneNumber, email, password, name, null);
    }

    public User( String id, String type, String phoneNumber, String email, String password, String name, String dateOfBirth) {
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.name = name;
        this.id = id;
        this.dateOfBirth = dateOfBirth;
    }
    static FirestoreManager fm = FirestoreManager.getInstance();
    static DatabaseManager dm = new DatabaseManager();


    // public void Login(String email, String Password) throws LoginException {

    //     if (email == null) {
    //         throw new LoginException("Email is incorrect.");
    //     }
    //     String id = email.substring(0, 6);
    //     User user = fm.getAdmin(id);

    //     if (user == null) {
    //         user = fm.getInstructor(id);
    //         if (user != null) {
    //             if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {
    //                 if (((Instructor) user).isDepartmentHead()) {
    //                     LoginController.isDepartmentHead = true;
    //                 } else {
    //                     LoginController.isInstructor = true;
    //                 }
    //             } else {
    //                 throw new LoginException("Password is incorrect.");
    //             }
    //         } else  {
    //             user = fm.getStudent(id);
    //             if (user != null) {
    //                 if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

    //                     LoginController.isStudent = true;

    //                 } else {
    //                     throw new LoginException("Password is incorrect.");
    //                 }
    //             }else  {
    //                 user = fm.getHR(id);
    //                 if (user != null) {
    //                     if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

    //                         LoginController.isHr = true;

    //                     } else {
    //                         throw new LoginException("Password is incorrect.");
    //                     }
    //                 }else {
    //                     user = fm.getParent(id);
    //                     if (user != null) {
    //                         if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

    //                             LoginController.isParent = true;

    //                         } else {
    //                             throw new LoginException("Password is incorrect.");
    //                         }
    //                     }else{
    //                         throw new LoginException("Email is incorrect.");
    //                     }
    //                 }
    //             }
    //         }
    //     }else{
    //         if (user.getEmail().equals(email) && user.getPassword().equals(Password)) {

    //             //LoginController.isAdmin = true;
    //         }else {
    //             throw new LoginException("Password is incorrect.");
    //         }
    //     }
    //     GlobalData.setCurrentlyLoggedIN(id);
    // }

    public void Login(String email, String Password) throws LoginException {

        if (email == null) {
            throw new LoginException("Incorrect email or password.");
        }
        String id = email.substring(0, 6);
        User user = dm.getUser(id);
        if (user == null) {
            throw new LoginException("Incorrect email or password.");
        }
        if (user.getEmail().equals(email) && dm.checkPassword(Password, user.getPassword())) {
            GlobalData.setCurrentlyLoggedIN(id);
            switch (user.getType()) {
                case "Instructor":
                try {    
                Instructor instructor = dm.getInstructor(id);
                if (instructor.isDepartmentHead()) {
                    LoginController.isDepartmentHead = true;
                } else {
                    LoginController.isInstructor = true;
                }
                } catch (SQLException e) {
                    throw new LoginException("Incorrect email or password.");
                }
                    break;
                case "Student":
                    LoginController.isStudent = true;
                    break;
                case "HR":
                    LoginController.isHr = true;
                    break;
                case "Parent":
                    LoginController.isParent = true;
                    break;
            }
        } else {
            throw new LoginException("Incorrect email or password.");
        }
    }

    public static void Logout(){
        GlobalData.setCurrentlyLoggedIN(null);
        // Reset all login flags on logout
        LoginController.isInstructor = false;
        LoginController.isStudent = false;
        LoginController.isHr = false;
        LoginController.isDepartmentHead = false;
        LoginController.isParent = false;
    }

    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getdateOfBirth() {
        return dateOfBirth;
    }


    public void setdateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


class LoginException extends Exception {
    public LoginException(String message) {
        super(message);
    }
}