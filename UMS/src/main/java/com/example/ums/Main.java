package com.example.ums;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        String studentId = "25S002";
        
        System.out.println("Testing getStudent with ID: " + studentId);
        System.out.println("----------------------------------------");
        
        try {
            Student student = dbManager.getStudent(studentId);
            
            if (student != null) {
                System.out.println("Student found!");
                System.out.println("ID: " + student.getId());
                System.out.println("Name: " + student.getName());
                System.out.println("Email: " + student.getEmail());
                System.out.println("Phone Number: " + student.getPhoneNumber());
                System.out.println("Date of Birth: " + student.getdateOfBirth());
                System.out.println("GPA: " + student.getGpa());
                System.out.println("Major: " + student.getMajor());
                System.out.println("Semester: " + student.getSemester());
            } else {
                System.out.println("Student not found with ID: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving student: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
