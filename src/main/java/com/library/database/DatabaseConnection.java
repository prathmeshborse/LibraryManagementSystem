package com.library.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/LibraryDB";
    private static final String USER = "";  // Change if needed
    private static final String PASSWORD = "";  // Change if needed

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(10);  // Max connections in the pool
            config.setMinimumIdle(2);       // Min idle connections
            config.setIdleTimeout(30000);   // 30 sec idle timeout
            config.setMaxLifetime(1800000); // 30 min max connection lifetime

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new RuntimeException("Database pool initialization failed!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection(); // Get connection from the pool
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();  // Close the pool on shutdown
        }
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connection successful using HikariCP!");
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to database.");
            e.printStackTrace();
        }
    }
}

