package com.example.ums;

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

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public Map<String, String> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, String> grades) {
        this.grades = grades;
    }

    public Map<String, String> getFeedback() {
        return feedback;
    }

    public void setFeedback(Map<String, String> feedback) {
        this.feedback = feedback;
    }
}
