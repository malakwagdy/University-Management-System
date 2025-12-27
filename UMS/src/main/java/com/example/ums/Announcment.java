package com.example.ums;

public class Announcment {
    private int id;
    private String title;
    private String content;
    private String date;
    private Integer courseid;

    public Announcment(String title, String content, String date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Announcment(String title, String content, String date, Integer courseid) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.courseid = courseid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCourseid() {
        return courseid;
    }

    public void setCourseid(Integer courseid) {
        this.courseid = courseid;
    }
}
