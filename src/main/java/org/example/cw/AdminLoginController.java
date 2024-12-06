package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Admin Login interface.
 * Handles admin login validation and navigation to the Admin Dashboard.
 */
public class AdminLoginController implements Initializable {

    // FXML bindings for the Admin Login UI components
    @FXML
    private TextField text_admin_id; // Input field for Admin ID

    @FXML
    private TextField text_admin_name; // Input field for Admin Name

    @FXML
    private TextField text_admin_password; // Input field for Admin Password

    @FXML
    private TextField text_admin_password_confirmation; // Input field for confirming Admin Password

    @FXML
    private Button button_back_to_login_page; // Button to navigate back to the general login page

    /**
     * Validates the admin login credentials and navigates to the Admin Dashboard if valid.
     *
     * @param event The action event triggered by clicking the login button.
     */
    @FXML
    private void validateAdminLogin(ActionEvent event) {
        // Retrieve input values from the text fields
        String id = text_admin_id.getText();
        String name = text_admin_name.getText();
        String password = text_admin_password.getText();
        String confirmPassword = text_admin_password_confirmation.getText();

        // Check if the password and confirmation password match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
            return;
        }

        // Validate the admin credentials
        if (id.equals("admin1") && name.equals("admin") && password.equals("admin")) {
            loadAdminDashboard(); // Load the Admin Dashboard if credentials are valid
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Admin Credentials!"); // Show error for invalid login
        }
    }

    /**
     * Loads the Admin Dashboard scene upon successful login.
     */
    private void loadAdminDashboard() {
        try {
            // Load the Admin Dashboard FXML file
            Parent root = FXMLLoader.load(getClass().getResource("Admin.fxml"));

            // Create a new stage for the Admin Dashboard
            Stage stage = new Stage();
            stage.setTitle("Admin Dashboard"); // Set the title of the stage
            stage.setScene(new Scene(root)); // Set the scene to the loaded Admin Dashboard
            stage.show(); // Display the Admin Dashboard

            // Close the current login window
            Stage currentStage = (Stage) text_admin_id.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading Admin Dashboard!"); // Handle FXML loading error
        }
    }

    /**
     * Displays an alert dialog with the specified type and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param message   The message to display in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Admin Login"); // Set the title of the alert dialog
        alert.setContentText(message); // Set the content message
        alert.showAndWait(); // Display the alert and wait for user acknowledgment
    }

    /**
     * Initializes the controller when the scene is loaded.
     * Sets up event handlers for navigation buttons.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the action for the "Back to Login" button to navigate to the general login page
        button_back_to_login_page.setOnAction(event ->
                DBUtils.changeScene(event, "LogIn.fxml", "Log-In", null));
    }
}
