package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddArticleController implements Initializable{

    @FXML
    private TextField text_article_title;

    @FXML
    private TextArea text_article_body;

    @FXML
    private Button button_add_article;

    @FXML
    private Button button_back_to_admin;
    /**
     * Method to handle the addition of a new article to the database.
     */
    @FXML
    private void addArticle() {
        String title = text_article_title.getText();
        String body = text_article_body.getText();

        // Validate that fields are not empty
        if (title.isEmpty() || body.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Fields cannot be empty!");
            return;
        }

        // Insert article into the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO news (title, body) VALUES (?, ?)")) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, body);
            preparedStatement.executeUpdate();

            // Success alert and clear input fields
            showAlert(Alert.AlertType.INFORMATION, "Article added successfully!");
            text_article_title.clear();
            text_article_body.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error adding article to the database.");
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
    public void initialize(URL location, ResourceBundle resources) {
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));

    }
}
