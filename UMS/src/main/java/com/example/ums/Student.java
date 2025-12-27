package com.example.ums;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class Student extends User {
    private ArrayList<Integer> currentCourses;
    private Map<Integer,Map<String,String>> takenCourses;
    private String gpa;
    private String semester;
    private String major;
    private DatabaseManager dm = new DatabaseManager();


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
    public  ArrayList<Announcment> getStudentAnnouncements(String studentId) {
        DatabaseManager dm = new DatabaseManager();
        ArrayList<Announcment> list = dm.getStudentAnnouncements(studentId);
        list.addAll(dm.getGeneralAnnouncements());
        return list;
    }

    public ArrayList<Assignment> getStudentAssignments(String userId) {
        ArrayList<Assignment> allAssignments = new ArrayList<>();
        try {
            ArrayList<Integer> courses = dm.getCurrentCourses(userId);
            for (int courseId : courses) {
                allAssignments.addAll(dm.getAssignments(courseId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allAssignments;
    }

    public ArrayList<Exam> getStudentAllExams(String userId) {
        return dm.getStudentAllExams(userId);
    }

    public ArrayList<Exam> getStudentQuizzes(String userId) {
        return dm.getStudentQuizzes(userId);
    }

    public ArrayList<Exam> getStudentMidterms(String userId) {
        return dm.getStudentMidterms(userId);
    }

    public ArrayList<Exam> getStudentFinals(String userId) {
        return dm.getStudentFinals(userId);
    }

    public String getExamGradeForStudent(int examId, String userId) {
        return dm.getExamGradeForStudent(examId, userId);
    }

    public String getAssignmentGradeForStudent(int assignmentId, String userId) {
        return dm.getAssignmentGradeForStudent(assignmentId, userId);
    }

    public String getAssignmentFeedbackForStudent(int assignmentId, String userId) {
        return dm.getAssignmentFeedbackForStudent(assignmentId, userId);
    }

    public String getExamFeedbackForStudent(int examId, String userId) {
        return dm.getExamFeedbackForStudent(examId, userId);
    }

    public Map<String, String> getStudentCourseGrades(String userId, Integer courseId) {
        return dm.getStudentCourseGrades(userId, courseId);
    }

    public Map<String, String> getStudentCourseFeedback(String userId, Integer courseId) {
        return dm.getStudentCourseFeedback(userId, courseId);
    }
}
