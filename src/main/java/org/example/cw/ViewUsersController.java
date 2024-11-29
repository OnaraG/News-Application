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
    private VBox userContainer;
    @FXML
    private Button button_back_to_admin;

    /**
     * Initializes the controller and loads the user list.
     */
    /**
     * Loads the list of users and displays them dynamically in the VBox.
     */
    private void loadUsers() {
        userContainer.getChildren().clear(); // Clear existing user list

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement statement = connection.prepareStatement("SELECT user_id, username FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");

                // Create user display
                VBox userBox = new VBox();
                userBox.setSpacing(5);

                Label usernameLabel = new Label("Username: " + username);
                Label userIdLabel = new Label("User ID: " + userId);
                Button deleteButton = new Button("Delete User");

                deleteButton.setOnAction(e -> deleteUser(userId)); // Delete user by ID

                userBox.getChildren().addAll(usernameLabel, userIdLabel, deleteButton);
                userContainer.getChildren().add(userBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading user data.");
        }
    }

    /**
     * Deletes a user and their associated data from `reading_history` and `preferences` tables.
     *
     * @param userId The ID of the user to delete.
     */
    private void deleteUser(int userId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {

            connection.setAutoCommit(false); // Start transaction

            try (
                    PreparedStatement deleteReadingHistory = connection.prepareStatement("DELETE FROM reading_history WHERE reading_history_user_id = ?");
                    PreparedStatement deletePreferences = connection.prepareStatement("DELETE FROM preferences WHERE user_id = ?");
                    PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

                // Delete reading history associated with the user
                deleteReadingHistory.setInt(1, userId);
                deleteReadingHistory.executeUpdate();

                // Delete preferences associated with the user
                deletePreferences.setInt(1, userId);
                deletePreferences.executeUpdate();

                // Delete user
                deleteUser.setInt(1, userId);
                deleteUser.executeUpdate();

                connection.commit(); // Commit transaction
                showAlert(Alert.AlertType.INFORMATION, "User and associated logs deleted successfully!");
                loadUsers(); // Refresh user list
            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error deleting user. Transaction rolled back.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error.");
        }
    }

    /**
     * Utility method to display alert messages to the user.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param message   The message to display.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load articles when the page is initialized
        loadUsers();

        // Set up the back button
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }
}
