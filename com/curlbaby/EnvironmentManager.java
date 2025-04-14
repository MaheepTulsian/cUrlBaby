package com.curlbaby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentManager {
    private final String dbPath;
    private Connection connection;
    private final UIManager uiManager;
    private String activeEnvironment = null;
    private Map<String, String> activeVariables = new HashMap<>();
    
    public EnvironmentManager(UIManager uiManager) {
        this.uiManager = uiManager;
        String userHome = System.getProperty("user.home");
        File dataDir = new File(userHome, ".curlbaby");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        this.dbPath = new File(dataDir, "environments.db").getAbsolutePath();
        initializeDatabase();
        loadActiveEnvironment();
    }
    
    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            
            try (Statement stmt = connection.createStatement()) {
                // Create environments table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS environments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE NOT NULL, " +
                    "description TEXT, " +
                    "is_active INTEGER DEFAULT 0)"
                );
                
                // Create variables table
                stmt.execute(
                    "CREATE TABLE IF NOT EXISTS environment_variables (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "environment_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "value TEXT, " +
                    "FOREIGN KEY(environment_id) REFERENCES environments(id) ON DELETE CASCADE, " +
                    "UNIQUE(environment_id, name))"
                );
                
                // Enable foreign keys
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error initializing environment database: " + e.getMessage());
        }
    }
    
    private void loadActiveEnvironment() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT id, name FROM environments WHERE is_active = 1 LIMIT 1")) {
            
            if (rs.next()) {
                int envId = rs.getInt("id");
                activeEnvironment = rs.getString("name");
                loadEnvironmentVariables(envId);
                
                uiManager.printInfo("Active environment: " + activeEnvironment);
            }
        } catch (SQLException e) {
            uiManager.printError("Error loading active environment: " + e.getMessage());
        }
    }
    
    private void loadEnvironmentVariables(int envId) {
        activeVariables.clear();
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT name, value FROM environment_variables WHERE environment_id = ?")) {
            pstmt.setInt(1, envId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    activeVariables.put(rs.getString("name"), rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            uiManager.printError("Error loading environment variables: " + e.getMessage());
        }
    }
    
    public boolean createEnvironment(String name, String description) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO environments (name, description) VALUES (?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                uiManager.printError("Environment name already exists: " + name);
            } else {
                uiManager.printError("Error creating environment: " + e.getMessage());
            }
            return false;
        }
    }
    
    public boolean setActiveEnvironment(String name) {
        Integer envId = getEnvironmentIdByName(name);
        if (envId == null) {
            uiManager.printError("Environment not found: " + name);
            return false;
        }
        
        try {
            // First, set all environments to inactive
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("UPDATE environments SET is_active = 0");
            }
            
            // Then set the selected one to active
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE environments SET is_active = 1 WHERE id = ?")) {
                pstmt.setInt(1, envId);
                pstmt.executeUpdate();
            }
            
            activeEnvironment = name;
            loadEnvironmentVariables(envId);
            uiManager.printSuccess("Activated environment: " + name);
            
            return true;
        } catch (SQLException e) {
            uiManager.printError("Error setting active environment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteEnvironment(int envId) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "DELETE FROM environments WHERE id = ?")) {
            pstmt.setInt(1, envId);
            int deleted = pstmt.executeUpdate();
            
            if (deleted > 0) {
                // If the deleted environment was active, clear the active environment
                try (PreparedStatement checkPstmt = connection.prepareStatement(
                        "SELECT COUNT(*) FROM environments WHERE is_active = 1")) {
                    ResultSet rs = checkPstmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        activeEnvironment = null;
                        activeVariables.clear();
                    }
                }
            }
            
            return deleted > 0;
        } catch (SQLException e) {
            uiManager.printError("Error deleting environment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean setVariable(String environmentName, String name, String value) {
        Integer envId = getEnvironmentIdByName(environmentName);
        if (envId == null) {
            uiManager.printError("Environment not found: " + environmentName);
            return false;
        }
        
        try {
            // First try to update if it exists
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE environment_variables SET value = ? " +
                    "WHERE environment_id = ? AND name = ?")) {
                pstmt.setString(1, value);
                pstmt.setInt(2, envId);
                pstmt.setString(3, name);
                int updated = pstmt.executeUpdate();
                
                if (updated == 0) {
                    // Variable doesn't exist, insert it
                    try (PreparedStatement insertPstmt = connection.prepareStatement(
                            "INSERT INTO environment_variables (environment_id, name, value) " +
                            "VALUES (?, ?, ?)")) {
                        insertPstmt.setInt(1, envId);
                        insertPstmt.setString(2, name);
                        insertPstmt.setString(3, value);
                        insertPstmt.executeUpdate();
                    }
                }
            }
            
            // If this is the active environment, update the active variables
            if (environmentName.equals(activeEnvironment)) {
                activeVariables.put(name, value);
            }
            
            return true;
        } catch (SQLException e) {
            uiManager.printError("Error setting variable: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteVariable(String environmentName, String name) {
        Integer envId = getEnvironmentIdByName(environmentName);
        if (envId == null) {
            uiManager.printError("Environment not found: " + environmentName);
            return false;
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "DELETE FROM environment_variables WHERE environment_id = ? AND name = ?")) {
            pstmt.setInt(1, envId);
            pstmt.setString(2, name);
            int deleted = pstmt.executeUpdate();
            
            // If this is the active environment, remove from active variables
            if (environmentName.equals(activeEnvironment)) {
                activeVariables.remove(name);
            }
            
            return deleted > 0;
        } catch (SQLException e) {
            uiManager.printError("Error deleting variable: " + e.getMessage());
            return false;
        }
    }
    
    public List<Map<String, Object>> getAllEnvironments() {
        List<Map<String, Object>> environments = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT id, name, description, is_active FROM environments ORDER BY name")) {
            
            while (rs.next()) {
                Map<String, Object> env = new HashMap<>();
                env.put("id", rs.getInt("id"));
                env.put("name", rs.getString("name"));
                env.put("description", rs.getString("description"));
                env.put("is_active", rs.getInt("is_active") == 1);
                environments.add(env);
            }
            
        } catch (SQLException e) {
            uiManager.printError("Error retrieving environments: " + e.getMessage());
        }
        return environments;
    }
    
    public Map<String, Object> getEnvironmentById(int envId) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id, name, description, is_active FROM environments WHERE id = ?")) {
            pstmt.setInt(1, envId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> env = new HashMap<>();
                    env.put("id", rs.getInt("id"));
                    env.put("name", rs.getString("name"));
                    env.put("description", rs.getString("description"));
                    env.put("is_active", rs.getInt("is_active") == 1);
                    return env;
                }
            }
        } catch (SQLException e) {
            uiManager.printError("Error retrieving environment: " + e.getMessage());
        }
        return null;
    }
    
    public Integer getEnvironmentIdByName(String name) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id FROM environments WHERE name = ?")) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            uiManager.printError("Error retrieving environment ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<Map<String, Object>> getVariablesByEnvironmentId(int envId) {
        List<Map<String, Object>> variables = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(
                "SELECT id, name, value FROM environment_variables " +
                "WHERE environment_id = ? ORDER BY name")) {
            pstmt.setInt(1, envId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> variable = new HashMap<>();
                    variable.put("id", rs.getInt("id"));
                    variable.put("name", rs.getString("name"));
                    variable.put("value", rs.getString("value"));
                    variables.add(variable);
                }
            }
        } catch (SQLException e) {
            uiManager.printError("Error retrieving variables: " + e.getMessage());
        }
        return variables;
    }
    
    public String substituteVariables(String input) {
        if (input == null || activeVariables.isEmpty()) {
            return input;
        }
        
        // Substitute {{variable}} format
        Pattern pattern = Pattern.compile("\\{\\{([^{}]+)\\}\\}");
        Matcher matcher = pattern.matcher(input);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = activeVariables.getOrDefault(varName, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    public String getActiveEnvironment() {
        return activeEnvironment;
    }
    
    public Map<String, String> getActiveVariables() {
        return new HashMap<>(activeVariables);
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}