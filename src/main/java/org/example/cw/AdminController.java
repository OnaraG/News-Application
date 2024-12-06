package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the Admin Dashboard interface.
 * Manages navigation to different admin functionalities such as viewing articles, adding articles,
 * viewing users, and logging out.
 */
public class AdminController implements Initializable {

    // FXML bindings for the buttons in the Admin Dashboard UI
    @FXML
    private Button button_view_articles; // Button to navigate to the "View Articles" page

    @FXML
    private Button button_add_articles; // Button to navigate to the "Add Articles" page

    @FXML
    private Button button_view_users; // Button to navigate to the "View Users" page

    @FXML
    private Button button_back_to_login_page; // Button to log out and navigate to the login page

    /**
     * Initializes the controller when the scene is loaded.
     * Sets up event handlers for navigation buttons in the Admin Dashboard.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set action for the "View Articles" button to navigate to the "View Articles" page
        button_view_articles.setOnAction(event ->
                DBUtils.changeScene(event, "ViewArticles.fxml", "View Articles", null));

        // Set action for the "Add Articles" button to navigate to the "Add Articles" page
        button_add_articles.setOnAction(event ->
                DBUtils.changeScene(event, "AddArticle.fxml", "Add Articles", null));

        // Set action for the "View Users" button to navigate to the "View Users" page
        button_view_users.setOnAction(event ->
                DBUtils.changeScene(event, "ViewUsers.fxml", "View Users", null));

        // Set action for the "Back to Login" button to navigate back to the login page
        button_back_to_login_page.setOnAction(event ->
                DBUtils.changeScene(event, "LogIn.fxml", "Log-In", null));
    }
}
