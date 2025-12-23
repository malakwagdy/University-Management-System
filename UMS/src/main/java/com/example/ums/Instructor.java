package com.example.ums;

import java.sql.SQLException;
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
    static DatabaseManager dm = new DatabaseManager();

    public void updateRole(String userId, String newRole) {
        dm.updateUserAttribute(6 ,userId, newRole);
    }
    public void updateResponsibilities(String userId, ArrayList<String> newResponsibilities,ArrayList<String> oldResponsibilities) {
        ArrayList<String> updatedList = newResponsibilities != null ? new ArrayList<String>(newResponsibilities) : new ArrayList<String>();
        ArrayList<String> existingList = oldResponsibilities != null ? new ArrayList<String>(oldResponsibilities) : new ArrayList<String>();

        ArrayList<String> toAdd = new ArrayList<String>(updatedList);
        toAdd.removeAll(existingList);

        ArrayList<String> toRemove = new ArrayList<String>(existingList);
        toRemove.removeAll(updatedList);

        
        for (String Responsibility : toAdd) {
            dm.addResponsibility(userId, Responsibility);
        }
        for (String Responsibility : toRemove) {
            dm.deleteResponsibility(userId, Responsibility);
        }
    }
    public ArrayList<String> displayResponsibilities(String userId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            list = dm.getResponsibilities(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public ArrayList<Assignment> displayAssignments(String courseId) {
        ArrayList<Assignment> list = new ArrayList<>();
        try {
            list = dm.getAssignments(courseId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public Assignment displayAssignmentDetails(String assignmentId) {
        Assignment assignment = null;
        try {
            assignment = dm.getAssignmentDetails(assignmentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assignment;
    }
    public void createAssignment(int courseId, Assignment assignment) {
        dm.addAssignment(courseId, assignment);
        
    }
    public ArrayList<String> displayInstructorCourses(String userId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            list = dm.getCurrentCourses(userId);
        } catch (Exception e) {
            e.printStackTrace();
    
        }
        return list;
    }
    public void updateCourses(String userId, ArrayList<String> newCourses,ArrayList<String> oldCourses) {
        ArrayList<String> updatedList = newCourses != null ? new ArrayList<String>(newCourses) : new ArrayList<String>();
        ArrayList<String> existingList = oldCourses != null ? new ArrayList<String>(oldCourses) : new ArrayList<String>();

        ArrayList<String> toAdd = new ArrayList<String>(updatedList);
        toAdd.removeAll(existingList);

        ArrayList<String> toRemove = new ArrayList<String>(existingList);
        toRemove.removeAll(updatedList);


        for (String Responsibility : toAdd) {
            try {
                dm.addCurrentCourse(userId, Responsibility);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        for (String Responsibility : toRemove) {
            try {
                dm.removeCurrentCourse(userId, Responsibility);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
