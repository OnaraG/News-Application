package org.example.cw;

/**
 * A utility class for managing the state of the currently logged-in user.
 * Stores the user's ID and username as static fields for global access across the application.
 */
public class CurrentUser {

    // Static fields to store the logged-in user's ID and username
    private static int userId; // Stores the ID of the currently logged-in user
    private static String username; // Stores the username of the currently logged-in user

    /**
     * Sets the current user's details.
     *
     * @param id   The ID of the user.
     * @param name The username of the user.
     */
    public static void setUser(int id, String name) {
        userId = id;
        username = name;
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The user ID.
     */
    public static int getId() {
        return userId;
    }

    /**
     * Retrieves the username of the currently logged-in user.
     *
     * @return The username.
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Clears the current user's details.
     * Resets the user ID to 0 and the username to null.
     */
    public static void clear() {
        userId = 0; // Reset the user ID to the default value
        username = null; // Clear the username
    }
}
