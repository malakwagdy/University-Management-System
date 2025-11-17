package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.Map;

public class Assignment {
    private String assignmentId;
    private String assignmentName;
    private String url;
    private Map<String, String> grades;
    private Map<String, String> feedback;

    public Assignment(String assignmentId, String assignmentName, String url, Map<String, String> grades, Map<String, String> feedback) {
        this.assignmentId = assignmentId;
        this.assignmentName = assignmentName;
        this.url = url;
        this.grades = grades;
        this.feedback = feedback;
    }
    @PropertyName("assignmentId")
    public String getAssignmentId() {
        return assignmentId;
    }
    @PropertyName("assignmentId")
    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }
    @PropertyName("url")
    public String getUrl() {
        return url;
    }
    @PropertyName("url")
    public void setUrl(String url) {
        this.url = url;
    }
    @PropertyName("assignmentName")
    public String getAssignmentName() {
        return assignmentName;
    }
    @PropertyName("assignmentName")
    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }
    @PropertyName("grades")
    public Map<String, String> getGrades() {
        return grades;
    }
    @PropertyName("grades")
    public void setGrades(Map<String, String> grades) {
        this.grades = grades;
    }
    @PropertyName("feedback")
    public Map<String, String> getFeedback() {
        return feedback;
    }
    @PropertyName("feedback")
    public void setFeedback(Map<String, String> feedback) {
        this.feedback = feedback;
    }
}
