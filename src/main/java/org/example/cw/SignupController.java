package org.example.cw;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML
    private Button button_signup;  // Button to trigger the sign-up action
    @FXML
    private Button button_log_in; // Button to navigate to the log-in screen
    @FXML
    private TextField tf_username; // TextField for user to input username
    @FXML
    private TextField tf_password; // TextField for user to input password

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Event handler for the sign-up button
        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Check if both username and password fields are not empty
                if(!tf_username.getText().trim().isEmpty() && !tf_password.getText().trim().isEmpty()){
                    // Call the DBUtils method to handle user sign-up
                    DBUtils.signUpUser(event, tf_username.getText(), tf_password.getText());
                } else {
                    // If either field is empty, show an error message
                    System.out.println("Please fill in all the fields");
                    Alert alert = new Alert(Alert.AlertType.ERROR);  // Create an error alert
                    alert.setContentText("Please fill in all information to sign up");  // Set error message content
                    alert.show();  // Show the alert
                }
            }
        });

        // Event handler for the log-in button
        button_log_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Navigate to the Log In screen
                DBUtils.changeScene(event, "LogIn.fxml", "Log In", null);
            }
        });
    }
}
