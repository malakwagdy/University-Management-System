package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class Instructor extends User{
    private ArrayList<String> courses;
    private String salary;
    private String role;
    private boolean departmentHead;
    private String departmentName;
    @PropertyName("responsibilities")
    private ArrayList<String> responsibilities = new ArrayList<>();
    private ArrayList<String> officeHours;
    private ArrayList<String> benefits;


    public Instructor() {
        super();
    }

    public Instructor(String id , String phoneNumber, String email, String password, String name, String salary, ArrayList<String> courses, String role, boolean departmentHead, String departmentName, ArrayList<String> responsibilities, ArrayList<String> officeHours, ArrayList<String> benefits) {
        super( id, phoneNumber, email, password, name);
        this.salary = salary;
        this.courses = courses;
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = departmentName;
        this.responsibilities = responsibilities;
        this.officeHours = officeHours;
        this.benefits = benefits;
    }
    public Instructor(String id , String phoneNumber, String email, String password, String name, String department,boolean departmentHead,String role) {
        super( id, phoneNumber, email, password, name);
        this.salary = "0";
        this.courses = new ArrayList<String>();
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = department;
        this.responsibilities = new ArrayList<String>();
        this.officeHours = new ArrayList<String>();
        this.benefits = new ArrayList<String>();
    }


    @PropertyName("courses")
    public ArrayList<String> getCourses() {
        return courses;
    }
    @PropertyName("courses")
    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }
    @PropertyName("salary")
    public String getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(String salary) {
        this.salary = salary;
    }
    @PropertyName("departmentHead")
    public boolean isDepartmentHead() {
        return departmentHead;
    }
    @PropertyName("departmentHead")
    public void setDepartmentHead(boolean departmentHead) {
        this.departmentHead = departmentHead;
    }
    @PropertyName("role")
    public String getRole() {
        return role;
    }
    @PropertyName("role")
    public void setRole(String role) {
        this.role = role;
    }
    @PropertyName("departmentName")
    public String getDepartmentName() {
        return departmentName;
    }
    @PropertyName("departmentName")
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    @PropertyName("responsibilities")
    public ArrayList<String> getResponsibilities() {
        return responsibilities;
    }
    @PropertyName("responsibilities")
    public void setResponsibilities(ArrayList<String> responsibilities) {
        this.responsibilities = responsibilities;
    }
    @PropertyName("officeHours")
    public ArrayList<String> getOfficeHours() {
        return officeHours;
    }
    @PropertyName("officeHours")
    public void setOfficeHours(ArrayList<String> officeHours) {
        this.officeHours = officeHours;
    }
    @PropertyName("benefits")
    public ArrayList<String> getBenefits() {
        return benefits;
    }
    @PropertyName("benefits")
    public void setBenefits(ArrayList<String> benefits) {
        this.benefits = benefits;
    }
}
