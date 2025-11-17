package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class Parent extends User{
    private String relation;
    private ArrayList<String> children;

    public Parent(String username, String phoneNumber, String email, String password, String name, String relation, ArrayList<String> children) {
        super(username, phoneNumber, email, password, name);
        this.relation = relation;
        this.children = children;
    }
    @PropertyName("relation")
    public String getRelation() {
        return relation;
    }
    @PropertyName("relation")
    public void setRelation(String relation) {
        this.relation = relation;
    }
    @PropertyName("children")
    public ArrayList<String> getChildren() {
        return children;
    }
    @PropertyName("children")
    public void setChildren(ArrayList<String> children) {
        this.children = children;
    }
}
