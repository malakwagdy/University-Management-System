package com.example.ums;

import java.time.LocalDate;

public class Admission {
    private int admissionId;
    private String name;
    private String phoneNumber;
    private String email;
    private String dateOfBirth;
    private String major;
    private String highschoolGPA;
    private String status;
    private String yearOfAdmission;

    static DatabaseManager dm = new DatabaseManager();

    // No-arg constructor required for Firestore deserialization
    public Admission() {
    }

    public Admission(int admissionId, String name, String phoneNumber, String email, String dateOfBirth, String major, String highschoolGPA) {
        this.admissionId = admissionId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.major = major;
        this.highschoolGPA = highschoolGPA;
        this.status = "Pending";
        this.yearOfAdmission = String.valueOf(LocalDate.now().getYear());

    }

    public static void newAdmission(String name, String phoneNumber, String email, String dateOfBirth, String major, String highschoolGPA) {

        Admission.validateInputs(name, phoneNumber, email, dateOfBirth, major, highschoolGPA);

        Admission admission = new Admission(0, name, phoneNumber, email, dateOfBirth, major, highschoolGPA);

        try {
            dm.addAdmission(admission);
        } catch (Exception e) {
            System.out.println("Failed to add admission");
            e.printStackTrace();
            return;
        }
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

    public String getYearOfAdmission() {
        return yearOfAdmission;
    }

    public void setYearOfAdmission(String yearOfAdmission) {
        this.yearOfAdmission = yearOfAdmission;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAdmissionId() {
        return admissionId;
    }

    public void setAdmissionId(int admissionId) {
        this.admissionId = admissionId;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getHighschoolGPA() {
        return highschoolGPA;
    }

    public void setHighschoolGPA(String highschoolGPA) {
        this.highschoolGPA = highschoolGPA;
    }
}
