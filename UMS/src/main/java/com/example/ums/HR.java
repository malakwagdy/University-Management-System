package com.example.ums;

import java.util.ArrayList;

public class HR extends User {
    private String salary;
    private String departmentName;


    public HR() {
        super();
    }

    public HR( String id, String phoneNumber, String email, String password, String dateOfBirth, String name, String salary, String departmentName) {
        super( id, "HR", phoneNumber, email, password, name, dateOfBirth);
        this.salary = salary;
        this.departmentName = departmentName;
    }
    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    static DatabaseManager dm = new DatabaseManager();


    public void updateSalary(String userId, String newSalary) {
        // Update salary in the database
        dm.updateUserAttribute(5 ,userId, newSalary);
    }
    public ArrayList<String> displayBenefits(String userId) {
        // Display benefits in the database

        ArrayList<String> list = new ArrayList<>();
        try {
            list = dm.getBenefits(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public void updateBenefits(String userId, ArrayList<String> newBenefits,ArrayList<String> oldBenefits) {
        ArrayList<String> updatedList = newBenefits != null ? new ArrayList<String>(newBenefits) : new ArrayList<String>();
        ArrayList<String> existingList = oldBenefits != null ? new ArrayList<String>(oldBenefits) : new ArrayList<String>();

        ArrayList<String> toAdd = new ArrayList<String>(updatedList);
        toAdd.removeAll(existingList);

        ArrayList<String> toRemove = new ArrayList<String>(existingList);
        toRemove.removeAll(updatedList);

        
        for (String benefit : toAdd) {
            dm.addBenefit(userId, benefit);
        }
        for (String benefit : toRemove) {
            dm.deleteBenefit(userId, benefit);
        }
    }


}
