package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Button button_view_articles;
    @FXML
    private Button button_add_articles;
    @FXML
    private Button button_view_users;
    @FXML
    private Button button_back_to_login_page;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Navigate to "View Articles" page
        button_view_articles.setOnAction(event ->
                DBUtils.changeScene(event, "ViewArticles.fxml", "View Articles", null));

        // Navigate to "Add Article" page
        button_add_articles.setOnAction(event ->
                DBUtils.changeScene(event, "AddArticle.fxml", "Add Articles", null));

        // Navigate to "View Users" page
        button_view_users.setOnAction(event ->
                DBUtils.changeScene(event, "ViewUsers.fxml", "View Users", null));
 
        // Navigate back to login page
        button_back_to_login_page.setOnAction(event ->
                DBUtils.changeScene(event, "LogIn.fxml", "Log-In", null));
    }
}
