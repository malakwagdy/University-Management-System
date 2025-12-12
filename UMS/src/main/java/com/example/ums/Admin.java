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
//    public void updateUser(User user){
//        String code = user.getId().substring(2,3);
//        switch (code) {
//            case"A":
//                fm.updateAdmin((Admin) user);
//                break;
//            case "S":
//                fm.updateStudent((Student) user);
//                break;
//            case "I":
//                fm.updateInstructor((Instructor) user);
//                break;
//            case "P":
//                fm.updateParent((Parent) user);
//                break;
//            case "H":
//                fm.updateHR((HR) user);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid ID: " + user.getId());
//        }
//
//    }



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

    public void createHR(String phoneNumber,  String password, String name, String salary, String departmentName) {
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
        HR hr=new HR(id, phoneNumber, email, password, name, salary, departmentName);
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

    private void mergeBaseUserFields(User updated, User existing) {

        updated.setEmail(normalize(updated.getEmail()));
        updated.setPassword(normalize(updated.getPassword()));
        updated.setPhoneNumber(normalize(updated.getPhoneNumber()));
        updated.setName(normalize(updated.getName()));
        updated.setId(normalize(updated.getId()));

        if (updated.getEmail() == null) {
            updated.setEmail(existing.getEmail());
        }

        if (updated.getPassword() == null) {
            updated.setPassword(existing.getPassword());
        }

        if (updated.getPhoneNumber() == null) {
            updated.setPhoneNumber(existing.getPhoneNumber());
        }

        if (updated.getName() == null) {
            updated.setName(existing.getName());
        }

        if (updated.getId() == null) {
            updated.setId(existing.getId());
        }
    }

    private void mergeStudent(Student updated, Student existing) {

        if ((updated.getCurrentCourses() == null || updated.getCurrentCourses().isEmpty())
                && existing.getCurrentCourses() != null) {
            updated.setCurrentCourses(existing.getCurrentCourses());
        }

        if ((updated.getTakenCourses() == null || updated.getTakenCourses().isEmpty())
                && existing.getTakenCourses() != null) {
            updated.setTakenCourses(existing.getTakenCourses());
        }

        if (updated.getdateOfBirth() == null && existing.getdateOfBirth() != null) {
            updated.setdateOfBirth(existing.getdateOfBirth());
        }

        if (updated.getGpa() == null && existing.getGpa() != null) {
            updated.setGpa(existing.getGpa());
        }

        if (updated.getSemester() == null && existing.getSemester() != null) {
            updated.setSemester(existing.getSemester());
        }

        if (updated.getMajor() == null && existing.getMajor() != null) {
            updated.setMajor(existing.getMajor());
        }
    }
    private void mergeInstructor(Instructor updated, Instructor existing) {

        if ((updated.getCourses() == null || updated.getCourses().isEmpty())
                && existing.getCourses() != null) {
            updated.setCourses(existing.getCourses());
        }

        if (updated.getSalary() == null && existing.getSalary() != null) {
            updated.setSalary(existing.getSalary());
        }

        if (updated.getRole() == null && existing.getRole() != null) {
            updated.setRole(existing.getRole());
        }

        if (updated.getDepartmentName() == null && existing.getDepartmentName() != null) {
            updated.setDepartmentName(existing.getDepartmentName());
        }

        if ((updated.getResponsibilities() == null || updated.getResponsibilities().isEmpty())
                && existing.getResponsibilities() != null) {
            updated.setResponsibilities(existing.getResponsibilities());
        }

        if ((updated.getOfficeHours() == null || updated.getOfficeHours().isEmpty())
                && existing.getOfficeHours() != null) {
            updated.setOfficeHours(existing.getOfficeHours());
        }

        if ((updated.getBenefits() == null || updated.getBenefits().isEmpty())
                && existing.getBenefits() != null) {
            updated.setBenefits(existing.getBenefits());
        }
    }
    private void mergeParent(Parent updated, Parent existing) {

        if (updated.getRelation() == null && existing.getRelation() != null) {
            updated.setRelation(existing.getRelation());
        }

        if ((updated.getChildren() == null || updated.getChildren().isEmpty())
                && existing.getChildren() != null) {
            updated.setChildren(existing.getChildren());
        }
    }
    private void mergeHR(HR updated, HR existing) {

        if (updated.getSalary() == null && existing.getSalary() != null) {
            updated.setSalary(existing.getSalary());
        }

        if (updated.getDepartmentName() == null && existing.getDepartmentName() != null) {
            updated.setDepartmentName(existing.getDepartmentName());
        }
    }
    private void mergeAdmin(Admin updated, Admin existing) {

        if (updated.getSalary() == null && existing.getSalary() != null) {
            updated.setSalary(existing.getSalary());
        }
    }
    private User findExistingUserById(String id) {

        Student s = fm.getStudent(id);
        if (s != null) return s;

        Instructor i = fm.getInstructor(id);
        if (i != null) return i;

        Parent p = fm.getParent(id);
        if (p != null) return p;

        HR hr = fm.getHR(id);
        if (hr != null) return hr;

        Admin a = fm.getAdmin(id);
        if (a != null) return a;

        return null;
    }

    public void updateUser(User user) {

        // 1. Get existing user from ANY collection
        User existingGeneral = findExistingUserById(user.getId());

        // 2. Merge base fields if found
        if (existingGeneral != null) {
            mergeBaseUserFields(user, existingGeneral);
        }

        // 3. Merge subclass-specific fields
        if (user instanceof Student) {
            Student s = (Student) user;
            Student existing = fm.getStudent(s.getId());
            if (existing != null) mergeStudent(s, existing);
            fm.addStudent(s);
        }

        else if (user instanceof Instructor) {
            Instructor i = (Instructor) user;
            Instructor existing = fm.getInstructor(i.getId());
            if (existing != null) mergeInstructor(i, existing);
            fm.addInstructor(i);
        }

        else if (user instanceof Parent) {
            Parent p = (Parent) user;
            Parent existing = fm.getParent(p.getId());
            if (existing != null) mergeParent(p, existing);
            fm.addParent(p);
        }

        else if (user instanceof HR) {
            HR hr = (HR) user;
            HR existing = fm.getHR(hr.getId());
            if (existing != null) mergeHR(hr, existing);
            fm.addHR(hr);
        }

        else if (user instanceof Admin) {
            Admin a = (Admin) user;
            Admin existing = fm.getAdmin(a.getId());
            if (existing != null) mergeAdmin(a, existing);
            fm.addAdmin(a);
        }
    }
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }




    public ArrayList<Admission> retrieveAdmissions() {
        return fm.getAllAdmissions();
    }
    public ArrayList<Admission> retrieveAdmissionsByStatus(String status) {
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
