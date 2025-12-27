package com.example.ums;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Instructor extends User {
    private ArrayList<Integer> courses;
    private String salary;
    private String role;
    private boolean departmentHead;
    private String departmentName;
    private ArrayList<String> responsibilities = new ArrayList<>();
    private Map<String, String> officeHours;
    private ArrayList<String> benefits;

    public Instructor() {
        super();
    }

    public Instructor(String id, String phoneNumber, String email, String password, String dateOfBirth, String name,
            String salary, ArrayList<Integer> courses, String role, boolean departmentHead, String departmentName,
            ArrayList<String> responsibilities, Map<String, String> officeHours, ArrayList<String> benefits) {
        super(id, "Instructor", phoneNumber, email, password, name, dateOfBirth);
        this.salary = salary;
        this.courses = courses;
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = departmentName;
        this.responsibilities = responsibilities;
        this.officeHours = officeHours;
        this.benefits = benefits;
    }

    public Instructor(String id, String phoneNumber, String email, String password, String dateOfBirth, String name,
            String department, boolean departmentHead, String role) {
        super(id, "Instructor", phoneNumber, email, password, name, dateOfBirth);
        this.salary = "0";
        this.courses = new ArrayList<Integer>();
        this.role = role;
        this.departmentHead = departmentHead;
        this.departmentName = department;
        this.responsibilities = new ArrayList<String>();
        this.officeHours = new HashMap<>();
        this.benefits = new ArrayList<String>();
    }

    public ArrayList<Integer> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Integer> courses) {
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

    public Map<String, String> getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(Map<String, String> officeHours) {
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
        dm.updateUserAttribute(6, userId, newRole);
    }

    public void updateResponsibilities(String userId, ArrayList<String> newResponsibilities,
            ArrayList<String> oldResponsibilities) {
        ArrayList<String> updatedList = newResponsibilities != null ? new ArrayList<String>(newResponsibilities)
                : new ArrayList<String>();
        ArrayList<String> existingList = oldResponsibilities != null ? new ArrayList<String>(oldResponsibilities)
                : new ArrayList<String>();

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

    public void addCourse(String courseName, String courseDescription, String year) {
        Course course = new Course(courseName, courseDescription, year);
        dm.addCourse(course);
    }

    public void deleteCourse(int courseId) {

        dm.deleteCourse(courseId);
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

    public ArrayList<Course> getInstructorCourses(String userId) {
        ArrayList<Course> courses = new ArrayList<>();
        try {
            courses = dm.getInstructorCourses(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public ArrayList<Course> getDepartmentCourses(String department) {
        ArrayList<Course> courses = new ArrayList<>();
        try {
            courses = dm.getDepartmentCourses(department);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courses;
    }

    public ArrayList<Student> getStudentsByMajor(String major) {
        ArrayList<Student> students = new ArrayList<>();
        try {
            students = dm.getStudentsByMajor(major);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
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

    public ArrayList<Assignment> displayAssignments(int courseId) {
        ArrayList<Assignment> list = new ArrayList<>();
        try {
            list = dm.getAssignments(courseId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Assignment displayAssignmentDetails(int assignmentId) {
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

    public void addAssignmentGrade(int assignmentId, String userId, String grade) {
        dm.addAssignmentGrade(assignmentId, userId, grade);
    }

    public void addAssignmentFeedback(int assignmentId, String userId, String feedback) {
        dm.addAssignmentFeedback(assignmentId, userId, feedback);
    }

    public void addExamGrade(int examId, String userId, String grade) {
        dm.addExamGrade(examId, userId, grade);
    }

    public void addExamFeedback(int examId, String userId, String feedback) {
        dm.addExamFeedback(examId, userId, feedback);
    }

    public ArrayList<Integer> displayInstructorCourses(String userId) {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            list = dm.getCurrentCourses(userId);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return list;
    }

    public void updateCourses(String userId, ArrayList<Integer> newCourses, ArrayList<Integer> oldCourses) {
        ArrayList<Integer> updatedList = newCourses != null ? new ArrayList<Integer>(newCourses)
                : new ArrayList<Integer>();
        ArrayList<Integer> existingList = oldCourses != null ? new ArrayList<Integer>(oldCourses)
                : new ArrayList<Integer>();

        ArrayList<Integer> toAdd = new ArrayList<Integer>(updatedList);
        toAdd.removeAll(existingList);

        ArrayList<Integer> toRemove = new ArrayList<Integer>(existingList);
        toRemove.removeAll(updatedList);

        for (int courseId : toAdd) {
            try {
                dm.addCurrentCourse(userId, courseId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        for (int courseId : toRemove) {
            try {
                dm.removeCurrentCourse(userId, courseId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateCourseMaterials(int courseId, ArrayList<Material> newMaterial, ArrayList<Material> oldMaterial) {
        ArrayList<Material> updatedList = newMaterial != null ? new ArrayList<Material>(newMaterial)
                : new ArrayList<Material>();
        ArrayList<Material> existingList = oldMaterial != null ? new ArrayList<Material>(oldMaterial)
                : new ArrayList<Material>();

        ArrayList<Material> toAdd = new ArrayList<Material>(updatedList);
        toAdd.removeAll(existingList);

        ArrayList<Material> toRemove = new ArrayList<Material>(existingList);
        toRemove.removeAll(updatedList);

        for (Material material : toAdd) {
            dm.addMaterial(courseId, material);
        }
        for (Material material : toRemove) {
            dm.deleteMaterial(material.getMaterialId());
        }
    }

    public void updateOfficeHours(String userId, Map<String, String> newHours, Map<String, String> oldHours) {
        Map<String, String> updated = newHours != null ? new HashMap<>(newHours) : new HashMap<>();
        Map<String, String> existing = oldHours != null ? new HashMap<>(oldHours) : new HashMap<>();

        // Remove entries that disappeared or changed
        for (Map.Entry<String, String> entry : existing.entrySet()) {
            String day = entry.getKey();
            String oldHour = entry.getValue();
            String newHour = updated.get(day);
            if (newHour == null || !newHour.equals(oldHour)) {
                dm.deleteOfficeHours(userId, day, oldHour);
            }
        }

        // Add new or changed entries
        Map<String, String> toAdd = new HashMap<>();
        for (Map.Entry<String, String> entry : updated.entrySet()) {
            String day = entry.getKey();
            String newHour = entry.getValue();
            String oldHour = existing.get(day);
            if (newHour != null && !newHour.equals(oldHour)) {
                toAdd.put(day, newHour);
            }
        }
        if (!toAdd.isEmpty()) {
            dm.addOfficeHours(userId, toAdd);
        }
    }

    public void addOfficeHours(String userId, Map<String, String> officeHours) {
        dm.addOfficeHours(userId, officeHours);
    }

    public void addMaterial(int courseID, Material material) {
        dm.addMaterial(courseID, material);
    }

    public ArrayList<Material> displayCourseMaterials(int courseID) {
        ArrayList<Material> list = new ArrayList<>();
        try {
            list = dm.getMaterials(courseID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Announcment> getInstructorAnnouncments(String studentId) {
        DatabaseManager dm = new DatabaseManager();
        ArrayList<Announcment> list = dm.getStudentAnnouncements(studentId);
        list.addAll(dm.getGeneralAnnouncements());
        return list;
    }

    public void createAnnouncement(Announcment announcment) {
        dm.addAnnouncment(announcment);
    }

    public int getCourseStudentsCount(int courseId) {
        ArrayList<Student> students = dm.getStudentsByCourse(courseId);
        return students.size();
    }

    public ArrayList<Student> getInstructorStudents(String instructorId) {
        ArrayList<Student> allStudents = new ArrayList<>();
        ArrayList<Integer> courses = displayInstructorCourses(instructorId);
        for (int courseId : courses) {
            ArrayList<Student> courseStudents = dm.getStudentsByCourse(courseId);
            for (Student student : courseStudents) {
                if (!allStudents.contains(student)) {
                    allStudents.add(student);
                }
            }
        }
        return allStudents;
    }

    public ArrayList<Assignment> getInstructorAssignments(String instructorId) {
        ArrayList<Assignment> allAssignments = new ArrayList<>();
        ArrayList<Integer> courses = displayInstructorCourses(instructorId);
        for (int courseId : courses) {
            try {
                ArrayList<Assignment> courseAssignments = dm.getAssignments(courseId);
                allAssignments.addAll(courseAssignments);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allAssignments;
    }

    public ArrayList<Exam> getInstructorExams(String instructorId) {
        ArrayList<Exam> allExams = new ArrayList<>();
        ArrayList<Integer> courses = displayInstructorCourses(instructorId);
        for (int courseId : courses) {
            try {
                ArrayList<Exam> courseExams = dm.getCourseAllExams(courseId);
                allExams.addAll(courseExams);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allExams;
    }

    public void bookHall(int hallId) {
        dm.bookClassroom(hallId);
    }

    public ArrayList<Classroom> getAllHalls() {
        try {
            return dm.getAllClassrooms();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ---- Exams (Course) ----
    public ArrayList<Exam> getCourseAllExams(int courseId) {
        return dm.getCourseAllExams(courseId);
    }

    // ---- Exams (Create) ----
    public void addQuiz(Exam exam) {
        dm.addQuiz(exam);
    }

    public void addMidterm(Exam midterm) {
        dm.addMidterm(midterm);
    }

    public void addFinal(Exam finalExam) {
        dm.addFinal(finalExam);
    }

    public ArrayList<Exam> getCourseQuizzes(int courseId) {
        return dm.getCourseQuizzes(courseId);
    }

    public ArrayList<Exam> getCourseMidterms(int courseId) {
        return dm.getCourseMidterms(courseId);
    }

    public ArrayList<Exam> getCourseFinals(int courseId) {
        return dm.getCourseFinals(courseId);
    }

    // ---- Exams (Student) ----
    public ArrayList<Exam> getStudentAllExams(String userId) {
        return dm.getStudentAllExams(userId);
    }

    public ArrayList<Exam> getStudentQuizzes(String userId) {
        return dm.getStudentQuizzes(userId);
    }

    public ArrayList<Exam> getStudentMidterms(String userId) {
        return dm.getStudentMidterms(userId);
    }

    public ArrayList<Exam> getStudentFinals(String userId) {
        return dm.getStudentFinals(userId);
    }
}
