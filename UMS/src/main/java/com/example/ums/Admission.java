package com.example.ums;

import com.google.firebase.database.PropertyName;

public class Admission {
    private String admissionId;
    private String name;
    private String phoneNumber;
    private String email;
    private String dateOfBirth;
    private String major;
    private String highschoolGPA;
    private String status;


    public Admission(String admissionId, String name, String phoneNumber, String email, String dateOfBirth, String major, String highschoolGPA) {
        this.admissionId = admissionId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.major = major;
        this.highschoolGPA = highschoolGPA;
        this.status = "Pending";
    }

    static FirestoreManager fm = FirestoreManager.getInstance();

    public void newAdmission(String name, String phoneNumber, String email, String dateOfBirth, String major, String highschoolGPA) {

        Admission.validateInputs(name, phoneNumber, email, dateOfBirth, major, highschoolGPA);

        Admission admission = new Admission(null, name, phoneNumber, email, dateOfBirth, major, highschoolGPA);

        fm.addAdmission(admission);
    }




    public static void validateInputs(String name, String phoneNumber, String email,
                                      String dateOfBirth, String major, String highschoolGPA) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (dateOfBirth == null || dateOfBirth.isBlank()) {
            throw new IllegalArgumentException("Date of birth cannot be empty.");
        }
        if (major == null || major.isBlank()) {
            throw new IllegalArgumentException("Major cannot be empty.");
        }
        if (highschoolGPA == null || highschoolGPA.isBlank()) {
            throw new IllegalArgumentException("High school GPA cannot be empty.");
        }
    }





    @PropertyName("status")
    public String getStatus() {
        return status;
    }
    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    @PropertyName("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @PropertyName("admissionId")
    public String getAdmissionId() {
        return admissionId;
    }
    @PropertyName("admissionId")
    public void setAdmissionId(String admissionId) {
        this.admissionId = admissionId;
    }
    @PropertyName("name")
    public String getName() {
        return name;
    }
    @PropertyName("name")
    public void setName(String name) {
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
    @PropertyName("dateOfBirth")
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    @PropertyName("dateOfBirth")
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    @PropertyName("major")
    public String getMajor() {
        return major;
    }
    @PropertyName("major")
    public void setMajor(String major) {
        this.major = major;
    }
    @PropertyName("highschoolGPA")
    public String getHighschoolGPA() {
        return highschoolGPA;
    }
    @PropertyName("highschoolGPA")
    public void setHighschoolGPA(String highschoolGPA) {
        this.highschoolGPA = highschoolGPA;
    }
}
