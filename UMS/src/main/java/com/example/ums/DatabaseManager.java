package com.example.ums;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class DatabaseManager {

    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String JDBC_URL = System.getenv("DB_URL");

    /**
     * Get a new database connection using environment variables (plain JDBC).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Add a student into the EAV schema:
     * - Insert into Users table (with UserType = 'Student')
     * - Insert attributes into UserAttributes / UserValues as JSONB
     */
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
}
