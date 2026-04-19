package com.ip241k.skoryk.dao;

import com.ip241k.skoryk.model.User;
import com.ip241k.skoryk.config.DatabaseConfig;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private DataSource dataSource;

    public UserDAO() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    // CREATE
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            conn.setAutoCommit(false);
            try {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                
                conn.commit();
                System.out.println("User created successfully!");
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Failed to create user: " + e.getMessage(), e);
            }
        }
    }

    // READ (By ID)
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    // READ ALL
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    // UPDATE
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            try {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setInt(3, user.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("User not found");
                }
                
                conn.commit();
                System.out.println("User updated successfully!");
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Failed to update user: " + e.getMessage(), e);
            }
        }
    }

    // DELETE
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            try {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new SQLException("User not found");
                }
                
                conn.commit();
                System.out.println("User deleted successfully!");
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Failed to delete user: " + e.getMessage(), e);
            }
        }
    }

    // Helper method
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email")
        );
    }
}