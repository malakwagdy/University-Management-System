package com.example.ums;

import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;

public class Student extends User {
    private ArrayList<Integer> currentCourses;
    private Map<Integer,Map<String,String>> takenCourses;
    private String gpa;
    private String semester;
    private String major;


    public Student() {
        super();
    }

    public Student( String studentId, String phoneNumber, String email, String password, String name, String dateOfBirth, ArrayList<Integer> currentCourses, Map<Integer,Map<String,String>> takenCourses, String GPA, String semester, String major) {
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
    public ArrayList<Integer> getCurrentCourses() {
        return currentCourses;
    }
    public void setCurrentCourses(ArrayList<Integer> currentCourses) {
        this.currentCourses = currentCourses;
    }
    public Map<Integer,Map<String,String>> getTakenCourses() {
        return takenCourses;
    }
    public void setTakenCourses(Map<Integer,Map<String,String>> takenCourses) {
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
    
    public static Student getCurrentStudent() {
        try {
            DatabaseManager dm = new DatabaseManager();
            return dm.getStudent(GlobalData.getCurrentlyLoggedIN());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ArrayList<Integer> getCurrentCoursesForStudent(String userId) {
        try {
            DatabaseManager dm = new DatabaseManager();
            return dm.getCurrentCourses(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public  ArrayList<Announcment> getStudentAnnouncments(String studentId) {
        DatabaseManager dm = new DatabaseManager();
        ArrayList<Announcment> list = dm.getStudentAnnouncements(studentId);
        list.addAll(dm.getGeneralAnnouncements());
        return list;
    }
}
