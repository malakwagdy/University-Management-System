package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Map;

public class Student extends User {
    private String dateOfBirth;
    private ArrayList<String> currentCourses;
    private Map<String, String> takenCourses;
    private String gpa;
    private String semester;
    private String major;
    private String studentId;

    public Student() {
        super();
    }

    public Student( String studentId, String phoneNumber, String email, String password, String name, String dateOfBirth, ArrayList<String> currentCourses, Map<String, String> takenCourses, String GPA, String semester, String major) {
        super( phoneNumber, email, password, name);
        this.dateOfBirth = dateOfBirth;
        this.studentId = studentId;
        this.currentCourses = currentCourses;
        this.takenCourses = takenCourses;
        this.gpa = GPA;
        this.semester = semester;
        this.major = major;
    }
    @PropertyName("studentId")
    public String getStudentID() {return studentId;}
    @PropertyName("studentId")
    public void setStudentID(String studentID) {this.studentId = studentID;}
    @PropertyName("dateOfBirth")
    public String getdateOfBirth() {
        return dateOfBirth;
    }
    @PropertyName("dateOfBirth")
    public void setdateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    @PropertyName("currentCourses")
    public ArrayList<String> getCurrentCourses() {
        return currentCourses;
    }
    @PropertyName("currentCourses")
    public void setCurrentCourses(ArrayList<String> currentCourses) {
        this.currentCourses = currentCourses;
    }
    @PropertyName("takenCourses")
    public Map<String, String> getTakenCourses() {
        return takenCourses;
    }
    @PropertyName("takenCourses")
    public void setTakenCourses(Map<String, String> takenCourses) {
        this.takenCourses = takenCourses;
    }
    @PropertyName("gpa")
    public String getGpa() {
        return gpa;
    }
    @PropertyName("gpa")
    public void setGpa(String GPA) {
        this.gpa = GPA;
    }
    @PropertyName("semester")
    public String getSemester() {
        return semester;
    }
    @PropertyName("semester")
    public void setSemester(String semester) {
        this.semester = semester;
    }
    @PropertyName("major")
    public String getMajor() {
        return major;
    }
    @PropertyName("major")
    public void setMajor(String major) {
        this.major = major;
    }
}
