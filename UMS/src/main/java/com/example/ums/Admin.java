package com.example.ums;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class Admin extends User {
    private String salary;

    static DatabaseManager dm = new DatabaseManager();

    public Admin() {
        super();
    }

    public Admin(String id,String phoneNumber, String email, String password, String dateOfBirth, String name, String salary) {
        super(id, "Admin", phoneNumber, email, password, name, dateOfBirth);
        this.salary = salary;
    }
    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }


    public void createAdmin(String phoneNumber,  String password, String dateOfBirth, String name, String salary) {
        String id= this.generateID("Admin");
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
        Admin admin=new Admin(id, phoneNumber, email, password, dateOfBirth, name, salary);
        dm.addAdmin(admin);
    }

    public void deleteUser(String id){
        dm.deleteUser(id);
    }

    public void updateDepartmentHead(String instructorId, Boolean isDepartmentHead) {
        Instructor instructor = null;
        try {
            instructor = dm.getInstructor(instructorId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (instructor == null) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        instructor.setDepartmentHead(isDepartmentHead);
        try {
            dm.updateInstructor(instructor);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateRole(String instructorId, String role) {
        Instructor instructor = null;
        try {
            instructor = dm.getInstructor(instructorId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        instructor.setRole(role);
        try {
            dm.updateInstructor(instructor);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createHR(String phoneNumber,  String password, String dateOfBirth, String name, String salary, String departmentName) {
        String id= this.generateID("HR");
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
        HR hr=new HR(id, phoneNumber, email, password, dateOfBirth, name, salary, departmentName);
        dm.addHR(hr);
    }
    public void createInstructor(String phoneNumber,  String password, String dateOfBirth, String name, String department,String role,Boolean departmentHead) {
        String id= this.generateID("Instructor");
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
        Instructor instructor=new Instructor(id, phoneNumber, email, password, dateOfBirth, name, department, departmentHead, role);
        dm.addInstructor(instructor);
    }
    public void createParent(String phoneNumber, String password, String dateOfBirth, String name, String relation, ArrayList<String> children) {
        String id= this.generateID("Parent");
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

        Parent parent=new Parent(id, phoneNumber, email, password, name, dateOfBirth, relation, children);
        dm.addParent(parent);
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

        Student s = null;
        try {
            s = dm.getStudent(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (s != null) return s;

        Instructor i = null;
        try {
            i = dm.getInstructor(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (i != null) return i;

        Parent p = null;
        try {
            p = dm.getParent(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (p != null) return p;

        HR hr = null;
        try {
            hr = dm.getHR(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (hr != null) return hr;

        Admin a = null;
        try {
            a = dm.getAdmin(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            Student existing = null;
            try {
                existing = dm.getStudent(s.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (existing != null) mergeStudent(s, existing);
            dm.addStudent(s);
        }

        else if (user instanceof Instructor) {
            Instructor i = (Instructor) user;
            Instructor existing = null;
            try {
                existing = dm.getInstructor(i.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (existing != null) mergeInstructor(i, existing);
            dm.addInstructor(i);
        }

        else if (user instanceof Parent) {
            Parent p = (Parent) user;
            Parent existing = null;
            try {
                existing = dm.getParent(p.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (existing != null) mergeParent(p, existing);
            dm.addParent(p);
        }

        else if (user instanceof HR) {
            HR hr = (HR) user;
            HR existing = null;
            try {
                existing = dm.getHR(hr.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (existing != null) mergeHR(hr, existing);
            dm.addHR(hr);
        }

        else if (user instanceof Admin) {
            Admin a = (Admin) user;
            Admin existing = null;
            try {
                existing = dm.getAdmin(a.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (existing != null) mergeAdmin(a, existing);
            dm.addAdmin(a);
        }
    }
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }


    public ArrayList<Admission> retrieveAdmissions() {
        ArrayList<Admission> admissions = null;
        try {
            admissions = dm.getAllAdmissions();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return admissions;
    }
    public ArrayList<Admission> retrieveAdmissionsByStatus(String status) {
        ArrayList<Admission> admissions = null;
        try {
            admissions = dm.getAdmissionsByStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return admissions;
    }
    public void acceptAdmission(Admission admission) {
        String id= this.generateID("Student");
        String email = id+"@ums.edu";
        Student student=new Student(id, admission.getPhoneNumber(), email,"12345", admission.getName(), admission.getDateOfBirth(), admission.getMajor());
        admission.setStatus("Accepted");
        try {
            dm.addStudent(student);
            dm.updateAdmissionStatus(admission);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejectAdmission(Admission admission) {
        admission.setStatus("Rejected");
        try {
            dm.updateAdmissionStatus(admission);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        switch (type.toLowerCase()) {
            case "student":
                letter = "S";
                break;
            case "instructor":
                letter = "I";
                break;
            case "parent":
                letter = "P";
                break;
            case "hr":
                letter = "H";
                break;
            case "admin":
                letter = "A";
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
        int highest = dm.getHighestIdNumber(type);
        int newNumber = highest + 1;
        String number = String.format("%03d", newNumber);
        return year + letter + number;
    }

    public Map<Course,String>generateTranscript(String studentId) {
        Student student = null;
        try {
            student = dm.getStudent(studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (student == null) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        Map<Course, String> transcript = null;
        try {
            transcript = dm.getTakenCoursesForTranscript(studentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transcript;
    }
    public ArrayList<Course> getAllCourse() {
        ArrayList<Course> courses = null;
        try {
            courses = dm.getAllCourses();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return  courses;
    }
    public void editCourseDetails(int courseId, String courseName, String courseDescription, String year) {
        Course course = null;
        course = dm.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }
        if (courseName != null && !courseName.trim().isEmpty()) {
            course.setCourseName(courseName);
        }
        if (courseDescription != null && !courseDescription.trim().isEmpty()) {
            course.setCourseDescription(courseDescription);
        }
        if (year != null && !year.trim().isEmpty()) {
            course.setYear(year);
        }
        dm.updateCourse(course);
    }

}
