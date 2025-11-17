package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Map;

public class Course {
    private String courseId;
    private String courseName;
    private String courseDescription;
    private String year;
    private ArrayList<String> material;
    private ArrayList<String> assignments;
    private ArrayList<String> quizzes;
    private Map<String, String> midterm;
    private Map<String, String> finals;


    public Course(String courseId, String courseName, String courseDescription, String year, ArrayList<String> material, ArrayList<String> assignments, ArrayList<String> quizzes, Map<String, String> midterm, Map<String, String> finals, ArrayList<String> students) {
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
    @PropertyName("courseId")
    public String getCourseId() {
        return courseId;
    }
    @PropertyName("courseId")
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    @PropertyName("courseName")
    public String getCourseName() {
        return courseName;
    }
    @PropertyName("courseName")
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    @PropertyName("courseDescription")
    public String getCourseDescription() {
        return courseDescription;
    }
    @PropertyName("courseDescription")
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }
    @PropertyName("year")
    public String getYear() {
        return year;
    }
    @PropertyName("year")
    public void setYear(String year) {
        this.year = year;
    }
    @PropertyName("material")
    public ArrayList<String> getMaterial() {
        return material;
    }
    @PropertyName("material")
    public void setMaterial(ArrayList<String> material) {
        this.material = material;
    }
    @PropertyName("assignments")
    public ArrayList<String> getAssignments() {
        return assignments;
    }
    @PropertyName("assignments")
    public void setAssignments(ArrayList<String> assignments) {
        this.assignments = assignments;
    }
    @PropertyName("quizzes")
    public ArrayList<String> getQuizzes() {
        return quizzes;
    }
    @PropertyName("quizzes")
    public void setQuizzes(ArrayList<String> quizzes) {
        this.quizzes = quizzes;
    }
    @PropertyName("midterm")
    public Map<String, String> getMidterm() {
        return midterm;
    }
    @PropertyName("midterm")
    public void setMidterm(Map<String, String> midterm) {
        this.midterm = midterm;
    }
    @PropertyName("finals")
    public Map<String, String> getFinals() {
        return finals;
    }
    @PropertyName("finals")
    public void setFinals(Map<String, String> finals) {
        this.finals = finals;
    }

}
