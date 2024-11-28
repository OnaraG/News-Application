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

public class AdminLoginController implements Initializable {

    @FXML
    private TextField text_admin_id;

    @FXML
    private TextField text_admin_name;

    @FXML
    private TextField text_admin_password;

    @FXML
    private TextField text_admin_password_confirmation;

    @FXML
    private Button button_back_to_login_page;

    @FXML
    private void validateAdminLogin(ActionEvent event) {
        String id = text_admin_id.getText();
        String name = text_admin_name.getText();
        String password = text_admin_password.getText();
        String confirmPassword = text_admin_password_confirmation.getText();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Passwords do not match!");
            return;
        }

        // Validate admin credentials
        if (id.equals("admin1") && name.equals("admin") && password.equals("admin")) {
            loadAdminDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Admin Credentials!");
        }
    }

    // Helper method to load the admin dashboard
    private void loadAdminDashboard() {
        try {
            // Load the Admin Dashboard FXML
            Parent root = FXMLLoader.load(getClass().getResource("Admin.fxml"));

            // Set up the new stage for Admin Dashboard
            Stage stage = new Stage();
            stage.setTitle("Admin Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current login window (optional)
            Stage currentStage = (Stage) text_admin_id.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error loading Admin Dashboard!");
        }
    }

    // Helper method to display alerts
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Admin Login");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Navigate back to login page
        button_back_to_login_page.setOnAction(event ->
                DBUtils.changeScene(event, "LogIn.fxml", "Log-In", null));
    }
}
