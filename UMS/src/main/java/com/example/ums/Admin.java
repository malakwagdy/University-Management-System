package com.example.ums;

import com.google.firebase.database.PropertyName;

import java.time.LocalDate;
import java.util.ArrayList;

public class Admin extends User {
    private String salary;

    public Admin() {
        super();
    }

    public Admin(String id,String phoneNumber, String email, String password, String name, String salary) {
        super(id,phoneNumber, email, password, name);
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


    public void createAdmin(String phoneNumber,  String password, String name, String salary) {
        String id= this.generateID("admin");
        String email = id+"@ums.edu";
        validateCommonFields(phoneNumber, email, password, name);
        if (salary == null || salary.trim().isEmpty()) {
            throw new IllegalArgumentException("salary is required");
        }

        Admin admin=new Admin(id, phoneNumber, email, password, name, salary);
        fm.addAdmin(admin);
    }

    public void deleteUser(String id){
        String code = id.substring(2,3);
        switch (code) {
            case"A":
                fm.deleteAdmin(id);
                break;
            case "S":
                fm.deleteStudent(id);
                break;
            case "I":
                fm.deleteInstructor(id);
                break;
            case "P":
                fm.deleteParent(id);
                break;
            case "H":
                fm.deleteHR(id);
                break;
            default:
                throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }

    public void updateDepartmentHead(String instructorId, Boolean isDepartmentHead) {
        Instructor instructor = fm.getInstructor(instructorId);
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        instructor.setDepartmentHead(isDepartmentHead);
        fm.updateInstructor(instructor);
    }
    public void updateRole(String instructorId, String role) {
        Instructor instructor = fm.getInstructor(instructorId);
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        instructor.setRole(role);
        fm.updateInstructor(instructor);
    }

    public void createHR(String phoneNumber,  String password, String name, String salary) {
        String id= this.generateID("hr");
        String email = id+"@ums.edu";
        validateCommonFields(phoneNumber, email, password, name);
        if (salary == null || salary.trim().isEmpty()) {
            throw new IllegalArgumentException("salary is required");
        }

        HR hr=new HR(id, phoneNumber, email, password, name, salary);
        fm.addHR(hr);
    }
    public void createInstructor(String phoneNumber,  String password, String name, String department,String role,Boolean departmentHead) {
        String id= this.generateID("instructor");
        String email = id+"@ums.edu";
        validateCommonFields(phoneNumber, email, password, name);
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("department is required");
        }

        Instructor instructor=new Instructor(id, phoneNumber, email, password, name, department, departmentHead, role);
        fm.addInstructor(instructor);
    }
    public void createParent(String phoneNumber, String password, String name, String relation, ArrayList<String> children) {
        String id= this.generateID("parent");
        String email = id+"@ums.edu";

        validateCommonFields(phoneNumber, email, password, name);

        if (relation == null || relation.trim().isEmpty()) {
            throw new IllegalArgumentException("relation is required");
        }
        if (children == null) {
            throw new IllegalArgumentException("children list must not  be empty)");
        }

        Parent parent=new Parent(id, phoneNumber, email, password, name, relation, children);
        fm.addParent(parent);
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


    private void validateCommonFields(String phoneNumber, String email, String password, String name) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("phoneNumber is required");
        }

        if (phoneNumber.length() != 11|| !phoneNumber.startsWith("011") && !phoneNumber.startsWith("012") && !phoneNumber.startsWith("010") && !phoneNumber.startsWith("015")) {
            throw new IllegalArgumentException("phoneNumber is invalid");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email is required");
        }
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("email is invalid");
        }

        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("password must be at least 4 characters");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
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
