package com.example.ums;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;


public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String JDBC_URL = dotenv.get("DB_URL");

    /**
     * Get a new database connection using environment variables (plain JDBC).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    public static boolean bookClassroom(int hallId) {

            String sql = "UPDATE halls SET availability = false WHERE hallid = ?";

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, hallId);
                return ps.executeUpdate() == 1;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

    public static void AddClassroom(Classroom classroom) throws SQLException {
        String sql =
                "INSERT INTO halls " +
                        "(hallcapacity, halltype)" +
                        "VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classroom.getHallCapacity());
            ps.setString(2, classroom.getHallType());


            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    classroom.setHallId(rs.getInt(1));
                }
            }
        }
    }


    public void addAdmission(Admission admission) throws SQLException {
        String sql = "INSERT INTO Admissions " +
                "(applicantname, email, phonenumber, dateofbirth, highschoolgpa, major, admissionstatus, yearofadmission) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, admission.getName());
            ps.setString(2, admission.getEmail());
            ps.setString(3, admission.getPhoneNumber());
            ps.setString(4, admission.getDateOfBirth());
            ps.setString(5, admission.getHighschoolGPA());
            ps.setString(6, admission.getMajor());
            ps.setString(7, admission.getStatus());
            ps.setString(8, admission.getYearOfAdmission());

            ps.executeUpdate();

            // Get auto-generated primary key
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    admission.setAdmissionId(rs.getInt(1)); // admissionid
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to add admission");
            e.printStackTrace();
            return;
        }
    }

    public ArrayList<Admission> getAllAdmissions() throws SQLException {
        String sql = "SELECT * FROM Admissions";
        ArrayList<Admission> admissions = new ArrayList<>();
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                admissions.add(mapAdmission(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all admissions");
            e.printStackTrace();
            return null;
        }
        return admissions;
    }

    public Admission getAdmissionById(int admissionId) throws SQLException {
        String sql = "SELECT * FROM Admissions WHERE admissionid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAdmission(rs);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get admission by id");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void updateAdmissionStatus(Admission admission) throws SQLException {
        String sql = "UPDATE Admissions SET applicantname = ?, email = ?, phonenumber = ?, dateofbirth = ?, highschoolgpa = ?, yearofadmission = ?, major = ?, admissionstatus = ? WHERE admissionid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admission.getName());
            ps.setString(2, admission.getEmail());
            ps.setString(3, admission.getPhoneNumber());
            ps.setString(4, admission.getDateOfBirth());
            ps.setString(5, admission.getHighschoolGPA());
            ps.setString(6, admission.getYearOfAdmission());
            ps.setString(7, admission.getMajor());
            ps.setString(8, admission.getStatus());
            ps.setInt(9, admission.getAdmissionId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update admission status");
            e.printStackTrace();
            return;
        }
    }

    public void deleteAdmission(int admissionId) throws SQLException {
        String sql = "DELETE FROM Admissions WHERE admissionid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, admissionId);
            ps.executeUpdate();
        }
    }

    public Admission getAdmissionByName(String name) throws SQLException {
        String sql = "SELECT * FROM Admissions WHERE applicantname = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAdmission(rs);
            }
        }
        return null;
    }

    public ArrayList<Admission> getAdmissionsByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM Admissions WHERE admissionstatus = ?";
        ArrayList<Admission> admissions = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                admissions.add(mapAdmission(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get admissions by status");
            e.printStackTrace();
            return null;
        }
        return admissions;
    }

    /**
     * Add a student into the EAV schema:
     * - Insert into Users table (with UserType = 'Student')
     * - Insert attributes into UserAttributes / UserValues as JSONB
     */
    public void addStudent(Student student) {
        String userSql = "INSERT INTO users (userid,usertype,username,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, student.getId());
            ps.setString(2, student.getType());
            ps.setString(3, student.getName());
            ps.setString(4, student.getEmail());
            ps.setString(5, hashPassword(student.getPassword()));
            ps.setString(6, student.getPhoneNumber());
            ps.setString(7, student.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add student");
            e.printStackTrace();
            return;
        }

        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
        for (int i = 1; i <= 3; i++) {
            try (Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(attributeSql)) {
                ps.setString(1, student.getId());
                ps.setInt(2, i);
                switch (i) {
                    case 1:
                        ps.setString(3, toJsonValue(student.getGpa()));
                        break;
                    case 2:
                        ps.setString(3, toJsonValue(student.getMajor()));
                        break;
                    case 3:
                        ps.setString(3, toJsonValue(student.getSemester()));
                        break;
                    default:
                        ps.setString(3, toJsonValue((String) null));
                        break;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add student attributes");
                e.printStackTrace();
            }
        }
    }

    public User getUser(String id) {
        String sql = "SELECT * FROM users WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ArrayList<Classroom> getAllClassrooms() throws SQLException {
        String sql = "SELECT hallid, hallcapacity, halltype, hallmaintenance, availability FROM halls;";
        ArrayList<Classroom> classrooms = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classrooms.add(new Classroom(
                        rs.getInt("hallid"),
                        rs.getString("hallcapacity"),
                        rs.getString("halltype"),
                        rs.getBoolean("hallmaintenance"),
                        rs.getBoolean("availability")
                ));
            }
        }
        return classrooms;
    }
    private Map<Integer, String> getTakenCourses(String userId) throws SQLException {
        HashMap<Integer, String> courses = new HashMap<>();
        String sql = "SELECT courseid, grade FROM takencourses WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer courseId = rs.getInt("courseid");
                String grade = rs.getString("grade");
                courses.put(courseId, grade != null ? grade : "");
            }
        }
        return courses;
    }

    // public Student getStudent(String id) {
    //     try (Connection conn = getConnection()) {
    //         Student student = fetchStudentCore(conn, id);
    //         if (student == null) {
    //             return null;
    //         }
    //         student.setCurrentCourses(fetchCurrentCourses(conn, id));
    //         student.setTakenCourses(fetchTakenCourses(conn, id));
    //         hydrateUserAttributeValues(conn, id, student);
    //         return student;
    //     } catch (SQLException e) {
    //         throw new RuntimeException("Failed to load student " + id, e);
    //     }
    // }

    // public ArrayList<Student> getAllStudents() {
    //     ArrayList<Student> students = new ArrayList<>();
    //     try (Connection conn = getConnection()) {
    //         String sql = "SELECT userid FROM users WHERE usertype = 'Student'";
    //         try (PreparedStatement ps = conn.prepareStatement(sql);
    //              ResultSet rs = ps.executeQuery()) {
    //             while (rs.next()) {
    //                 String userId = rs.getString("userid");
    //                 Student student = fetchStudentCore(conn, userId);
    //                 if (student != null) {
    //                     student.setCurrentCourses(fetchCurrentCourses(conn, userId));
    //                     student.setTakenCourses(fetchTakenCourses(conn, userId));
    //                     hydrateUserAttributeValues(conn, userId, student);
    //                     students.add(student);
    //                 }
    //             }
    //         }
    //     } catch (SQLException e) {
    //         throw new RuntimeException("Failed to load students", e);
    //     }
    //     return students;
    // }

public ArrayList<Student> getStudentsByCourse(String courseCode) {
    String sql = "SELECT userid FROM currentcourses WHERE courseid = ?";
    ArrayList<Student> students = new ArrayList<>();
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, courseCode);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student student = getStudent(rs.getString("userid"));
                if (student != null) {
                    students.add(student);
                }
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to load student by course", e);
    }
    return students;
}

    public Student getStudent(String id) throws SQLException {
        String sql = "SELECT u.userid, u.usertype, u.username, u.email, u.userpassword, u.phonenumber, u.dateofbirth, "
                +
                "a.attributeid, a.attributename, v.attributevalue " +
                "FROM users u " +
                "LEFT JOIN uservalues v ON u.userid = v.userid " +
                "LEFT JOIN userattributes a ON v.attributeid = a.attributeid " +
                "WHERE u.userid = ? AND u.usertype = 'Student'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            // Variables to store user data
            String userId = null;
            String name = null;
            String email = null;
            String password = null;
            String phoneNumber = null;
            String dateOfBirth = null;

            // Variables to store attribute values
            String gpa = null;
            String major = null;
            String semester = null;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Get user data (same for all rows)
                if (userId == null) {
                    userId = rs.getString("userid");
                    name = rs.getString("username");
                    email = rs.getString("email");
                    password = rs.getString("userpassword");
                    phoneNumber = rs.getString("phonenumber");
                    dateOfBirth = rs.getString("dateofbirth");
                }

                // Get attribute data
                Integer attributeId = rs.getObject("attributeid") != null ? rs.getInt("attributeid") : null;
                String attributeValueJson = rs.getString("attributevalue");

                if (attributeId != null && attributeValueJson != null) {
                    // Parse JSONB value: {"value":"..."} or {"value":true} or {"value":null}
                    String parsedValue = parseJsonValue(attributeValueJson);

                    // Map attributeid to field (1=gpa, 2=major, 3=semester based on addStudent)
                    switch (attributeId) {
                        case 1:
                            gpa = parsedValue;
                            break;
                        case 2:
                            major = parsedValue;
                            break;
                        case 3:
                            semester = parsedValue;
                            break;
                    }
                }
            }

            if (hasData && userId != null) {
                // Use constructor with major (7 params), defaulting to empty string if null
                Student student = new Student(userId, phoneNumber, email, password, name, dateOfBirth,
                        major != null ? major : "");
                if (gpa != null)
                    student.setGpa(gpa);
                if (semester != null)
                    student.setSemester(semester);
                
                // Fetch current courses
                ArrayList<String> currentCourses = getCurrentCourses(userId);
                student.setCurrentCourses(currentCourses);
                
                // Fetch taken courses
                Map<Integer, String> takenCourses = getTakenCourses(userId);
                student.setTakenCourses(takenCourses);
                
                return student;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get student");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Student> getAllStudents() throws SQLException {
        ArrayList<Student> students = new ArrayList<>();
        String sql = "SELECT userid FROM users WHERE usertype = 'Student'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("userid");
                Student student = getStudent(id);
                if (student != null) {
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all students");
            e.printStackTrace();
            throw e;
        }
        return students;
    }
    
    /**
     * Get current courses for a student
     */
    private ArrayList<String> getCurrentCourses(String userId) throws SQLException {
        ArrayList<String> courses = new ArrayList<>();
        String sql = "SELECT courseid FROM currentcourses WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Convert CourseID (INT) to String
                courses.add(String.valueOf(rs.getInt("courseid")));
            }
        }
        return courses;
    }
    
    /**
     * Get taken courses with grades for a student
     */
    public Map<Course, String> getTakenCoursesForTranscript(String userId) throws SQLException {
        Map<Course, String> courses = new HashMap<>();
        String sql = "SELECT c.courseid, c.coursename, c.coursedescription, c.courseyear, tc.grade " +
                "FROM takencourses tc " +
                "JOIN courses c ON tc.courseid = c.courseid " +
                "WHERE tc.userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int courseId = rs.getInt("courseid");
                String courseName = rs.getString("coursename");
                String courseDescription = rs.getString("coursedescription");
                String courseYear = rs.getString("courseyear");
                String grade = rs.getString("grade");

                Course course = new Course(
                        courseId,
                        courseName,
                        courseDescription,
                        courseYear
                );
                courses.put(course, grade != null ? grade : "");
            }
        }
        return courses;
    }

    public void addInstructor(Instructor instructor) {
        String userSql = "INSERT INTO users (userid,usertype,username,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, instructor.getId());
            ps.setString(2, instructor.getType());
            ps.setString(3, instructor.getName());
            ps.setString(4, instructor.getEmail());
            ps.setString(5, hashPassword(instructor.getPassword()));
            ps.setString(6, instructor.getPhoneNumber());
            ps.setString(7, instructor.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add Instructor");
            e.printStackTrace();
            return;
        }
        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
        for (int i = 4; i <= 7; i++) {
            try (Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(attributeSql)) {
                ps.setString(1, instructor.getId());
                ps.setInt(2, i);
                switch (i) {
                    case 4:
                        ps.setString(3, toJsonValue(instructor.getDepartmentName()));
                        break;
                    case 5:
                        ps.setString(3, toJsonValue(instructor.getSalary()));
                        break;
                    case 6:
                        ps.setString(3, toJsonValue(instructor.getRole()));
                        break;
                    case 7:
                        ps.setString(3, toJsonValue(instructor.isDepartmentHead()));
                        break;
                    default:
                        ps.setString(3, toJsonValue((String) null));
                        break;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add instructor attributes");
                e.printStackTrace();
            }
            // String coursesSql = "INSERT INTO currentcourses (instructorid, courseid)
            // VALUES (?, ?)";
            // for (String courseId : instructor.getCourses()) {
            // try (Connection conn = getConnection(); PreparedStatement ps =
            // conn.prepareStatement(coursesSql)) {
            // ps.setString(1, instructor.getId());
            // ps.setString(2, courseId);
            // ps.executeUpdate();
            // }
            // catch (SQLException e) {
            // System.out.println("Failed to add instructor's courses");
            // e.printStackTrace();
            // }
            // }

        }
    }

    public Instructor getInstructor(String id) throws SQLException {
        String sql = "SELECT u.userid, u.usertype, u.username, u.email, u.userpassword, u.phonenumber, u.dateofbirth, "
                +
                "a.attributeid, a.attributename, v.attributevalue " +
                "FROM users u " +
                "LEFT JOIN uservalues v ON u.userid = v.userid " +
                "LEFT JOIN userattributes a ON v.attributeid = a.attributeid " +
                "WHERE u.userid = ? AND u.usertype = 'Instructor'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            // Variables to store user data
            String userId = null;
            String name = null;
            String email = null;
            String password = null;
            String phoneNumber = null;
            String dateOfBirth = null;

            // Variables to store attribute values
            String departmentName = null;
            String salary = null;
            String role = null;
            Boolean isDepartmentHead = null;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Get user data (same for all rows)
                if (userId == null) {
                    userId = rs.getString("userid");
                    name = rs.getString("username");
                    email = rs.getString("email");
                    password = rs.getString("userpassword");
                    phoneNumber = rs.getString("phonenumber");
                    dateOfBirth = rs.getString("dateofbirth");
                }

                // Get attribute data
                Integer attributeId = rs.getObject("attributeid") != null ? rs.getInt("attributeid") : null;
                String attributeValueJson = rs.getString("attributevalue");

                if (attributeId != null && attributeValueJson != null) {
                    // Parse JSONB value: {"value":"..."} or {"value":true} or {"value":null}
                    String parsedValue = parseJsonValue(attributeValueJson);

                    // Map attributeid to field (4=departmentName, 5=salary, 6=role, 7=isDepartmentHead based on addInstructor)
                    switch (attributeId) {
                        case 4:
                            departmentName = parsedValue;
                            break;
                        case 5:
                            salary = parsedValue;
                            break;
                        case 6:
                            role = parsedValue;
                            break;
                        case 7:
                            isDepartmentHead = parsedValue != null ? Boolean.parseBoolean(parsedValue) : null;
                            break;
                    }
                }
            }

            if (hasData && userId != null) {
                // Use constructor with major (7 params), defaulting to empty string if null
                Instructor instructor = new Instructor(userId, phoneNumber, email, password, dateOfBirth,name,
                        departmentName != null ? departmentName : "", isDepartmentHead != null ? isDepartmentHead : false, role != null ? role : "");
                if (salary != null)
                    instructor.setSalary(salary);
                ArrayList<String> courses = getCurrentCourses(userId);
                instructor.setCourses(courses);
                ArrayList<String> responsibilities = getResponsibilities(userId);
                instructor.setResponsibilities(responsibilities);
                ArrayList<String> officeHours = getOfficeHours(userId);
                instructor.setOfficeHours(officeHours);
                ArrayList<String> benefits = getBenefits(userId);
                instructor.setBenefits(benefits);
                return instructor;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get instructor");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Instructor> getAllInstructors() throws SQLException {
        ArrayList<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT userid FROM users WHERE usertype = 'Instructor'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("userid");
                Instructor instructor = getInstructor(id);
                if (instructor != null) {
                    instructors.add(instructor);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all students");
            e.printStackTrace();
            throw e;
        }
        return instructors;
    }

    public ArrayList<String> getResponsibilities(String userId) throws SQLException {
        ArrayList<String> responsibilities = new ArrayList<>();
        String sql = "SELECT responsibility FROM responsibilities WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                responsibilities.add(rs.getString("responsibility"));
            }
        }
        return responsibilities;
    }

    public ArrayList<String> getOfficeHours(String userId) throws SQLException {
        ArrayList<String> officeHours = new ArrayList<>();
        String sql = "SELECT officehour, officehourday FROM officehours WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                officeHours.add(rs.getString("officehour") + " , " + rs.getString("officehourday"));
            }
        }
        return officeHours;
    }

    public ArrayList<String> getBenefits(String userId) throws SQLException {
        ArrayList<String> benefits = new ArrayList<>();
        String sql = "SELECT benefit FROM benefits WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                benefits.add(rs.getString("benefit"));
            }
        }
        return benefits;
    }

    public void addHR(HR hr) {
        String userSql = "INSERT INTO users (userid,usertype,username,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, hr.getId());
            ps.setString(2, hr.getType());
            ps.setString(3, hr.getName());
            ps.setString(4, hr.getEmail());
            ps.setString(5, hashPassword(hr.getPassword()));
            ps.setString(6, hr.getPhoneNumber());
            ps.setString(7, hr.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add HR");
            e.printStackTrace();
            return;
        }
        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
        for (int i = 4; i <= 4; i++) {
            try (Connection conn = getConnection();
                    PreparedStatement ps = conn.prepareStatement(attributeSql)) {
                ps.setString(1, hr.getId());
                ps.setInt(2, i);
                switch (i) {
                    case 4:
                        ps.setString(3, toJsonValue(hr.getDepartmentName()));
                        break;
                    case 5:
                        ps.setString(3, toJsonValue(hr.getSalary()));
                        break;
                    default:
                        ps.setString(3, toJsonValue((String) null));
                        break;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add HR attributes");
                e.printStackTrace();
            }
        }
    }

    public HR getHR(String id) throws SQLException {
        String sql = "SELECT u.userid, u.usertype, u.username, u.email, u.userpassword, u.phonenumber, u.dateofbirth, "
                +
                "a.attributeid, a.attributename, v.attributevalue " +
                "FROM users u " +
                "LEFT JOIN uservalues v ON u.userid = v.userid " +
                "LEFT JOIN userattributes a ON v.attributeid = a.attributeid " +
                "WHERE u.userid = ? AND u.usertype = 'HR'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            // Variables to store user data
            String userId = null;
            String name = null;
            String email = null;
            String password = null;
            String phoneNumber = null;
            String dateOfBirth = null;

            // Variables to store attribute values
            String departmentName = null;
            String salary = null;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Get user data (same for all rows)
                if (userId == null) {
                    userId = rs.getString("userid");
                    name = rs.getString("username");
                    email = rs.getString("email");
                    password = rs.getString("userpassword");
                    phoneNumber = rs.getString("phonenumber");
                    dateOfBirth = rs.getString("dateofbirth");
                }

                // Get attribute data
                Integer attributeId = rs.getObject("attributeid") != null ? rs.getInt("attributeid") : null;
                String attributeValueJson = rs.getString("attributevalue");

                if (attributeId != null && attributeValueJson != null) {
                    // Parse JSONB value: {"value":"..."} or {"value":true} or {"value":null}
                    String parsedValue = parseJsonValue(attributeValueJson);

                    // Map attributeid to field (4=departmentName, 5=salary, 6=role, 7=isDepartmentHead based on addInstructor)
                    switch (attributeId) {
                        case 4:
                            departmentName = parsedValue;
                            break;
                        case 5:
                            salary = parsedValue;
                            break;
                    }
                }
            }

            if (hasData && userId != null) {
                HR hr = new HR(userId, phoneNumber, email, password, dateOfBirth, name,
                        salary != null ? salary : "", departmentName != null ? departmentName : "");
                return hr;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get HR");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<HR> getAllHR() throws SQLException {
        ArrayList<HR> hrs = new ArrayList<>();
        String sql = "SELECT userid FROM users WHERE usertype = 'HR'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("userid");
                HR hr = getHR(id);
                if (hr != null) {
                    hrs.add(hr);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all HRs");
            e.printStackTrace();
            throw e;
        }
        return hrs;
    }

    public void addAdmin(Admin admin) {
        String userSql = "INSERT INTO users (userid,usertype,username,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, admin.getId());
            ps.setString(2, admin.getType());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getEmail());
            ps.setString(5, hashPassword(admin.getPassword()));
            ps.setString(6, admin.getPhoneNumber());
            ps.setString(7, admin.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add admin");
            e.printStackTrace();
            return;
        }
        // String attributeSql = "INSERT INTO uservalues (userid, attributeid,
        // attributeValue) VALUES (?, ?, ?::jsonb)";
        // try (Connection conn = getConnection(); PreparedStatement ps =
        // conn.prepareStatement(attributeSql)) {
        // ps.setString(1, admin.getId());
        // ps.setInt(2, 6);
        // ps.setString(3, toJsonValue(admin.getSalary()));
        // ps.executeUpdate();
        // } catch (SQLException e) {
        // System.out.println("Failed to add admin attributes");
        // e.printStackTrace();
        // }
    }
    
    public Admin getAdmin(String id) throws SQLException {
        String sql = "SELECT u.userid, u.usertype, u.username, u.email, u.userpassword, u.phonenumber, u.dateofbirth, "
                +
                "a.attributeid, a.attributename, v.attributevalue " +
                "FROM users u " +
                "LEFT JOIN uservalues v ON u.userid = v.userid " +
                "LEFT JOIN userattributes a ON v.attributeid = a.attributeid " +
                "WHERE u.userid = ? AND u.usertype = 'Admin'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            // Variables to store user data
            String userId = null;
            String name = null;
            String email = null;
            String password = null;
            String phoneNumber = null;
            String dateOfBirth = null;

            // Variables to store attribute values
            String salary = null;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Get user data (same for all rows)
                if (userId == null) {
                    userId = rs.getString("userid");
                    name = rs.getString("username");
                    email = rs.getString("email");
                    password = rs.getString("userpassword");
                    phoneNumber = rs.getString("phonenumber");
                    dateOfBirth = rs.getString("dateofbirth");
                }

                // Get attribute data
                Integer attributeId = rs.getObject("attributeid") != null ? rs.getInt("attributeid") : null;
                String attributeValueJson = rs.getString("attributevalue");

                if (attributeId != null && attributeValueJson != null) {
                    // Parse JSONB value: {"value":"..."} or {"value":true} or {"value":null}
                    String parsedValue = parseJsonValue(attributeValueJson);

                    salary = parsedValue;
                }
            }

            if (hasData && userId != null) {
                Admin admin = new Admin(userId, phoneNumber, email, password, dateOfBirth, name,
                        salary != null ? salary : "");
                return admin;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get Admin");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Admin> getAllAdmins() throws SQLException {
        ArrayList<Admin> admins = new ArrayList<>();
        String sql = "SELECT userid FROM users WHERE usertype = 'Admin'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("userid");
                Admin admin = getAdmin(id);
                if (admin != null) {
                    admins.add(admin);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all admins");
            e.printStackTrace();
            throw e;
        }
        return admins;
    }

    public void addParent(Parent parent) {
        String userSql = "INSERT INTO users (userid,usertype,username,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, parent.getId());
            ps.setString(2, parent.getType());
            ps.setString(3, parent.getName());
            ps.setString(4, parent.getEmail());
            ps.setString(5, parent.getPassword());
            ps.setString(6, parent.getPhoneNumber());
            ps.setString(7, parent.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add Parent");
            e.printStackTrace();
            return;
        }

        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(attributeSql)) {
            ps.setString(1, parent.getId());
            ps.setInt(2, 6);
            ps.setString(3, toJsonValue(parent.getRelation()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add parent attributes");
            e.printStackTrace();
        }

        String childrenSql = "INSERT INTO children (parentid, childid) VALUES (?, ?)";
        for (String childId : parent.getChildren()) {
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(childrenSql)) {
                ps.setString(1, parent.getId());
                ps.setString(2, childId);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add parent's children");
                e.printStackTrace();
            }
        }
    }

    public Parent getParent(String id) throws SQLException {
        String sql = "SELECT u.userid, u.usertype, u.username, u.email, u.userpassword, u.phonenumber, u.dateofbirth, "
                +
                "a.attributeid, a.attributename, v.attributevalue " +
                "FROM users u " +
                "LEFT JOIN uservalues v ON u.userid = v.userid " +
                "LEFT JOIN userattributes a ON v.attributeid = a.attributeid " +
                "WHERE u.userid = ? AND u.usertype = 'Parent'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            // Variables to store user data
            String userId = null;
            String name = null;
            String email = null;
            String password = null;
            String phoneNumber = null;
            String dateOfBirth = null;

            // Variables to store attribute values
            String relation = null;

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Get user data (same for all rows)
                if (userId == null) {
                    userId = rs.getString("userid");
                    name = rs.getString("username");
                    email = rs.getString("email");
                    password = rs.getString("userpassword");
                    phoneNumber = rs.getString("phonenumber");
                    dateOfBirth = rs.getString("dateofbirth");
                }

                // Get attribute data
                Integer attributeId = rs.getObject("attributeid") != null ? rs.getInt("attributeid") : null;
                String attributeValueJson = rs.getString("attributevalue");

                if (attributeId != null && attributeValueJson != null) {
                    // Parse JSONB value: {"value":"..."} or {"value":true} or {"value":null}
                    String parsedValue = parseJsonValue(attributeValueJson);

                    relation = parsedValue;
                }
            }

            if (hasData && userId != null) {
                ArrayList<String> children = getChildren(userId);
                Parent parent = new Parent(userId, phoneNumber, email, password, name, dateOfBirth,relation, children);
                return parent;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get Parent");
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Parent> getAllParents() throws SQLException {
        ArrayList<Parent> parents = new ArrayList<>();
        String sql = "SELECT userid FROM users WHERE usertype = 'Parent'";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("userid");
                Parent parent = getParent(id);
                if (parent != null) {
                    parents.add(parent);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all parents");
            e.printStackTrace();
            throw e;
        }
        return parents;
    }

    private ArrayList<String> getChildren(String userId) throws SQLException {
        ArrayList<String> children = new ArrayList<>();
        String sql = "SELECT childid FROM children WHERE parentid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                children.add(rs.getString("childid"));
            }
        }
        return children;
    }

    public void addCourse(Course course) {
        String courseSql = "INSERT INTO courses (courseid, coursename, coursedescription, courseyear) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(courseSql)) {
            ps.setInt(1, course.getCourseId());
            ps.setString(2, course.getCourseName());
            ps.setString(3, course.getCourseDescription());
            ps.setString(4, course.getYear());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add course");
            e.printStackTrace();
            return;
        }
    }

    // public Course getCourse(String id) throws SQLException {
    //     String sql = "SELECT * FROM courses WHERE courseid = ?";
    //     try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setString(1, id);
    //         ResultSet rs = ps.executeQuery();
    //         if (!rs.next()) {
    //             return null;
    //         }
    //         String courseName = rs.getString("coursename");
    //         String courseDescription = rs.getString("coursedescription");
    //         String year = rs.getString("courseyear");
    //         return new Course(id, courseName, courseDescription, year);
    //     }
    // }

    // public ArrayList<String> getMaterial(String id) throws SQLException {
    //     String sql = "SELECT materialname FROM material WHERE courseid = ?";
    //     ArrayList<String> material = new ArrayList<>();
    //     try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setString(1, id);
    //         ResultSet rs = ps.executeQuery();
    //         while (rs.next()) {
    //             material.add(rs.getString("materialname"));
    //         }
    //     } catch (SQLException e) {
    //         System.out.println("Failed to get material");
    //         e.printStackTrace();
    //         return null;
    //     }
    //     return material;
    // }

    // public ArrayList<String> getAssignments(String id) throws SQLException {
    //     String sql = "SELECT assignmen FROM assignments WHERE courseid = ?";
    //     ArrayList<String> assignments = new ArrayList<>();
    //     try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setString(1, id);
    //         ResultSet rs = ps.executeQuery();
    //         while (rs.next()) {
    //             assignments.add(rs.getString("assignmentname"));
    //         }
    //     } catch (SQLException e) {
    //         System.out.println("Failed to get assignments");
    //         e.printStackTrace();
    //         return null;
    //     }
    //     return assignments;
    // }

    public int getHighestIdNumber(String usertype) {
        int highest = 0;

        try {
            // Get the maximum userid (lexicographic comparison works because numbers are zero-padded)
            // Format: YYLetterNNN (e.g., "25I005")
            String sql = "SELECT MAX(userid) FROM users WHERE usertype = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usertype);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String maxId = rs.getString(1);
                    // Extract the numeric part (last 3 digits) from the max ID
                    if (maxId != null && maxId.length() >= 6) {
                        String numberPart = maxId.substring(3); // Get last 3 digits
                        highest = Integer.parseInt(numberPart);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return highest;
    }

    private static String toJsonValue(String value) {
        if (value == null) {
            return "{\"value\":null}";
        }
        return "{\"value\":\"" + escapeJson(value) + "\"}";
    }

    private static String toJsonValue(Boolean value) {
        if (value == null) {
            return "{\"value\":null}";
        }
        return "{\"value\":" + value + "}";
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Parse JSONB value from format {"value":"..."} or {"value":true} or
     * {"value":null}
     * Returns the actual value as a String, or null if the value is null
     */
    private String parseJsonValue(String jsonValue) {
        if (jsonValue == null || jsonValue.trim().isEmpty()) {
            return null;
        }

        // Remove whitespace
        jsonValue = jsonValue.trim();

        // Check for null value
        if (jsonValue.equals("{\"value\":null}")) {
            return null;
        }

        // Extract value from {"value":"..."} format
        if (jsonValue.startsWith("{\"value\":\"")) {
            // String value
            int start = 11; // Length of {"value":"
            int end = jsonValue.length() - 2; // Remove "}
            if (end > start) {
                String value = jsonValue.substring(start, end);
                // Unescape JSON string
                return unescapeJson(value);
            }
        } else if (jsonValue.startsWith("{\"value\":")) {
            // Boolean or number value
            int start = 9; // Length of {"value":
            int end = jsonValue.length() - 1; // Remove }
            if (end > start) {
                return jsonValue.substring(start, end);
            }
        }

        return null;
    }

    /**
     * Unescape JSON string (reverse of escapeJson)
     */
    private String unescapeJson(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("\\t", "\t")
                .replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private Admission mapAdmission(ResultSet rs) throws SQLException {
        Admission admission = new Admission(
                rs.getInt("admissionid"),
                rs.getString("applicantname"),
                rs.getString("phonenumber"),
                rs.getString("email"),
                rs.getString("dateofbirth"),
                rs.getString("major"),
                rs.getString("highschoolgpa"));
        admission.setStatus(rs.getString("admissionstatus"));
        admission.setYearOfAdmission(rs.getString("yearofadmission"));
        return admission;
    }

    
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("userid"));
        user.setType(rs.getString("usertype"));
        user.setName(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("userpassword"));
        user.setPhoneNumber(rs.getString("phonenumber"));
        user.setdateOfBirth(rs.getString("dateofbirth"));
        return user;
    }

    private Student fetchStudentCore(Connection conn, String id) throws SQLException {
        String sql = "SELECT userid, usertype, username, email, userpassword, phonenumber, dateofbirth " +
                "FROM users WHERE userid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            Student student = new Student();
            student.setId(rs.getString("userid"));
            student.setType(rs.getString("usertype"));
            student.setName(rs.getString("username"));
            student.setEmail(rs.getString("email"));
            student.setPassword(rs.getString("userpassword"));
            student.setPhoneNumber(rs.getString("phonenumber"));
            student.setdateOfBirth(rs.getString("dateofbirth"));
            return student;
        }
    }

    // private ArrayList<String> fetchCurrentCourses(Connection conn, String userId) throws SQLException {
    //     ArrayList<String> courses = new ArrayList<>();
    //     String sql = "SELECT coursecode FROM currentcourses WHERE userid = ?";
    //     try (PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setString(1, userId);
    //         ResultSet rs = ps.executeQuery();
    //         while (rs.next()) {
    //             courses.add(rs.getString("coursecode"));
    //         }
    //     }
    //     return courses;
    // }

    private Map<String, String> fetchTakenCourses(Connection conn, String userId) throws SQLException {
        Map<String, String> taken = new HashMap<>();
        String sql = "SELECT coursecode, grade FROM takencourses WHERE userid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                taken.put(rs.getString("coursecode"), rs.getString("grade"));
            }
        }
        return taken;
    }

    private void hydrateUserAttributeValues(Connection conn, String userId, Student student) throws SQLException {
        String sql = "SELECT ua.attributename, uv.attributevalue " +
                "FROM uservalues uv " +
                "JOIN userattributes ua ON uv.attributeid = ua.attributeid " +
                "WHERE uv.userid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("attributename");
                String raw = rs.getString("attributevalue");
                String value = extractScalarValue(raw);
                if (name == null) {
                    continue;
                }
                switch (name) {
                    case "gpa":
                        student.setGpa(value);
                        break;
                    case "major":
                        student.setMajor(value);
                        break;
                    case "semester":
                        student.setSemester(value);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private String extractScalarValue(String json) {
        if (json == null) {
            return null;
        }
        String trimmed = json.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("null")) {
            return null;
        }
        if (trimmed.startsWith("{") && trimmed.contains("\"value\"")) {
            if (trimmed.contains("\"value\":null")) {
                return null;
            }
            int start = trimmed.indexOf("\"value\"");
            start = trimmed.indexOf(':', start);
            if (start == -1) {
                return trimmed;
            }
            start++;
            while (start < trimmed.length() && Character.isWhitespace(trimmed.charAt(start))) {
                start++;
            }
            if (start < trimmed.length() && trimmed.charAt(start) == '\"') {
                start++;
                int end = trimmed.indexOf("\"", start);
                if (end > start) {
                    return trimmed.substring(start, end)
                            .replace("\\\"", "\"")
                            .replace("\\\\", "\\");
                }
            }
        }
        return trimmed;
    }


    // public static void addStudent(Student student) {

    // String userType = "Student";
    // try (Connection conn = getConnection()) {

    // conn.setAutoCommit(false);
    // // 1) Insert into Users and get generated UserID
    // long userId;
    // String insertUserSql = "INSERT INTO Users (UserType) VALUES (?) RETURNING
    // UserID";
    // try (PreparedStatement ps = conn.prepareStatement(insertUserSql)) {
    // ps.setString(1, userType);
    // try (ResultSet rs = ps.executeQuery()) {
    // if (rs.next()) {
    // userId = rs.getLong("UserID");
    // } else {
    // throw new SQLException("Failed to retrieve generated UserID");
    // }
    // }
    // }

    // // 2) Insert main attributes as EAV JSONB values
    // insertUserAttributeValue(conn, userId, "id", "string", student.getId());
    // insertUserAttributeValue(conn, userId, "name", "string", student.getName());
    // insertUserAttributeValue(conn, userId, "email", "string",
    // student.getEmail());
    // insertUserAttributeValue(conn, userId, "phoneNumber", "string",
    // student.getPhoneNumber());
    // insertUserAttributeValue(conn, userId, "dateOfBirth", "string",
    // student.getdateOfBirth());
    // insertUserAttributeValue(conn, userId, "major", "string",
    // student.getMajor());
    // insertUserAttributeValue(conn, userId, "semester", "string",
    // student.getSemester());
    // insertUserAttributeValue(conn, userId, "gpa", "float", student.getGpa());

    // // Optional: currentCourses as JSON array
    // if (student.getCurrentCourses() != null) {
    // String coursesJson = listToJsonArray(student.getCurrentCourses());
    // insertUserAttributeValue(conn, userId, "currentCourses", "array",
    // coursesJson, true);
    // }

    // // Optional: takenCourses as JSON object (course -> grade)
    // if (student.getTakenCourses() != null) {
    // String takenJson = mapToJsonObject(student.getTakenCourses());
    // insertUserAttributeValue(conn, userId, "takenCourses", "object", takenJson,
    // true);
    // }

    // conn.commit();
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to save student in EAV schema", e);
    // }
    // }

    // /**
    // * Insert or reuse an attribute definition, then save a value for that user.
    // */
    // private static void insertUserAttributeValue(Connection conn,
    // long userId,
    // String attributeName,
    // String valueType,
    // String value) throws SQLException {
    // insertUserAttributeValue(conn, userId, attributeName, valueType, value,
    // false);
    // }

    // private static void insertUserAttributeValue(Connection conn,
    // long userId,
    // String attributeName,
    // String valueType,
    // String value,
    // boolean valueIsRawJson) throws SQLException {
    // int attributeId = getOrCreateAttributeId(conn, attributeName, valueType);

    // String jsonValue = valueIsRawJson ? value : "{\"value\":\"" +
    // escapeJson(value) + "\"}";

    // String sql = "INSERT INTO UserValues (UserID, AttributeID, AttributeValue) "
    // +
    // "VALUES (?, ?, cast(? as jsonb))";
    // try (PreparedStatement ps = conn.prepareStatement(sql)) {
    // ps.setLong(1, userId);
    // ps.setInt(2, attributeId);
    // ps.setString(3, jsonValue);
    // ps.executeUpdate();
    // }
    // }

    // /**
    // * Get existing AttributeID or create a new one if it doesn't exist.
    // */
    // private static int getOrCreateAttributeId(Connection conn,
    // String attributeName,
    // String valueType) throws SQLException {
    // // 1) Try to find existing
    // String selectSql = "SELECT AttributeID FROM UserAttributes WHERE
    // AttributeName = ? AND ValueType = ? LIMIT 1";
    // try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
    // ps.setString(1, attributeName);
    // ps.setString(2, valueType);
    // try (ResultSet rs = ps.executeQuery()) {
    // if (rs.next()) {
    // return rs.getInt("AttributeID");
    // }
    // }
    // }

    // // 2) Create new attribute
    // String insertSql = "INSERT INTO UserAttributes (AttributeName, ValueType) " +
    // "VALUES (?, ?) RETURNING AttributeID";
    // try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
    // ps.setString(1, attributeName);
    // ps.setString(2, valueType);
    // try (ResultSet rs = ps.executeQuery()) {
    // if (rs.next()) {
    // return rs.getInt("AttributeID");
    // } else {
    // throw new SQLException("Failed to create UserAttribute for " +
    // attributeName);
    // }
    // }
    // }
    // }

    // private static String listToJsonArray(Iterable<String> values) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("[");
    // boolean first = true;
    // for (String v : values) {
    // if (!first) {
    // sb.append(",");
    // }
    // sb.append("\"").append(escapeJson(v)).append("\"");
    // first = false;
    // }
    // sb.append("]");
    // return sb.toString();
    // }

    // private static String mapToJsonObject(Map<String, String> map) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("{");
    // boolean first = true;
    // for (Map.Entry<String, String> entry : map.entrySet()) {
    // if (!first) {
    // sb.append(",");
    // }
    // sb.append("\"")
    // .append(escapeJson(entry.getKey()))
    // .append("\":\"")
    // .append(escapeJson(entry.getValue()))
    // .append("\"");
    // first = false;
    // }
    // sb.append("}");
    // return sb.toString();
    // }

    // private static String escapeJson(String value) {
    // if (value == null) {
    // return "";
    // }
    // return value
    // .replace("\\", "\\\\")
    // .replace("\"", "\\\"")
    // .replace("\n", "\\n")
    // .replace("\r", "\\r")
    // .replace("\t", "\\t");
    // }

    /**
     * Update a student in the database
     */
    public void updateStudent(Student student) throws SQLException {
        // Update users table
        String userSql = "UPDATE users SET username = ?, email = ?, userpassword = ?, phonenumber = ?, dateofbirth = ? WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPassword());
            ps.setString(4, student.getPhoneNumber());
            ps.setString(5, student.getdateOfBirth());
            ps.setString(6, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update student in users table");
            e.printStackTrace();
            throw e;
        }

        // Update or insert uservalues for attributes
        // Try UPDATE first, then INSERT if no rows were affected
        for (int i = 1; i <= 3; i++) {
            String jsonValue = null;
            switch (i) {
                case 1:
                    jsonValue = toJsonValue(student.getGpa());
                    break;
                case 2:
                    jsonValue = toJsonValue(student.getMajor());
                    break;
                case 3:
                    jsonValue = toJsonValue(student.getSemester());
                    break;
            }

            try (Connection conn = getConnection()) {
                // Try UPDATE first
                String updateSql = "UPDATE uservalues SET attributeValue = ?::jsonb WHERE userid = ? AND attributeid = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, jsonValue);
                ps.setString(2, student.getId());
                ps.setInt(3, i);
                int rowsAffected = ps.executeUpdate();
                ps.close();

                // If no rows were updated, insert new record
                if (rowsAffected == 0) {
                    String insertSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
                    PreparedStatement insertPs = conn.prepareStatement(insertSql);
                    insertPs.setString(1, student.getId());
                    insertPs.setInt(2, i);
                    insertPs.setString(3, jsonValue);
                    insertPs.executeUpdate();
                    insertPs.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to update student attributes for attributeid " + i);
                e.printStackTrace();
            }
        }

        // Update current courses - delete existing and insert new ones
        String deleteCurrentCoursesSql = "DELETE FROM currentcourses WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteCurrentCoursesSql)) {
            ps.setString(1, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete current courses");
            e.printStackTrace();
        }

        if (student.getCurrentCourses() != null && !student.getCurrentCourses().isEmpty()) {
            String insertCurrentCoursesSql = "INSERT INTO currentcourses (userid, courseid) VALUES (?, ?)";
            for (String courseId : student.getCurrentCourses()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertCurrentCoursesSql)) {
                    ps.setString(1, student.getId());
                    ps.setInt(2, Integer.parseInt(courseId));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert current course: " + courseId);
                    e.printStackTrace();
                }
            }
        }

        // Update taken courses - delete existing and insert new ones
        String deleteTakenCoursesSql = "DELETE FROM takencourses WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteTakenCoursesSql)) {
            ps.setString(1, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete taken courses");
            e.printStackTrace();
        }

        if (student.getTakenCourses() != null && !student.getTakenCourses().isEmpty()) {
            String insertTakenCoursesSql = "INSERT INTO takencourses (userid, courseid, grade) VALUES (?, ?, ?)";
            for (Map.Entry<Integer, String> entry : student.getTakenCourses().entrySet()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertTakenCoursesSql)) {
                    ps.setString(1, student.getId());
                    ps.setInt(2, entry.getKey());
                    ps.setString(3, entry.getValue() != null ? entry.getValue() : "");
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert taken course: " + entry.getKey());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update an instructor in the database
     */
    public void updateInstructor(Instructor instructor) throws SQLException {
        // Update users table
        String userSql = "UPDATE users SET username = ?, email = ?, userpassword = ?, phonenumber = ?, dateofbirth = ? WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, instructor.getName());
            ps.setString(2, instructor.getEmail());
            ps.setString(3, instructor.getPassword());
            ps.setString(4, instructor.getPhoneNumber());
            ps.setString(5, instructor.getdateOfBirth());
            ps.setString(6, instructor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update instructor in users table");
            e.printStackTrace();
            throw e;
        }

        // Update or insert uservalues for attributes
        // Try UPDATE first, then INSERT if no rows were affected
        for (int i = 4; i <= 7; i++) {
            String jsonValue = null;
            switch (i) {
                case 4:
                    jsonValue = toJsonValue(instructor.getDepartmentName());
                    break;
                case 5:
                    jsonValue = toJsonValue(instructor.getSalary());
                    break;
                case 6:
                    jsonValue = toJsonValue(instructor.getRole());
                    break;
                case 7:
                    jsonValue = toJsonValue(instructor.isDepartmentHead());
                    break;
            }

            try (Connection conn = getConnection()) {
                // Try UPDATE first
                String updateSql = "UPDATE uservalues SET attributeValue = ?::jsonb WHERE userid = ? AND attributeid = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, jsonValue);
                ps.setString(2, instructor.getId());
                ps.setInt(3, i);
                int rowsAffected = ps.executeUpdate();
                ps.close();

                // If no rows were updated, insert new record
                if (rowsAffected == 0) {
                    String insertSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
                    PreparedStatement insertPs = conn.prepareStatement(insertSql);
                    insertPs.setString(1, instructor.getId());
                    insertPs.setInt(2, i);
                    insertPs.setString(3, jsonValue);
                    insertPs.executeUpdate();
                    insertPs.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to update instructor attributes for attributeid " + i);
                e.printStackTrace();
            }
        }

        // Update courses - delete existing and insert new ones
        String deleteCoursesSql = "DELETE FROM currentcourses WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteCoursesSql)) {
            ps.setString(1, instructor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete instructor courses");
            e.printStackTrace();
        }

        if (instructor.getCourses() != null && !instructor.getCourses().isEmpty()) {
            String insertCoursesSql = "INSERT INTO currentcourses (userid, courseid) VALUES (?, ?)";
            for (String courseId : instructor.getCourses()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertCoursesSql)) {
                    ps.setString(1, instructor.getId());
                    ps.setInt(2, Integer.parseInt(courseId));
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert instructor course: " + courseId);
                    e.printStackTrace();
                }
            }
        }

        // Update responsibilities - delete existing and insert new ones
        String deleteResponsibilitiesSql = "DELETE FROM responsibilities WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteResponsibilitiesSql)) {
            ps.setString(1, instructor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete responsibilities");
            e.printStackTrace();
        }

        if (instructor.getResponsibilities() != null && !instructor.getResponsibilities().isEmpty()) {
            String insertResponsibilitiesSql = "INSERT INTO responsibilities (userid, responsibility) VALUES (?, ?)";
            for (String responsibility : instructor.getResponsibilities()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertResponsibilitiesSql)) {
                    ps.setString(1, instructor.getId());
                    ps.setString(2, responsibility);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert responsibility: " + responsibility);
                    e.printStackTrace();
                }
            }
        }

        // Update office hours - delete existing and insert new ones
        String deleteOfficeHoursSql = "DELETE FROM officehours WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteOfficeHoursSql)) {
            ps.setString(1, instructor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete office hours");
            e.printStackTrace();
        }

        if (instructor.getOfficeHours() != null && !instructor.getOfficeHours().isEmpty()) {
            String insertOfficeHoursSql = "INSERT INTO officehours (userid, officehour, officehourday) VALUES (?, ?, ?)";
            for (String officeHour : instructor.getOfficeHours()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertOfficeHoursSql)) {
                    // Parse office hour format: "time , day"
                    String[] parts = officeHour.split(" , ");
                    if (parts.length == 2) {
                        ps.setString(1, instructor.getId());
                        ps.setString(2, parts[0].trim());
                        ps.setString(3, parts[1].trim());
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to insert office hour: " + officeHour);
                    e.printStackTrace();
                }
            }
        }

        // Update benefits - delete existing and insert new ones
        String deleteBenefitsSql = "DELETE FROM benefits WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteBenefitsSql)) {
            ps.setString(1, instructor.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete benefits");
            e.printStackTrace();
        }

        if (instructor.getBenefits() != null && !instructor.getBenefits().isEmpty()) {
            String insertBenefitsSql = "INSERT INTO benefits (userid, benefit) VALUES (?, ?)";
            for (String benefit : instructor.getBenefits()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertBenefitsSql)) {
                    ps.setString(1, instructor.getId());
                    ps.setString(2, benefit);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert benefit: " + benefit);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update an HR in the database
     */
    public void updateHR(HR hr) throws SQLException {
        // Update users table
        String userSql = "UPDATE users SET username = ?, email = ?, userpassword = ?, phonenumber = ?, dateofbirth = ? WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, hr.getName());
            ps.setString(2, hr.getEmail());
            ps.setString(3, hr.getPassword());
            ps.setString(4, hr.getPhoneNumber());
            ps.setString(5, hr.getdateOfBirth());
            ps.setString(6, hr.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update HR in users table");
            e.printStackTrace();
            throw e;
        }

        // Update or insert uservalues for attributes
        // Try UPDATE first, then INSERT if no rows were affected
        for (int i = 4; i <= 5; i++) {
            String jsonValue = null;
            switch (i) {
                case 4:
                    jsonValue = toJsonValue(hr.getDepartmentName());
                    break;
                case 5:
                    jsonValue = toJsonValue(hr.getSalary());
                    break;
            }

            try (Connection conn = getConnection()) {
                // Try UPDATE first
                String updateSql = "UPDATE uservalues SET attributeValue = ?::jsonb WHERE userid = ? AND attributeid = ?";
                PreparedStatement ps = conn.prepareStatement(updateSql);
                ps.setString(1, jsonValue);
                ps.setString(2, hr.getId());
                ps.setInt(3, i);
                int rowsAffected = ps.executeUpdate();
                ps.close();

                // If no rows were updated, insert new record
                if (rowsAffected == 0) {
                    String insertSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
                    PreparedStatement insertPs = conn.prepareStatement(insertSql);
                    insertPs.setString(1, hr.getId());
                    insertPs.setInt(2, i);
                    insertPs.setString(3, jsonValue);
                    insertPs.executeUpdate();
                    insertPs.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to update HR attributes for attributeid " + i);
                e.printStackTrace();
            }
        }
    }

    /**
     * Update an Admin in the database
     */
    public void updateAdmin(Admin admin) throws SQLException {
        // Update users table
        String userSql = "UPDATE users SET username = ?, email = ?, userpassword = ?, phonenumber = ?, dateofbirth = ? WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPassword());
            ps.setString(4, admin.getPhoneNumber());
            ps.setString(5, admin.getdateOfBirth());
            ps.setString(6, admin.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update admin in users table");
            e.printStackTrace();
            throw e;
        }

        // Note: Admin doesn't have attributes stored in uservalues currently
        // If salary needs to be stored, it would be added here similar to other user types
    }

    /**
     * Update a Parent in the database
     */
    public void updateParent(Parent parent) throws SQLException {
        // Update users table
        String userSql = "UPDATE users SET username = ?, email = ?, userpassword = ?, phonenumber = ?, dateofbirth = ? WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, parent.getName());
            ps.setString(2, parent.getEmail());
            ps.setString(3, parent.getPassword());
            ps.setString(4, parent.getPhoneNumber());
            ps.setString(5, parent.getdateOfBirth());
            ps.setString(6, parent.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update parent in users table");
            e.printStackTrace();
            throw e;
        }

        // Update or insert uservalues for relation attribute (attributeid = 6)
        // Try UPDATE first, then INSERT if no rows were affected
        String jsonValue = toJsonValue(parent.getRelation());
        try (Connection conn = getConnection()) {
            // Try UPDATE first
            String updateSql = "UPDATE uservalues SET attributeValue = ?::jsonb WHERE userid = ? AND attributeid = ?";
            PreparedStatement ps = conn.prepareStatement(updateSql);
            ps.setString(1, jsonValue);
            ps.setString(2, parent.getId());
            ps.setInt(3, 6);
            int rowsAffected = ps.executeUpdate();
            ps.close();

            // If no rows were updated, insert new record
            if (rowsAffected == 0) {
                String insertSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                insertPs.setString(1, parent.getId());
                insertPs.setInt(2, 6);
                insertPs.setString(3, jsonValue);
                insertPs.executeUpdate();
                insertPs.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to update parent attributes");
            e.printStackTrace();
        }

        // Update children - delete existing and insert new ones
        String deleteChildrenSql = "DELETE FROM children WHERE parentid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(deleteChildrenSql)) {
            ps.setString(1, parent.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete children");
            e.printStackTrace();
        }

        if (parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            String insertChildrenSql = "INSERT INTO children (parentid, childid) VALUES (?, ?)";
            for (String childId : parent.getChildren()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertChildrenSql)) {
                    ps.setString(1, parent.getId());
                    ps.setString(2, childId);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert child: " + childId);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Simple connectivity test (optional).
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Connected!");

            } else {
                System.out.println("Failed to connect to the database");
            }
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }
}