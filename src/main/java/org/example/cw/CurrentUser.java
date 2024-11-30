package org.example.cw;

public class CurrentUser {
    private static int userId;   // Stores the currently logged-in user's ID
    private static String username;

    public static void setUser(int id, String name) {
        userId = id;
        username = name;
    }

    public static int getId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        userId = 0;
        username = null;
    }
}
