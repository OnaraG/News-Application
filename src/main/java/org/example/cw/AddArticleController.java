package org.example.cw;

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

import static org.example.cw.ArticleCategorizer.categorizeArticle; // Import the categorization logic

/**
 * Controller class for the "Add Article" interface in the admin panel.
 * Handles user interactions and database operations related to adding articles.
 */
public class AddArticleController implements Initializable {

    // FXML bindings for the UI components
    @FXML
    private TextField text_article_title; // Text field for entering the article title

    @FXML
    private TextArea text_article_body; // Text area for entering the article body

    @FXML
    private Button button_add_article; // Button to add the article to the database

    @FXML
    private Button button_back_to_admin; // Button to navigate back to the admin dashboard

    /**
     * Handles the "Add Article" button click event.
     * Retrieves input from the user, validates it, categorizes the article,
     * and inserts the data into the database.
     */
    @FXML
    private void addArticle() {
        // Retrieve user input
        String title = text_article_title.getText();
        String body = text_article_body.getText();

        // Validate that title and body fields are not empty
        if (title.isEmpty() || body.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Fields cannot be empty!");
            return;
        }

        // Categorize the article based on its content using the ArticleCategorizer utility
        String category = categorizeArticle(title, body);

        // Establish a database connection and insert the article
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO news (title, body, category) VALUES (?, ?, ?)")) {

            // Set the parameters for the SQL INSERT statement
            preparedStatement.setString(1, title); // Set the article title
            preparedStatement.setString(2, body); // Set the article body
            preparedStatement.setString(3, category); // Set the article category

            // Execute the update to insert the article into the database
            preparedStatement.executeUpdate();

            // Show success alert and clear the input fields
            showAlert(Alert.AlertType.INFORMATION, "Article added successfully! Categorized as: " + category);
            text_article_title.clear();
            text_article_body.clear();

        } catch (SQLException e) {
            // Handle any SQL errors that occur during the database operation
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error adding article to the database.");
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
        alert.setTitle("Notification"); // Set the title of the alert window
        alert.setHeaderText(null); // No header text for the alert
        alert.setContentText(message); // Set the content of the alert message
        alert.showAndWait(); // Display the alert and wait for user acknowledgment
    }

    /**
     * Initializes the controller when the scene is loaded.
     * Sets up event handlers for UI components.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set the action for the "Back to Admin" button to navigate to the admin dashboard
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }
}
