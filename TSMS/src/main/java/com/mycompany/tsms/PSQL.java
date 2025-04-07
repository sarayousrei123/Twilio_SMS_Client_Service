package com.mycompany.tsms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PSQL {         
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/twillo"; 
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    static {
        try {
            Class.forName(JDBC_DRIVER);  
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found!", e);
        }
    }

    public static Connection getConnection() {
        try {           
            return DriverManager.getConnection(URL, USER, PASSWORD);  
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage(), e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}
