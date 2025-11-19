package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class Admin extends User {
    private String salary;

    public Admin() {
        super();
    }

    public Admin(String phoneNumber, String email, String password, String name, String salary) {
        super(phoneNumber, email, password, name);
        this.salary = salary;
    }
    @PropertyName("salary")
    public String getSalary() {
        return salary;
    }
    @PropertyName("salary")
    public void setSalary(String salary) {
        this.salary = salary;
    }

    public ArrayList<Admission> getAdmissions() {
        return fm.getAllAdmissions();
    }
    public ArrayList<Admission> getAdmissionsByStatus(String status) {
        return fm.getAdmissionsByStatus(status);
    }
    public void acceptAdmission(Admission admission) {
        String id= this.generateID("student");
        String email = id+"@ums.edu";
        Student student=new Student(id, admission.getPhoneNumber(), email,"12345", admission.getName(), admission.getDateOfBirth(), admission.getMajor());
        admission.setStatus("Accepted");
        fm.addStudent(student);
        fm.updateAdmissionStatus(admission.getAdmissionId(),"Accepted");
    }

    public void rejectAdmission(Admission admission) {
        admission.setStatus("Rejected");
        fm.updateAdmissionStatus(admission.getAdmissionId(),"Rejected");
    }
    public String generateID(String type) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String letter;
        String collection;


        switch (type.toLowerCase()) {
            case "student":
                letter = "S";
                collection = "Student";
                break;
            case "instructor":
                letter = "I";
                collection = "Instructor";
                break;
            case "parent":
                letter = "P";
                collection = "Parent";
                break;
            case "hr":
                letter = "H";
                collection = "HR";
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
        int highest = fm.getHighestIdNumber(collection, letter);
        int newNumber = highest + 1;
        String number = String.format("%03d", newNumber);
        return year + letter + number;
    }

}
