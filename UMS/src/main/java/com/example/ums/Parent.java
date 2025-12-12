package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class Parent extends User{
    private String relation;
    private ArrayList<String> children;

    public Parent() {
        super();
    }

    public Parent(String id ,String phoneNumber, String email, String password, String name, String dateOfBirth, String relation, ArrayList<String> children) {
        super(id, phoneNumber, email, password, name, dateOfBirth);
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
