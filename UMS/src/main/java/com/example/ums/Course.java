package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;

public class Course {
    private int courseId;
    private String courseName;
    private String courseDescription;
    private String year;
    private ArrayList<String> material;
    private ArrayList<String> assignments;
    private ArrayList<String> quizzes;
    private Map<String, String> midterm;
    private Map<String, String> finals;
    private String courseDepartment;


    public Course(int courseId, String courseName, String courseDescription, String year, ArrayList<String> material, ArrayList<String> assignments, ArrayList<String> quizzes, Map<String, String> midterm, Map<String, String> finals, ArrayList<String> students) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.year = year;
        this.material = material;
        this.assignments = assignments;
        this.quizzes = quizzes;
        this.midterm = midterm;
        this.finals = finals;

    }

    public Course(int courseId, String courseName, String courseDescription, String year, String courseDepartment) {
        this(courseId, courseName, courseDescription, year);
        this.courseDepartment = courseDepartment;
    }

    public Course(int courseId, String courseName, String courseDescription, String year) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.year = year;
    }

    public Course(String courseName, String courseDescription, String year) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.year = year;
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public void setCourseDepartment(String courseDepartment) {
        this.courseDepartment = courseDepartment;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ArrayList<String> getMaterial() {
        return material;
    }

    public void setMaterial(ArrayList<String> material) {
        this.material = material;
    }

    public ArrayList<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(ArrayList<String> assignments) {
        this.assignments = assignments;
    }

    public ArrayList<String> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(ArrayList<String> quizzes) {
        this.quizzes = quizzes;
    }

    public Map<String, String> getMidterm() {
        return midterm;
    }

    public void setMidterm(Map<String, String> midterm) {
        this.midterm = midterm;
    }

    public Map<String, String> getFinals() {
        return finals;
    }

    public void setFinals(Map<String, String> finals) {
        this.finals = finals;
    }
    
    public static ArrayList<Instructor> getCourseInstructors(int courseId) {
        try {
            DatabaseManager dm = new DatabaseManager();
            return dm.getCourseInstructors(courseId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static ArrayList<Material> getCourseMaterials(int courseId) {
        try {
            DatabaseManager dm = new DatabaseManager();
            return dm.getMaterials(courseId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static Course getCourseById(int courseId) {
        try {
            DatabaseManager dm = new DatabaseManager();
            return dm.getCourse(courseId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
