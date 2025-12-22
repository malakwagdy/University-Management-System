package com.example.ums;

import java.util.ArrayList;

public class Instructor extends User{
    private ArrayList<String> courses;
    private String salary;
    private String role;
    private boolean departmentHead;
    private String departmentName;
    private ArrayList<String> responsibilities = new ArrayList<>();
    private ArrayList<String> officeHours;
    private ArrayList<String> benefits;


    public Instructor() {
        super();
    }

    public Instructor(String id , String phoneNumber, String email, String password, String dateOfBirth, String name, String salary, ArrayList<String> courses, String role, boolean departmentHead, String departmentName, ArrayList<String> responsibilities, ArrayList<String> officeHours, ArrayList<String> benefits) {
        super( id, "Instructor", phoneNumber, email, password, name, dateOfBirth);
        this.salary = salary;
        this.courses = courses;
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = departmentName;
        this.responsibilities = responsibilities;
        this.officeHours = officeHours;
        this.benefits = benefits;
    }
    public Instructor(String id , String phoneNumber, String email, String password, String dateOfBirth, String name, String department,boolean departmentHead,String role) {
        super( id, "Instructor", phoneNumber, email, password, name, dateOfBirth);
        this.salary = "0";
        this.courses = new ArrayList<String>();
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = department;
        this.responsibilities = new ArrayList<String>();
        this.officeHours = new ArrayList<String>();
        this.benefits = new ArrayList<String>();
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public boolean isDepartmentHead() {
        return departmentHead;
    }

    public void setDepartmentHead(boolean departmentHead) {
        this.departmentHead = departmentHead;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public ArrayList<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(ArrayList<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public ArrayList<String> getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(ArrayList<String> officeHours) {
        this.officeHours = officeHours;
    }

    public ArrayList<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(ArrayList<String> benefits) {
        this.benefits = benefits;
    }
}
