package com.example.ums;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {


    private static final Dotenv dotenv = Dotenv.load();

    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");
    private static final String JDBC_URL = dotenv.get("DB_URL");

    /**
     * Get a new database connection using environment variables (plain JDBC).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    public void addAdmission(Admission admission) throws SQLException {
        String sql = "INSERT INTO Admissions " +
                "(applicantname, email, phonenumber, dateofbirth, highschoolgpa, admissiondate, major, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, admission.getName());
            ps.setString(2, admission.getEmail());
            ps.setString(3, admission.getPhoneNumber());
            ps.setString(4, admission.getDateOfBirth());
            ps.setString(5, admission.getHighschoolGPA());
            ps.setString(6, admission.getYearOfAdmission());
            ps.setString(7, admission.getMajor());
            ps.setString(8, admission.getStatus());

            ps.executeUpdate();

            // Get auto-generated primary key
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                   admission.setAdmissionId(rs.getInt(1));   // admissionid
                }
            }
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
        }
        return null;
    }
    public void updateAdmission(Admission admission) throws SQLException {
        String sql = "UPDATE Admissions SET applicantname = ?, email = ?, phonenumber = ?, dateofbirth = ?, highschoolgpa = ?, admissiondate = ?, major = ?, status = ? WHERE admissionid = ?";
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


    /**
     * Add a student into the EAV schema:
     * - Insert into Users table (with UserType = 'Student')
     * - Insert attributes into UserAttributes / UserValues as JSONB
     */
    public void addStudent(Student student) {
        String userSql = "INSERT INTO users (userid,usertype,name,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, student.getId());
            ps.setString(2, student.getType());
            ps.setString(3, student.getName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPassword());
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
                        ps.setString(3, toJsonValue(null));
                        break;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add student attributes");
                e.printStackTrace();
            }
        }
    }

public void addHR(HR hr) {
    String userSql = "INSERT INTO users (userid,usertype,name,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, hr.getId());
            ps.setString(2, hr.getType());
            ps.setString(3, hr.getName());
            ps.setString(4, hr.getEmail());
            ps.setString(5, hr.getPassword());
            ps.setString(6, hr.getPhoneNumber());
            ps.setString(7, hr.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add HR");
            e.printStackTrace();
            return;
        }
        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
        for (int i = 5; i <= 6; i++) {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(attributeSql)) {
                ps.setString(1, hr.getId());
                ps.setInt(2, i);
                switch (i) {
                    case 1:
                        ps.setString(3, toJsonValue(hr.getdateOfBirth()));
                        break;
                    case 2:
                        ps.setString(3, toJsonValue(hr.getSalary()));
                        break;
                    default:
                        ps.setString(3, toJsonValue(null));
                        break;
                }
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add admin attributes");
                e.printStackTrace();
            }
        }
    }
    public void addAdmin(Admin admin) {
        String userSql = "INSERT INTO users (userid,usertype,name,email,userpassword,phoneNumber,dateofbirth) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(userSql)) {
            ps.setString(1, admin.getId());
            ps.setString(2, admin.getType());
            ps.setString(3, admin.getName());
            ps.setString(4, admin.getEmail());
            ps.setString(5, admin.getPassword());
            ps.setString(6, admin.getPhoneNumber());
            ps.setString(7, admin.getdateOfBirth());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add admin");
            e.printStackTrace();
            return;
        }
        String attributeSql = "INSERT INTO uservalues (userid, attributeid, attributeValue) VALUES (?, ?, ?::jsonb)";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(attributeSql)) {
                ps.setString(1, admin.getId());
                ps.setInt(2, 6);
                ps.setString(3, toJsonValue(admin.getSalary()));
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Failed to add admin attributes");
                e.printStackTrace();
            }
        }
    



    // public static void addStudent(Student student) {

    //     String userType = "Student";
    //     try (Connection conn = getConnection()) {

    //         conn.setAutoCommit(false);
    //         // 1) Insert into Users and get generated UserID
    //         long userId;
    //         String insertUserSql = "INSERT INTO Users (UserType) VALUES (?) RETURNING UserID";
    //         try (PreparedStatement ps = conn.prepareStatement(insertUserSql)) {
    //             ps.setString(1, userType);
    //             try (ResultSet rs = ps.executeQuery()) {
    //                 if (rs.next()) {
    //                     userId = rs.getLong("UserID");
    //                 } else {
    //                     throw new SQLException("Failed to retrieve generated UserID");
    //                 }
    //             }
    //         }

    //         // 2) Insert main attributes as EAV JSONB values
    //         insertUserAttributeValue(conn, userId, "id", "string", student.getId());
    //         insertUserAttributeValue(conn, userId, "name", "string", student.getName());
    //         insertUserAttributeValue(conn, userId, "email", "string", student.getEmail());
    //         insertUserAttributeValue(conn, userId, "phoneNumber", "string", student.getPhoneNumber());
    //         insertUserAttributeValue(conn, userId, "dateOfBirth", "string", student.getdateOfBirth());
    //         insertUserAttributeValue(conn, userId, "major", "string", student.getMajor());
    //         insertUserAttributeValue(conn, userId, "semester", "string", student.getSemester());
    //         insertUserAttributeValue(conn, userId, "gpa", "float", student.getGpa());

    //         // Optional: currentCourses as JSON array
    //         if (student.getCurrentCourses() != null) {
    //             String coursesJson = listToJsonArray(student.getCurrentCourses());
    //             insertUserAttributeValue(conn, userId, "currentCourses", "array", coursesJson, true);
    //         }

    //         // Optional: takenCourses as JSON object (course -> grade)
    //         if (student.getTakenCourses() != null) {
    //             String takenJson = mapToJsonObject(student.getTakenCourses());
    //             insertUserAttributeValue(conn, userId, "takenCourses", "object", takenJson, true);
    //         }

    //         conn.commit();
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed to save student in EAV schema", e);
    //     }
    // }

    // /**
    //  * Insert or reuse an attribute definition, then save a value for that user.
    //  */
    // private static void insertUserAttributeValue(Connection conn,
    //                                              long userId,
    //                                              String attributeName,
    //                                              String valueType,
    //                                              String value) throws SQLException {
    //     insertUserAttributeValue(conn, userId, attributeName, valueType, value, false);
    // }

    // private static void insertUserAttributeValue(Connection conn,
    //                                              long userId,
    //                                              String attributeName,
    //                                              String valueType,
    //                                              String value,
    //                                              boolean valueIsRawJson) throws SQLException {
    //     int attributeId = getOrCreateAttributeId(conn, attributeName, valueType);

    //     String jsonValue = valueIsRawJson ? value : "{\"value\":\"" + escapeJson(value) + "\"}";

    //     String sql = "INSERT INTO UserValues (UserID, AttributeID, AttributeValue) " +
    //             "VALUES (?, ?, cast(? as jsonb))";
    //     try (PreparedStatement ps = conn.prepareStatement(sql)) {
    //         ps.setLong(1, userId);
    //         ps.setInt(2, attributeId);
    //         ps.setString(3, jsonValue);
    //         ps.executeUpdate();
    //     }
    // }

    // /**
    //  * Get existing AttributeID or create a new one if it doesn't exist.
    //  */
    // private static int getOrCreateAttributeId(Connection conn,
    //                                           String attributeName,
    //                                           String valueType) throws SQLException {
    //     // 1) Try to find existing
    //     String selectSql = "SELECT AttributeID FROM UserAttributes WHERE AttributeName = ? AND ValueType = ? LIMIT 1";
    //     try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
    //         ps.setString(1, attributeName);
    //         ps.setString(2, valueType);
    //         try (ResultSet rs = ps.executeQuery()) {
    //             if (rs.next()) {
    //                 return rs.getInt("AttributeID");
    //             }
    //         }
    //     }

    //     // 2) Create new attribute
    //     String insertSql = "INSERT INTO UserAttributes (AttributeName, ValueType) " +
    //             "VALUES (?, ?) RETURNING AttributeID";
    //     try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
    //         ps.setString(1, attributeName);
    //         ps.setString(2, valueType);
    //         try (ResultSet rs = ps.executeQuery()) {
    //             if (rs.next()) {
    //                 return rs.getInt("AttributeID");
    //             } else {
    //                 throw new SQLException("Failed to create UserAttribute for " + attributeName);
    //             }
    //         }
    //     }
    // }

    // private static String listToJsonArray(Iterable<String> values) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("[");
    //     boolean first = true;
    //     for (String v : values) {
    //         if (!first) {
    //             sb.append(",");
    //         }
    //         sb.append("\"").append(escapeJson(v)).append("\"");
    //         first = false;
    //     }
    //     sb.append("]");
    //     return sb.toString();
    // }

    // private static String mapToJsonObject(Map<String, String> map) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("{");
    //     boolean first = true;
    //     for (Map.Entry<String, String> entry : map.entrySet()) {
    //         if (!first) {
    //             sb.append(",");
    //         }
    //         sb.append("\"")
    //                 .append(escapeJson(entry.getKey()))
    //                 .append("\":\"")
    //                 .append(escapeJson(entry.getValue()))
    //                 .append("\"");
    //         first = false;
    //     }
    //     sb.append("}");
    //     return sb.toString();
    // }

    // private static String escapeJson(String value) {
    //     if (value == null) {
    //         return "";
    //     }
    //     return value
    //             .replace("\\", "\\\\")
    //             .replace("\"", "\\\"")
    //             .replace("\n", "\\n")
    //             .replace("\r", "\\r")
    //             .replace("\t", "\\t");
    // }

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

    private static String toJsonValue(String value) {
        if (value == null) {
            return "{\"value\":null}";
        }
        return "{\"value\":\"" + escapeJson(value) + "\"}";
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Admission mapAdmission(ResultSet rs) throws SQLException {
        Admission admission = new Admission(
                rs.getInt("admissionid"),
                rs.getString("applicantname"),
                rs.getString("phonenumber"),
                rs.getString("email"),
                rs.getString("dateofbirth"),
                rs.getString("major"),
                rs.getString("highschoolgpa")
        );
        admission.setStatus(rs.getString("status"));
        admission.setYearOfAdmission(rs.getString("admissiondate"));
        return admission;
    }
}
