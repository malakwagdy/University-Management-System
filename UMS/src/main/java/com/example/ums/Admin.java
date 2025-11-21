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
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
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
            throw new IllegalArgumentException("Salary is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        HR hr=new HR(id, phoneNumber, email, password, name, salary);
        fm.addHR(hr);
    }
    public void createInstructor(String phoneNumber,  String password, String name, String department,String role,Boolean departmentHead) {
        String id= this.generateID("instructor");
        String email = id+"@ums.edu";
        validateCommonFields(phoneNumber, email, password, name);
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role is required");
        }
        Instructor instructor=new Instructor(id, phoneNumber, email, password, name, department, departmentHead, role);
        fm.addInstructor(instructor);
    }
    public void createParent(String phoneNumber, String password, String name, String relation, ArrayList<String> children) {
        String id= this.generateID("parent");
        String email = id+"@ums.edu";

        validateCommonFields(phoneNumber, email, password, name);

        if (relation == null || relation.trim().isEmpty()) {
            throw new IllegalArgumentException("Relation is required");
        }
        if (children == null || children.isEmpty()) {
            throw new IllegalArgumentException("Children list must not be empty");
        }
        // Check if all children IDs are non-empty
        for (String childId : children) {
            if (childId == null || childId.trim().isEmpty()) {
                throw new IllegalArgumentException("Children list contains invalid (empty) child ID");
            }
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        Parent parent=new Parent(id, phoneNumber, email, password, name, relation, children);
        fm.addParent(parent);
    }

    public void updateUser(User user) {
        if (user instanceof Student) {
            Student student = (Student) user;
            Student existing = fm.getStudent(student.getId());
            if (existing != null) {
                if ((student.getCurrentCourses() == null || student.getCurrentCourses().isEmpty()) && existing.getCurrentCourses() != null) {
                    student.setCurrentCourses(existing.getCurrentCourses());
                }
                if ((student.getTakenCourses() == null || student.getTakenCourses().isEmpty()) && existing.getTakenCourses() != null) {
                    student.setTakenCourses(existing.getTakenCourses());
                }
                if (student.getdateOfBirth() == null && existing.getdateOfBirth() != null) {
                    student.setdateOfBirth(existing.getdateOfBirth());
                }
                if (student.getGpa() == null && existing.getGpa() != null) {
                    student.setGpa(existing.getGpa());
                }
                if (student.getSemester() == null && existing.getSemester() != null) {
                    student.setSemester(existing.getSemester());
                }
            }
            fm.addStudent(student);
        }else if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            Instructor existing = fm.getInstructor(instructor.getId());

            if (existing != null) {

                if ((instructor.getCourses() == null || instructor.getCourses().isEmpty())
                        && existing.getCourses() != null) {
                    instructor.setCourses(existing.getCourses());
                }

                if (instructor.getSalary() == null && existing.getSalary() != null) {
                    instructor.setSalary(existing.getSalary());
                }

                if (instructor.getRole() == null && existing.getRole() != null) {
                    instructor.setRole(existing.getRole());
                }

                // boolean → only replace if default (false) and existing is true
                if (!instructor.isDepartmentHead() && existing.isDepartmentHead()) {
                    instructor.setDepartmentHead(existing.isDepartmentHead());
                }

                if (instructor.getDepartmentName() == null && existing.getDepartmentName() != null) {
                    instructor.setDepartmentName(existing.getDepartmentName());
                }

                if ((instructor.getResponsibilities() == null || instructor.getResponsibilities().isEmpty())
                        && existing.getResponsibilities() != null) {
                    instructor.setResponsibilities(existing.getResponsibilities());
                }

                if ((instructor.getOfficeHours() == null || instructor.getOfficeHours().isEmpty())
                        && existing.getOfficeHours() != null) {
                    instructor.setOfficeHours(existing.getOfficeHours());
                }

                if ((instructor.getBenefits() == null || instructor.getBenefits().isEmpty())
                        && existing.getBenefits() != null) {
                    instructor.setBenefits(existing.getBenefits());
                }
            }

            fm.addInstructor(instructor);
        }
        else if (user instanceof Parent) {
            Parent parent = (Parent) user;
            Parent existing = fm.getParent(parent.getId());

            if (existing != null) {

                if (parent.getRelation() == null && existing.getRelation() != null) {
                    parent.setRelation(existing.getRelation());
                }

                if ((parent.getChildren() == null || parent.getChildren().isEmpty())
                        && existing.getChildren() != null) {
                    parent.setChildren(existing.getChildren());
                }
            }

            fm.addParent(parent);
        }else if (user instanceof HR) {
            HR hr = (HR) user;
            HR existing = fm.getHR(hr.getId());

            if (existing != null) {

                if (hr.getSalary() == null && existing.getSalary() != null) {
                    hr.setSalary(existing.getSalary());
                }

                if (hr.getDepartmentName() == null && existing.getDepartmentName() != null) {
                    hr.setDepartmentName(existing.getDepartmentName());
                }
            }

            fm.addHR(hr);
        } else if (user instanceof Admin) {
            Admin admin = (Admin) user;
            Admin existing = fm.getAdmin(admin.getId());

            if (existing != null) {

                if (admin.getSalary() == null && existing.getSalary() != null) {
                    admin.setSalary(existing.getSalary());
                }
            }

            fm.addAdmin(admin);
        }




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
            throw new IllegalArgumentException("Phone number is required");
        }

        if (phoneNumber.length() != 11|| !phoneNumber.startsWith("011") && !phoneNumber.startsWith("012") && !phoneNumber.startsWith("010") && !phoneNumber.startsWith("015")) {
            throw new IllegalArgumentException("Phone number is invalid");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("Email is invalid");
        }

        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
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
            case "admin":
                letter = "A";
                collection = "Admin";
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
