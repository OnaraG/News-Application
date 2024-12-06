package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing database operations and scene transitions in the application.
 */
public class DBUtils {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Onaragamage2005";

    /**
     * Establishes a connection to the database.
     *
     * @return A Connection object to interact with the database.
     * @throws SQLException If there is an issue with establishing the connection.
     */
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Changes the current scene to the specified FXML file.
     *
     * @param event    The event triggering the scene change.
     * @param fxmlFile The name of the FXML file to load.
     * @param title    The title of the new stage.
     * @param username The username to pass to the controller (can be null).
     */
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            Parent root = loader.load();

            // Pass username to the controller if provided
            if (username != null) {
                HomeController homeController = loader.getController();
                homeController.setUserInformation(username);
            }

            // Set up the stage with the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 782, 649)); // Adjust scene dimensions if needed
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading scene", "Unable to load the specified scene.");
        }
    }

    /**
     * Handles user sign-up by inserting a new user into the database.
     *
     * @param event    The event triggering the sign-up process.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    public static void signUpUser(ActionEvent event, String username, String password) {
        String checkQuery = "SELECT username FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        String selectQuery = "SELECT LAST_INSERT_ID()"; // Retrieves the last inserted user_id

        try (Connection connection = getConnection();
             PreparedStatement psCheckUserExists = connection.prepareStatement(checkQuery);
             PreparedStatement psInsert = connection.prepareStatement(insertQuery);
             PreparedStatement psSelect = connection.prepareStatement(selectQuery)) {

            // Check if the username already exists
            psCheckUserExists.setString(1, username);
            try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                if (resultSet.next()) {
                    showAlert("Signup Error", "Username already exists. Choose a different one.");
                    return;
                }
            }

            // Insert the new user into the database
            psInsert.setString(1, username);
            psInsert.setString(2, password);
            psInsert.executeUpdate();

            // Retrieve the newly inserted user_id
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    CurrentUser.setUser(userId, username); // Set the current user details
                }
            }

            // Navigate to the home scene
            changeScene(event, "Home.fxml", "Welcome!", username);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to sign up. Please try again later.");
        }
    }

    /**
     * Handles user login by validating credentials and redirecting to the home scene.
     *
     * @param event    The event triggering the login process.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    public static void logInUser(ActionEvent event, String username, String password) {
        String query = "SELECT user_id, password FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    showAlert("Login Error", "User not found. Check your credentials.");
                    return;
                }

                int userId = resultSet.getInt("user_id");
                String retrievedPassword = resultSet.getString("password");

                // Validate the password
                if (retrievedPassword.equals(password)) {
                    CurrentUser.setUser(userId, username); // Set the current user details
                    changeScene(event, "Home.fxml", "Welcome!", username); // Redirect to home scene
                } else {
                    showAlert("Login Error", "Incorrect password. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to log in. Please try again later.");
        }
    }

    /**
     * Retrieves a user's preferences from the database.
     *
     * @param userId The ID of the user.
     * @return A list of preferences for the user.
     */
    public static List<String> getUserPreferences(int userId) {
        List<String> preferences = new ArrayList<>();
        String query = "SELECT preference FROM preferences WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preferences.add(resultSet.getString("preference"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preferences;
    }

    /**
     * Retrieves a user's reading history from the database.
     *
     * @param userId The ID of the user.
     * @return A list of article IDs representing the user's reading history.
     */
    public static List<String> getUserReadingHistory(int userId) {
        List<String> readingHistory = new ArrayList<>();
        String query = "SELECT article_Id FROM reading_history WHERE reading_history_user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    readingHistory.add(resultSet.getString("article_Id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readingHistory;
    }

    /**
     * Displays an alert to the user.
     *
     * @param title   The title of the alert.
     * @param message The content of the alert.
     */
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
