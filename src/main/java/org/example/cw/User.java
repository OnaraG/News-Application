package org.example.cw;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String username;
    private String password;
    private List<String> preferences;
    private List<String> readingHistory;

    // Constructor
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.preferences = new ArrayList<>();
        this.readingHistory = new ArrayList<>();
    }

    // Getters and Setters
    public int getUserId() { return userId; }  // Getter for userId
    public void setUserId(int userId) { this.userId = userId; }  // Setter for userId

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<String> getPreferences() { return preferences; }
    public void setPreferences(List<String> preferences) { this.preferences = preferences; }

    public List<String> getReadingHistory() { return readingHistory; }
    public void setReadingHistory(List<String> readingHistory) { this.readingHistory=readingHistory; }

    public void addToPreferences(String preference) {
        this.preferences.add(preference);
    }
}
