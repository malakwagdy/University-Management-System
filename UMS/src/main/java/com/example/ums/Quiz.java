package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.Map;

public class Quiz {
    private String quizId;
    private String quizName;
    private Map<String, String> grades;
    private Map<String, String> feedback;

    public Quiz(String quizId, String quizName, Map<String, String> grades, Map<String, String> map) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.grades = grades;
        this.feedback = map;
    }
    public String getQuizId() {
        return quizId;
    }
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    public String getQuizName() {
        return quizName;
    }
    public void setQuizName(String quizName) {
        this.quizName = quizName;
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
    public void setFeedback(Map<String, String> map) {
        this.feedback = map;
    }
}
