package com.example.ums;

import java.util.Map;

public class Quiz {
    private String quizId;
    private String quizName;
    private Map<String, String> grades;
    private Map<String, String> map;

    public Quiz(String quizId, String quizName, Map<String, String> grades, Map<String, String> map) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.grades = grades;
        this.map = map;
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

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
