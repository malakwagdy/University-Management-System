package com.example.ums;

import java.util.ArrayList;

public class Parent extends User{
    private String relation;
    private ArrayList<String> children;

    public Parent(String username, String phoneNumber, String email, String password, String name, String relation, ArrayList<String> children) {
        super(username, phoneNumber, email, password, name);
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
