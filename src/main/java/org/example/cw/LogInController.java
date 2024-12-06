package org.example.cw;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LogInController implements Initializable {

    // UI components defined in the corresponding FXML file
    @FXML
    private Button button_login; // Button for logging in as a regular user
    @FXML
    private Button button_sign_in; // Button to navigate to the sign-up page
    @FXML
    private Button button_admin; // Button for accessing the admin login interface
    @FXML
    private TextField tf_username; // TextField to input the username
    @FXML
    private TextField tf_password; // TextField to input the password

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set an event handler for the login button
        button_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Call the DBUtils method to handle user login
                DBUtils.logInUser(event, tf_username.getText(), tf_password.getText());
            }
        });

        // Set an event handler for the sign-up button
        button_sign_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Navigate to the sign-up page
                DBUtils.changeScene(event, "sign-up.fxml", "Sign up", null);
            }
        });

        // Set an event handler for the admin login button
        button_admin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Navigate to the admin login page
                DBUtils.changeScene(event, "AdminLogin.fxml", "Admin Login", null);
            }
        });
    }
}
