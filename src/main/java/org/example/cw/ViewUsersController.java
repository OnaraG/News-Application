package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ViewUsersController implements Initializable {

    @FXML
    private VBox userContainer;  // VBox container to display users dynamically
    @FXML
    private Button button_back_to_admin;  // Button to go back to the admin dashboard

    /**
     * Initializes the controller and loads the user list when the page is displayed.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the list of users when the page is initialized
        loadUsers();

        // Set up the back button to navigate back to the admin dashboard
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }

    /**
     * Loads the list of users from the database and displays them in the VBox.
     * Clears any existing users before adding new ones.
     */
    private void loadUsers() {
        userContainer.getChildren().clear();  // Clear existing user list from the container

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement statement = connection.prepareStatement("SELECT user_id, username FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            // Iterate through the result set and create a display for each user
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");  // Get the user ID
                String username = resultSet.getString("username");  // Get the username

                // Create a new VBox for displaying the user
                VBox userBox = new VBox();
                userBox.setSpacing(5);  // Set spacing between elements

                // Create labels for displaying the user's ID and username
                Label usernameLabel = new Label("Username: " + username);
                Label userIdLabel = new Label("User ID: " + userId);

                // Create a button to delete the user
                Button deleteButton = new Button("Delete User");
                deleteButton.setOnAction(e -> deleteUser(userId));  // Set up the delete button action

                // Add the labels and delete button to the user display VBox
                userBox.getChildren().addAll(usernameLabel, userIdLabel, deleteButton);

                // Add the user display VBox to the user container (VBox in the UI)
                userContainer.getChildren().add(userBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Print stack trace in case of a SQL exception
            showAlert(Alert.AlertType.ERROR, "Error loading user data.");  // Show error alert
        }
    }

    /**
     * Deletes a user and their associated data from the database.
     * This includes deleting records from the `reading_history` and `preferences` tables.
     *
     * @param userId The ID of the user to delete.
     */
    private void deleteUser(int userId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {

            connection.setAutoCommit(false);  // Start a database transaction

            try (
                    PreparedStatement deleteReadingHistory = connection.prepareStatement("DELETE FROM reading_history WHERE reading_history_user_id = ?");
                    PreparedStatement deletePreferences = connection.prepareStatement("DELETE FROM preferences WHERE user_id = ?");
                    PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

                // Delete the user's reading history from the `reading_history` table
                deleteReadingHistory.setInt(1, userId);
                deleteReadingHistory.executeUpdate();

                // Delete the user's preferences from the `preferences` table
                deletePreferences.setInt(1, userId);
                deletePreferences.executeUpdate();

                // Delete the user from the `users` table
                deleteUser.setInt(1, userId);
                deleteUser.executeUpdate();

                connection.commit();  // Commit the transaction if all deletions succeed
                showAlert(Alert.AlertType.INFORMATION, "User and associated logs deleted successfully!");  // Success alert
                loadUsers();  // Refresh the user list
            } catch (SQLException e) {
                connection.rollback();  // Rollback the transaction if any deletion fails
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error deleting user. Transaction rolled back.");  // Error alert
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error.");  // Database connection error alert
        }
    }

    /**
     * Utility method to display alert messages to the user.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param message   The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Notification");
        alert.setHeaderText(null);  // No header text for the alert
        alert.setContentText(message);  // Set the content text to the given message
        alert.showAndWait();  // Show the alert and wait for user interaction
    }
}
