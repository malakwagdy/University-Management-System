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
    @PropertyName("quizId")
    public String getQuizId() {
        return quizId;
    }
    @PropertyName("quizId")
    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
    @PropertyName("quizName")
    public String getQuizName() {
        return quizName;
    }
    @PropertyName("quizName")
    public void setQuizName(String quizName) {
        this.quizName = quizName;
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
    public void setFeedback(Map<String, String> map) {
        this.feedback = map;
    }
}
