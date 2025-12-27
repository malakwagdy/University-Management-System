package com.example.ums;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.cdimascio.dotenv.Dotenv;


public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String JDBC_URL = dotenv.get("DB_URL");
    
    private static HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(JDBC_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        dataSource = new HikariDataSource(config);
    }
    
    // Cloudinary client initialized from .env values
    private static final Cloudinary CLOUDINARY = new Cloudinary(
            ObjectUtils.asMap(
                    "cloud_name", dotenv.get("cloud_name"),
                    "api_key", dotenv.get("api_key"),
                    "api_secret", dotenv.get("api_secret"),
                    "secure", true
            )
    );

    /**
     * Get a connection from the pool.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    public static Cloudinary getCloudinary() {
        return CLOUDINARY;
    }
    public String upload(String filePath) throws Exception {
        Map<String, Object> uploadResult = CLOUDINARY.uploader()
                .upload(filePath, ObjectUtils.asMap(

                        "resource_type", "image",
                        "format", "pdf",
                        "folder", "UMS",
                        "use_filename", true,
                        "unique_filename", true,
                        "access_mode", "public"
                ));

        Object secureUrl = uploadResult.getOrDefault("secure_url", uploadResult.get("url"));
        if (secureUrl == null) {
            throw new IllegalStateException("Upload succeeded but no URL returned: " + uploadResult);
        }

        return secureUrl.toString();
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

    public static boolean reserveSlot(int hallId, String slotDate, String slotTime) {

        String sql =
        "INSERT INTO reservedslots (hallid, slotdate, slottime)"+
        "VALUES (?, ?, ?)";


        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hallId);
            ps.setString(2, slotDate);
            ps.setString(3, slotTime);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Primary key violation = already booked
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

    
    public ArrayList<User> getAllUsersLite() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT userid, usertype, username, email, userpassword, phonenumber, dateofbirth FROM users";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch users (lite)");
            e.printStackTrace();
        }
        return users;
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
    private Map<Integer, Map<String,String>> getTakenCourses(String userId) throws SQLException {
        HashMap<Integer, Map<String,String>> courses = new HashMap<>();
        String sql = "SELECT courseid, grade, semester FROM takencourses WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer courseId = rs.getInt("courseid");
                String grade = rs.getString("grade");
                String semester = rs.getString("semester");
                
                Map<String, String> courseData = new HashMap<>();
                courseData.put("grade", grade != null ? grade : "");
                courseData.put("semester", semester != null ? semester : "");
                
                courses.put(courseId, courseData);
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

    public ArrayList<Student> getStudentsByMajor(String major) {
        String sql = "SELECT userid FROM users WHERE usertype = 'Student' AND userid IN (SELECT userid FROM uservalues WHERE attributeid = 2 AND attributeValue->>'value' = ?)";
        ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, major);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = getStudent(rs.getString("userid"));
                    if (student != null) {
                        students.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load students by major", e);
        }
        return students;
    }
    
public ArrayList<Student> getStudentsByCourse(int courseCode) {
    String sql = "SELECT userid FROM currentcourses WHERE courseid = ?";
    ArrayList<Student> students = new ArrayList<>();
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, courseCode);
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
                ArrayList<Integer> currentCourses = getCurrentCourses(userId);
                student.setCurrentCourses(currentCourses);
                
                // Fetch taken courses
                Map<Integer, Map<String,String>> takenCourses = getTakenCourses(userId);
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
    public ArrayList<Integer> getCurrentCourses(String userId) throws SQLException {
        ArrayList<Integer> courses = new ArrayList<>();
        String sql = "SELECT courseid FROM currentcourses WHERE userid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courses.add(rs.getInt("courseid"));
            }
        }
        return courses;
    }
    public void addCurrentCourse(String userId, int courseId) throws SQLException {
        String sql = "INSERT INTO currentcourses (userid, courseid) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }
    public void removeCurrentCourse(String userId, int courseId) throws SQLException {
        String sql = "DELETE FROM currentcourses WHERE userid = ? AND courseid = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }
    
    public ArrayList<Instructor> getCourseInstructors(int courseId) throws SQLException {
        String sql = "SELECT userid FROM currentcourses WHERE courseid = ?";
        ArrayList<Instructor> instructors = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            String userid;
            while (rs.next()) {
                userid = rs.getString("userid");
                if (userid.charAt(2) == 'I') {
                    Instructor instructor = getInstructor(userid);
                    if (instructor != null) {
                        instructors.add(instructor);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load instructors by course", e);
        }
        return instructors;
    }

    /**
     * Get taken courses with grades for a student
     */
    public Map<Course, Map<String,String>> getTakenCoursesForTranscript(String userId) throws SQLException {
        Map<Course, Map<String,String>> courses = new HashMap<>();
        String sql = "SELECT c.courseid, c.coursename, c.coursedescription, c.courseyear, tc.semester, tc.grade " +
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
                String semester = rs.getString("semester");
                String grade = rs.getString("grade");

                Course course = new Course(
                        courseId,
                        courseName,
                        courseDescription,
                        courseYear
                );
                Map<String,String> courseRecord = new HashMap<>();
                courseRecord.put("grade", grade);
                courseRecord.put("semester", semester);
                courses.put(course, courseRecord);
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
                            // Parse boolean value - handle "true", "false", or null
                            if (parsedValue != null) {
                                String trimmed = parsedValue.trim();
                                if ("true".equalsIgnoreCase(trimmed)) {
                                    isDepartmentHead = true;
                                } else if ("false".equalsIgnoreCase(trimmed)) {
                                    isDepartmentHead = false;
                                } else {
                                    // If value is not a valid boolean, default to false
                                    isDepartmentHead = false;
                                }
                            } else {
                                isDepartmentHead = null;
                            }
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
                ArrayList<Integer> courses = getCurrentCourses(userId);
                instructor.setCourses(courses);
                ArrayList<String> responsibilities = getResponsibilities(userId);
                instructor.setResponsibilities(responsibilities);
                Map<String,String> officeHours = getOfficeHours(userId);
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

    public void addBenefit(String userId, String benefit) {
        String sql = "INSERT INTO benefits (userid, benefit) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, benefit);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert benefit: " + benefit);
            e.printStackTrace();
        }
    }

    public void deleteBenefit(String userId, String benefit) {
        String sql = "DELETE FROM benefits WHERE userid = ? AND benefit = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, benefit);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete benefit: " + benefit);
            e.printStackTrace();
        }
    }

    public void addResponsibility(String userId, String responsibility) {

        String sql = "INSERT INTO responsibilities (userid, responsibility) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, responsibility);
            ps.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Failed to delete Responsibility: " + responsibility);
            e.printStackTrace();
    }}
    public void deleteResponsibility(String userId, String responsibility) {
        String sql = "DELETE FROM responsibilities WHERE userid = ? AND responsibility = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, responsibility);
            ps.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Failed to delete Responsibility: " + responsibility);
            e.printStackTrace();
    }}
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
         String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
         try (Connection conn = getConnection(); PreparedStatement ps =
             conn.prepareStatement(attributeSql)) {
             ps.setString(1, admin.getId());
             ps.setInt(2, 5);
             ps.setString(3, toJsonValue(admin.getSalary()));
             ps.executeUpdate();
         } catch (SQLException e) {
             System.out.println("Failed to add admin attributes");
             e.printStackTrace();
         }
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
            ps.setInt(2, 8);
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
        String courseSql = "INSERT INTO courses (coursename, coursedescription, courseyear, coursedepartment) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(courseSql)) {
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getCourseDescription());
            ps.setString(3, course.getYear());
            ps.setString(4, course.getCourseDepartment());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add course");
            e.printStackTrace();
        }
    }

    public ArrayList<Course> getDepartmentCourses(String department) throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE coursedepartment = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int courseId = rs.getInt("courseid");
                String courseName = rs.getString("coursename");
                String courseDescription = rs.getString("coursedescription");
                String year = rs.getString("courseyear");
                String dept = rs.getString("coursedepartment");
                Course course = new Course(courseId, courseName, courseDescription, year, dept);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get department courses");
            e.printStackTrace();
            throw e;
        }
        return courses;
    }

    public ArrayList<Course> getInstructorCourses(String instructorId) throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT c.courseid, c.coursename, c.coursedescription, c.courseyear, c.coursedepartment " +
                "FROM courses c " +
                "JOIN currentcourses cc ON c.courseid = cc.courseid " +
                "WHERE cc.userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, instructorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int courseId = rs.getInt("courseid");
                String courseName = rs.getString("coursename");
                String courseDescription = rs.getString("coursedescription");
                String year = rs.getString("courseyear");
                String dept = rs.getString("coursedepartment");
                Course course = new Course(courseId, courseName, courseDescription, year);
                course.setCourseDepartment(dept);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get instructor courses");
            e.printStackTrace();
            throw e;
        }
        return courses;
    }
    public void addInstructorToCourse(String instructorId, int courseId) {
        String sql = "INSERT INTO currentcourses (userid, courseid) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, instructorId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add instructor to course");
            e.printStackTrace();
        }
    }
    public void removeInstructorFromCourse(String instructorId, int courseId) {
        String sql = "DELETE FROM currentcourses WHERE userid = ? AND courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, instructorId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to remove instructor from course");
            e.printStackTrace();
        }
    }

    public ArrayList<Course> getAllCourses() throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int courseId = rs.getInt("courseid");
                String courseName = rs.getString("coursename");
                String courseDescription = rs.getString("coursedescription");
                String year = rs.getString("courseyear");
                String dept = rs.getString("coursedepartment");
                Course course = new Course(courseId, courseName, courseDescription, year);
                course.setCourseDepartment(dept);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get all courses");
            e.printStackTrace();
            throw e;
        }
        return courses;
    }

    public void updateCourse(Course course) {
        String courseSql = "UPDATE courses SET coursename = ?, coursedescription = ?, courseyear = ?, coursedepartment = ? WHERE courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(courseSql)) {
            ps.setString(1, course.getCourseName());
            ps.setString(2, course.getCourseDescription());
            ps.setString(3, course.getYear());
            ps.setString(4, course.getCourseDepartment());
            ps.setInt(5, course.getCourseId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update course");
            e.printStackTrace();
            return;
        }
    }
    public Course getCourse(int courseId) {
        String courseSql = "SELECT * FROM courses WHERE courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(courseSql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String courseName = rs.getString("coursename");
                String courseDescription = rs.getString("coursedescription");
                String year = rs.getString("courseyear");
                String dept = rs.getString("coursedepartment");
                Course course = new Course(courseId, courseName, courseDescription, year);
                course.setCourseDepartment(dept);
                return course;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get course");
            e.printStackTrace();
           return null;
        }
        return null;
    }

    public void deleteCourse(int courseId) {
        String courseSql = "DELETE FROM courses WHERE courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(courseSql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete course");
            e.printStackTrace();
            return;
        }
    }





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

    public ArrayList<Assignment> getAssignments(int CourseID) throws SQLException {
        ArrayList<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT * FROM assignments WHERE courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, CourseID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                assignments.add(mapNewAssignment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignments");
            e.printStackTrace();
            throw e;
        }
        return assignments;
    }

    public Assignment getAssignmentDetails(int assignmentId) throws SQLException {
        Assignment assignment = null;
        String sql =
                "SELECT a.assignmentid, " +
                "       a.assignmentname, " +
                "       a.assignmenturl, " +
                "       a.assignmentdate, " +
                "       a.courseid, " +
                "       ag.userid, " +
                "       ag.grade, " +
                "       ag.feedback " +
                "FROM assignments a " +
                "LEFT JOIN assignmentgrades ag ON a.assignmentid = ag.assignmentid " +
                "WHERE a.assignmentid = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (assignment == null) {
                    assignment = new Assignment(
                            rs.getInt("assignmentid"),
                            rs.getString("assignmentname"),
                            rs.getString("assignmenturl"),
                            rs.getString("assignmentdate"),
                            rs.getInt("courseid"));
                }
                String userId = rs.getString("userid");
                if (userId != null) {
                    String grade = rs.getString("grade");
                    String feedback = rs.getString("feedback");
                    if (grade != null) {
                        assignment.getGrades().put(userId, grade);
                    }
                    if (feedback != null) {
                        assignment.getFeedback().put(userId, feedback);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignment details");
            e.printStackTrace();
            throw e;
        }

        return assignment;
    }
        
    private Assignment mapNewAssignment(ResultSet rs) throws SQLException {
        Assignment assignment = new Assignment(
                rs.getInt("assignmentid"),
                rs.getString("assignmentname"),
                rs.getString("assignmenturl"),
                rs.getString("assignmentdate"),
                rs.getInt("courseid"));
        return assignment;
    }
    public void addAssignment(int courseId,Assignment assignment) {
        String sql = "INSERT INTO assignments (courseid, assignmentname, assignmenturl, assignmentdate) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, courseId);
            ps.setString(2, assignment.getAssignmentName());
            ps.setString(3, upload(assignment.getUrl()));
            ps.setString(4, assignment.getAssignmentDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    assignment.setAssignmentId(keys.getInt(1));
                }
            }
        }catch (SQLException e) {
            System.out.println("Failed to add assignment");
            e.printStackTrace();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addMaterial(int courseId, Material material) {
        String sql = "INSERT INTO materials (materialname, courseid, materialurl) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getMaterialName());
            ps.setInt(2, courseId);
            ps.setString(3, upload(material.geturl()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add material");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Material> getMaterials(int CourseID) throws SQLException {
        ArrayList<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE courseid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, CourseID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                materials.add(new Material(
                        rs.getString("materialname"),
                        rs.getString("materialurl")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get materials");
            e.printStackTrace();
            throw e;
        }
        return materials;
    }
    public void deleteMaterial(int materialId) {
        String sql = "DELETE FROM materials WHERE materialid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete material");
            e.printStackTrace();
        }
    }

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

        jsonValue = jsonValue.trim();

        if (jsonValue.equals("{\"value\":null}")) {
            return null;
        }

        // Find the colon position
        int colonPos = jsonValue.indexOf(':');
        if (colonPos == -1) {
            return null;
        }
        
        // Start after colon, skip whitespace
        int start = colonPos + 1;
        while (start < jsonValue.length() && Character.isWhitespace(jsonValue.charAt(start))) {
            start++;
        }
        
        // Find end (before closing })
        int end = jsonValue.lastIndexOf('}');
        if (end == -1) {
            return null;
        }
        
        // If value starts with quote, extract between quotes
        if (start < jsonValue.length() && jsonValue.charAt(start) == '"') {
            start++;
            end = jsonValue.indexOf('"', start);
            if (end > start) {
                return jsonValue.substring(start, end);
            }
        } else {
            // Boolean or null value
            return jsonValue.substring(start, end).trim();
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

//    private Map<String, String> fetchTakenCourses(Connection conn, String userId) throws SQLException {
//        Map<String, String> taken = new HashMap<>();
//        String sql = "SELECT coursecode, grade FROM takencourses WHERE userid = ?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, userId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                taken.put(rs.getString("coursecode"), rs.getString("grade"));
//            }
//        }
//        return taken;
//    }
//
//    private void hydrateUserAttributeValues(Connection conn, String userId, Student student) throws SQLException {
//        String sql = "SELECT ua.attributename, uv.attributevalue " +
//                "FROM uservalues uv " +
//                "JOIN userattributes ua ON uv.attributeid = ua.attributeid " +
//                "WHERE uv.userid = ?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, userId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                String name = rs.getString("attributename");
//                String raw = rs.getString("attributevalue");
//                String value = extractScalarValue(raw);
//                if (name == null) {
//                    continue;
//                }
//                switch (name) {
//                    case "gpa":
//                        student.setGpa(value);
//                        break;
//                    case "major":
//                        student.setMajor(value);
//                        break;
//                    case "semester":
//                        student.setSemester(value);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//    }

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
            for (int courseId : student.getCurrentCourses()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertCurrentCoursesSql)) {
                    ps.setString(1, student.getId());
                    ps.setInt(2, courseId);
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
            String insertTakenCoursesSql = "INSERT INTO takencourses (userid, courseid, grade, semester) VALUES (?, ?, ?, ?)";
            for (Map.Entry<Integer, Map<String,String>> entry : student.getTakenCourses().entrySet()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertTakenCoursesSql)) {
                    ps.setString(1, student.getId());
                    ps.setInt(2, entry.getKey());
                    Map<String, String> courseData = entry.getValue();
                    ps.setString(3, courseData != null ? courseData.get("grade") : "");
                    ps.setString(4, courseData != null ? courseData.get("semester") : "");
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
            for (int courseId : instructor.getCourses()) {
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertCoursesSql)) {
                    ps.setString(1, instructor.getId());
                    ps.setInt(2, courseId);
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
            for (Map.Entry<String, String> entry : instructor.getOfficeHours().entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                try (Connection conn = getConnection();
                        PreparedStatement ps = conn.prepareStatement(insertOfficeHoursSql)) {
                    ps.setString(1, instructor.getId());
                    ps.setString(2, entry.getValue().trim()); // hour
                    ps.setString(3, entry.getKey().trim());   // day
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Failed to insert office hour: day=" + entry.getKey() + ", hour=" + entry.getValue());
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
    public void updateUserAttribute(int attributeId,String userID ,String Change) {
        String sql = "UPDATE uservalues SET attributeValue = ?::jsonb WHERE userid = ? AND attributeid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toJsonValue(Change));
            ps.setString(2, userID);
            ps.setInt(3, attributeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update user attribute");
            e.printStackTrace();
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
                try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(insertChildrenSql)) {
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

    public void deleteUser(String id) {
        String userSql = "DELETE FROM users WHERE userid = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete user");
            e.printStackTrace();
            return;
        }
    }

    public void changePassword(String id, String newPassword) {
        String sql = "UPDATE users SET userpassword = ? WHERE userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to change password");
            e.printStackTrace();
            return;
        }
    }
    public void addOfficeHours(String userId, Map<String, String> officeHours) {
        if (officeHours == null || officeHours.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO officehours (userid, officehour, officehourday) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : officeHours.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                ps.setString(1, userId);
                ps.setString(2, entry.getValue());
                ps.setString(3, entry.getKey());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.out.println("Failed to add office hours");
            e.printStackTrace();
        }
    }
    public Map<String, String> getOfficeHours(String userId) throws SQLException {
        Map<String, String> officeHours = new HashMap<>();
        String sql = "SELECT officehour, officehourday FROM officehours WHERE userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                officeHours.put(rs.getString("officehourday"), rs.getString("officehour"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get office hours");
            e.printStackTrace();
            throw e;
        }
        return officeHours;
    }
    public void deleteOfficeHours(String userId, String day, String hour) {
        if (userId == null || day == null || hour == null) {
            return;
        }
        String sql = "DELETE FROM officehours WHERE userid = ? AND officehourday = ? AND officehour = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, day);
            ps.setString(3, hour);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete office hour");
            e.printStackTrace();
        }
    }

    public void addAnnouncment(Announcment announcment) {
        String sql = "INSERT INTO announcements (announcementid, announcementtitle, announcementcontent, announcementdate, courseid) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, announcment.getId());
            ps.setString(2, announcment.getTitle());
            ps.setString(3, announcment.getContent());
            ps.setString(4, announcment.getDate());
            ps.setInt(5, announcment.getCourseid());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add announcement");
            e.printStackTrace();
        }
    }

    public ArrayList<Announcment> getGeneralAnnouncements() {
        String sql= "SELECT * FROM announcements WHERE courseid IS NULL ORDER BY announcementdate DESC";
        ArrayList<Announcment> announcements = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Announcment announcment = new Announcment(rs.getString("announcementtitle"),
                        rs.getString("announcementcontent"),
                        rs.getString("announcementdate"));
                announcment.setId(rs.getInt("announcementid"));
                announcment.setCourseid(rs.getInt("courseid"));
                announcements.add(announcment);
            }} catch (SQLException e) {
            System.out.println("Failed to get general announcements");
            e.printStackTrace();
        }return announcements;
    }

    public ArrayList<Announcment> getCourseAnnouncements(int courseId) {
        String sql= "SELECT * FROM announcements WHERE courseid = ? ORDER BY announcementdate DESC";
        ArrayList<Announcment> announcements = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Announcment announcment = new Announcment(rs.getString("announcementtitle"),
                        rs.getString("announcementcontent"),
                        rs.getString("announcementdate"));
                announcment.setId(rs.getInt("announcementid"));
                announcment.setCourseid(rs.getInt("courseid"));
                announcements.add(announcment);
            }} catch (SQLException e) {
            System.out.println("Failed to get course announcements");
            e.printStackTrace();
        }return announcements;
    }

    public ArrayList<Announcment> getStudentAnnouncements(String userId) {
        String sql = "SELECT * FROM announcements WHERE courseid IN (SELECT courseid FROM currentcourses WHERE userid = ?) ORDER BY announcementdate DESC";
        ArrayList<Announcment> announcements = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Announcment announcment = new Announcment(rs.getString("announcementtitle"),
                        rs.getString("announcementcontent"),
                        rs.getString("announcementdate"));
                announcment.setId(rs.getInt("announcementid"));
                announcment.setCourseid(rs.getInt("courseid"));
                announcements.add(announcment);
            }
        } catch (SQLException e) {
            System.out.println("Failed to get student announcements");
            e.printStackTrace();
        }
        return announcements;
    }

    public void addQuiz(Exam exam) {
        addExam(exam, Exam.ExamType.QUIZ);
    }

    public void addMidterm(Exam midterm) {
        addExam(midterm, Exam.ExamType.MIDTERM);
    }

    public void addFinal(Exam finalExam) {
        addExam(finalExam, Exam.ExamType.FINAL);
    }

    /**
     * Insert an exam row into exams table. examid is auto-incremented by Postgres.
     */
    public void addExam(Exam exam, Exam.ExamType defaultType) {
        if (exam == null || exam.getCourseId() == null) {
            return;
        }
        Exam.ExamType type = exam.getExamType() != null ? exam.getExamType() : defaultType;
        if (type == null) {
            return;
        }

        String sql = "INSERT INTO exams (examdate, examtype, courseid) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String examDate = exam.getExamDate() != null ? exam.getExamDate().trim() : null;
            ps.setString(1, examDate);
            ps.setString(2, type.toDbValue());
            ps.setInt(3, exam.getCourseId());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add exam");
            e.printStackTrace();
        }
    }

    public Exam getExam(int examId) {
        String sql = "SELECT * FROM exams WHERE examid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return mapExam(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private Exam mapExam(ResultSet rs) throws SQLException {
        Integer examId = rs.getInt("examid");
        if (rs.wasNull()) {
            examId = null;
        }
        int courseId = rs.getInt("courseid");
        String examDate = rs.getString("examdate");
        Exam.ExamType type = Exam.ExamType.fromDbValue(rs.getString("examtype"));
        return new Exam(examId, courseId, examDate, type);
    }

    private ArrayList<Exam> getCourseExams(int courseId, Exam.ExamType type) {
        String sql = (type == null)
                ? "SELECT * FROM exams WHERE courseid = ?"
                : "SELECT * FROM exams WHERE courseid = ? AND examtype = ?";
        ArrayList<Exam> exams = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            if (type != null) {
                ps.setString(2, type.toDbValue());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exams.add(mapExam(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get course exams");
            e.printStackTrace();
        }
        return exams;
    }

    // ---- Course exams (separated + all) ----
    public ArrayList<Exam> getCourseAllExams(int courseId) {
        return getCourseExams(courseId, null);
    }

    public ArrayList<Exam> getCourseQuizzes(int courseId) {
        return getCourseExams(courseId, Exam.ExamType.QUIZ);
    }

    public ArrayList<Exam> getCourseMidterms(int courseId) {
        return getCourseExams(courseId, Exam.ExamType.MIDTERM);
    }

    public ArrayList<Exam> getCourseFinals(int courseId) {
        return getCourseExams(courseId, Exam.ExamType.FINAL);
    }

    public ArrayList<Exam> getStudentQuizzes(String userId) {
        return getStudentExams(userId, Exam.ExamType.QUIZ);
    }

    public ArrayList<Exam> getStudentMidterms(String userId) {
        return getStudentExams(userId, Exam.ExamType.MIDTERM);
    }

    public ArrayList<Exam> getStudentFinals(String userId) {
        return getStudentExams(userId, Exam.ExamType.FINAL);
    }

    public ArrayList<Exam> getStudentAllExams(String userId) {
        return getStudentExams(userId, null);
    }

    public ArrayList<Exam> getStudentExams(String userId, Exam.ExamType type) {
        String sql = (type == null)
                ? "SELECT * FROM exams WHERE courseid IN (SELECT courseid FROM currentcourses WHERE userid = ?) ORDER BY examdate DESC"
                : "SELECT * FROM exams WHERE examtype = ? AND courseid IN (SELECT courseid FROM currentcourses WHERE userid = ?) ORDER BY examdate DESC";
        ArrayList<Exam> exams = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (type == null) {
                ps.setString(1, userId);
            } else {
                ps.setString(1, type.toDbValue());
                ps.setString(2, userId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exams.add(mapExam(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get student exams");
            e.printStackTrace();
        }
        return exams;
    }

    public void addAssignmentGrade(int assignmentId, String userId, String grade) {
        String sql = "INSERT INTO assignmentgrades (assignmentid, userid, grade) VALUES (?, ?, ?) " +
                "ON CONFLICT (assignmentid, userid) DO UPDATE SET grade = EXCLUDED.grade";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setString(2, userId);
            ps.setString(3, grade);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add/update assignment grade");
            e.printStackTrace();
        }
    }

    public void addExamGrade(int examId, String userId, String grade) {
        String sql = "INSERT INTO examgrades (examid, userid, grade) VALUES (?, ?, ?) " +
                "ON CONFLICT (examid, userid) DO UPDATE SET grade = EXCLUDED.grade";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, userId);
            ps.setString(3, grade);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add/update exam grade");
            e.printStackTrace();
        }
    }

    public void addAssignmentFeedback(int assignmentId, String userId, String feedback) {
        String sql = "INSERT INTO assignmentgrades (assignmentid, userid, grade, feedback) VALUES (?, ?, NULL, ?) " +
                "ON CONFLICT (assignmentid, userid) DO UPDATE SET feedback = EXCLUDED.feedback";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setString(2, userId);
            ps.setString(3, feedback);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add/update assignment feedback");
            e.printStackTrace();
        }
    }

    public void addExamFeedback(int examId, String userId, String feedback) {
        String sql = "INSERT INTO examgrades (examid, userid, grade, feedback) VALUES (?, ?, NULL, ?) " +
                "ON CONFLICT (examid, userid) DO UPDATE SET feedback = EXCLUDED.feedback";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, userId);
            ps.setString(3, feedback);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to add/update exam feedback");
            e.printStackTrace();
        }
    }

    public ArrayList<Assignment> getAllAssignmentsForStudent() throws SQLException {
        ArrayList<Assignment> assignments = new ArrayList<>();
        String sql = "SELECT a.* FROM assignments a WHERE a.courseid IN (SELECT courseid FROM currentcourses WHERE userid = ? )";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, GlobalData.getCurrentlyLoggedIN());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                assignments.add(mapNewAssignment(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignments for student");
            e.printStackTrace();
            throw e;
        }
        return assignments;
    }

    public Map<String,String> getAssignmentGrades(int assignmentId) {
        String sql = "SELECT userid, grade FROM assignmentgrades WHERE assignmentid = ?";
        Map<String,String> grades = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                grades.put(rs.getString("userid"), rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignment grades");
            e.printStackTrace();
        }
        return grades;
    }
    public Map<String,String> getExamGrades(int examId) {
        String sql = "SELECT userid, grade FROM examgrades WHERE examid = ?";
        Map<String, String> grades = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                grades.put(rs.getString("userid"), rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get exam grades");
            e.printStackTrace();
        }
        return grades;
    }
    public Map <String,String> getAssignmentFeedback(int assignmentId) {
        String sql = "SELECT userid, feedback FROM assignmentgrades WHERE assignmentid = ?";
        Map<String, String> feedbacks = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                feedbacks.put(rs.getString("userid"), rs.getString("feedback"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignment feedbacks");
            e.printStackTrace();
        }
        return feedbacks;
    }
    public Map <String,String> getExamFeedback(int examId) {
        String sql = "SELECT userid, feedback FROM examgrades WHERE examid = ?";
        Map<String, String> feedbacks = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                feedbacks.put(rs.getString("userid"), rs.getString("feedback"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get exam feedbacks");
            e.printStackTrace();
        }
        return feedbacks;
    }

    public  String getExamGradeForStudent(int examId, String userId) {
        String sql = "SELECT grade FROM examgrades WHERE examid = ? AND userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("grade");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get exam grade for student");
            e.printStackTrace();
        }
        return null;
    }
    public String getAssignmentGradeForStudent(int assignmentId, String userId) {
        String sql = "SELECT grade FROM assignmentgrades WHERE assignmentid = ? AND userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("grade");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignment grade for student");
            e.printStackTrace();
        }
        return null;
    }
    public String getAssignmentFeedbackForStudent(int assignmentId, String userId) {
        String sql = "SELECT feedback FROM assignmentgrades WHERE assignmentid = ? AND userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("feedback");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get assignment feedback for student");
            e.printStackTrace();
        }
        return null;
    }
    public String getExamFeedbackForStudent(int examId, String userId) {
        String sql = "SELECT feedback FROM examgrades WHERE examid = ? AND userid = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setString(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("feedback");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get exam feedback for student");
            e.printStackTrace();
        }
        return null;
    }
    public Map<String,String> getStudentCourseGrades(String userId,Integer courseId) {
        String sql = "SELECT a.assignmentid, ag.grade AS assignment_grade, e.examid, eg.grade AS exam_grade " +
                "FROM assignments a " +
                "LEFT JOIN assignmentgrades ag ON a.assignmentid = ag.assignmentid AND ag.userid = ? " +
                "LEFT JOIN exams e ON a.courseid = e.courseid " +
                "LEFT JOIN examgrades eg ON e.examid = eg.examid AND eg.userid = ? " +
                "WHERE a.courseid = ?";

        Map<String, String> grades = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setInt(3, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int assignmentId = rs.getInt("assignmentid");
                String assignmentGrade = rs.getString("assignment_grade");
                if (assignmentGrade != null) {
                    grades.put("Assignment " + assignmentId, assignmentGrade);
                }

                int examId = rs.getInt("examid");
                String examGrade = rs.getString("exam_grade");
                if (examGrade != null) {
                    grades.put("Exam " + examId, examGrade);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get student course grades");
            e.printStackTrace();
        }
        return grades;
    }
    public Map<String,String> getStudentCourseFeedback(String userId,Integer courseId) {
        String sql = "SELECT a.assignmentid, ag.feedback AS assignment_feedback, e.examid, eg.feedback AS exam_feedback " +
                "FROM assignments a " +
                "LEFT JOIN assignmentgrades ag ON a.assignmentid = ag.assignmentid AND ag.userid = ? " +
                "LEFT JOIN exams e ON a.courseid = e.courseid " +
                "LEFT JOIN examgrades eg ON e.examid = eg.examid AND eg.userid = ? " +
                "WHERE a.courseid = ?";

        Map<String, String> feedbacks = new HashMap<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setInt(3, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int assignmentId = rs.getInt("assignmentid");
                String assignmentFeedback = rs.getString("assignment_feedback");
                if (assignmentFeedback != null) {
                    feedbacks.put("Assignment " + assignmentId, assignmentFeedback);
                }

                int examId = rs.getInt("examid");
                String examFeedback = rs.getString("exam_feedback");
                if (examFeedback != null) {
                    feedbacks.put("Exam " + examId, examFeedback);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to get student course feedbacks");
            e.printStackTrace();
        }
        return feedbacks;
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

