package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Map;

public class Student extends User {
    private ArrayList<String> currentCourses;
    private Map<String,String> takenCourses;
    private String gpa;
    private String semester;
    private String major;


    public Student() {
        super();
    }

    public Student( String studentId, String phoneNumber, String email, String password, String name, String dateOfBirth, ArrayList<String> currentCourses, Map<String,String> takenCourses, String GPA, String semester, String major) {
        super(studentId , "Student", phoneNumber, email, password, name, dateOfBirth);
        this.currentCourses = currentCourses;
        this.takenCourses = takenCourses;
        this.gpa = GPA;
        this.semester = semester;
        this.major = major;
    }
    public Student(String studentId, String phoneNumber, String email, String password, String name, String dateOfBirth,String major){
        super(studentId, "Student", phoneNumber, email, password, name, dateOfBirth);

        this.currentCourses = null;
        this.takenCourses = null;
        this.gpa = "0";
        this.semester = "1";
        this.major = major;
    }
    public ArrayList<String> getCurrentCourses() {
        return currentCourses;
    }
    public void setCurrentCourses(ArrayList<String> currentCourses) {
        this.currentCourses = currentCourses;
    }
    public Map<String,String> getTakenCourses() {
        return takenCourses;
    }
    public void setTakenCourses(Map<String,String> takenCourses) {
        this.takenCourses = takenCourses;
    }
    public String getGpa() {
        return gpa;
    }
    public void setGpa(String GPA) {
        this.gpa = GPA;
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
