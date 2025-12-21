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
        super(id, "Parent", phoneNumber, email, password, name, dateOfBirth);
        this.relation = relation;
        this.children = children;
    }
    public String getRelation() {
        return relation;
    }
    public void setRelation(String relation) {
        this.relation = relation;
    }
    public ArrayList<String> getChildren() {
        return children;
    }
    public void setChildren(ArrayList<String> children) {
        this.children = children;
    }
}
