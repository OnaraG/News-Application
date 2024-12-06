package org.example.cw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;  // Unique identifier for the user
    private String username;  // Username chosen by the user
    private String password;  // Password associated with the user
    private List<String> preferences;  // List of user's preferred categories or topics
    private List<String> readingHistory;  // List of articles the user has read

    // Constructor to initialize a new User object
    public User(int userId, String username, String password) {
        this.userId = userId;  // Set the userId for this user
        this.username = username;  // Set the username for this user
        this.password = password;  // Set the password for this user
        this.preferences = new ArrayList<>();  // Initialize an empty list for preferences
        this.readingHistory = new ArrayList<>();  // Initialize an empty list for reading history
    }

    // Getters and Setters
    public int getUserId() { return userId; }  // Get the user ID
    public void setUserId(int userId) { this.userId = userId; }  // Set the user ID

    public String getUsername() { return username; }  // Get the username
    public void setUsername(String username) { this.username = username; }  // Set the username

    public String getPassword() { return password; }  // Get the password
    public void setPassword(String password) { this.password = password; }  // Set the password

    public List<String> getPreferences() { return preferences; }  // Get the user's preferences
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }  // Set the user's preferences

    public List<String> getReadingHistory() { return readingHistory; }  // Get the user's reading history
    public void setReadingHistory(List<String> readingHistory) { this.readingHistory = readingHistory; }  // Set the user's reading history

    // Method to add a preference to the user's preferences list
    public void addToPreferences(String preference) {
        this.preferences.add(preference);  // Add the given preference to the list
    }
}
