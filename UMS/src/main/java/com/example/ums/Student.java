package com.example.ums;

import java.util.ArrayList;
import java.util.Map;

public class Student extends User {
    private String DateOfBirth;
    private ArrayList<String> currentCourses;
    private Map<String, String> takenCourses;
    private String GPA;
    private String semester;
    private String major;

    public Student(String username, String password, String role, String phoneNumber, String dateOfBirth, ArrayList<String> currentCourses, Map<String, String> takenCourses, String GPA, String semester, String major) {
        super(username, password, role, phoneNumber);
        DateOfBirth = dateOfBirth;
        this.currentCourses = currentCourses;
        this.takenCourses = takenCourses;
        this.GPA = GPA;
        this.semester = semester;
        this.major = major;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public ArrayList<String> getCurrentCourses() {
        return currentCourses;
    }

    public void setCurrentCourses(ArrayList<String> currentCourses) {
        this.currentCourses = currentCourses;
    }

    public Map<String, String> getTakenCourses() {
        return takenCourses;
    }

    public void setTakenCourses(Map<String, String> takenCourses) {
        this.takenCourses = takenCourses;
    }

    public String getGPA() {
        return GPA;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
