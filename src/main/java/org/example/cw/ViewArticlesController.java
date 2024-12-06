package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewArticlesController implements Initializable {

    @FXML
    private VBox articleContainer;  // VBox container to hold article details
    @FXML
    private Button button_back_to_admin;  // Button to go back to the admin dashboard

    /**
     * Initialize the controller and load articles when the view is displayed.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Load articles when the page is initialized
        loadArticles();

        // Set up the back button to navigate to the admin dashboard
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }

    /**
     * Loads articles from the database and displays them in the articleContainer.
     * Clears the existing articles before loading the new ones.
     */
    private void loadArticles() {
        articleContainer.getChildren().clear();  // Clear existing articles from the container

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM news")) {

            // Iterate through the result set and create article boxes for each article
            while (resultSet.next()) {
                int articleId = resultSet.getInt("news_id");  // Get the article ID
                String title = resultSet.getString("title");  // Get the article title
                String body = resultSet.getString("body");  // Get the article body

                // Create a new VBox for each article
                VBox articleBox = new VBox();
                articleBox.setSpacing(5);  // Set spacing between elements

                // Create labels for the title and body of the article
                Label titleLabel = new Label("Title: " + title);
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");  // Style the title label
                Label bodyLabel = new Label("Body: " + body);
                bodyLabel.setWrapText(true);  // Allow the body to wrap text

                // Create a delete button to remove the article
                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");  // Style the delete button
                deleteButton.setOnAction(e -> deleteArticle(articleId, title));  // Attach event handler to delete the article by ID

                // Add the title, body, and delete button to the article box
                articleBox.getChildren().addAll(titleLabel, bodyLabel, deleteButton);

                // Add the article box to the article container (the VBox in the UI)
                articleContainer.getChildren().add(articleBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Print stack trace in case of a SQL exception
        }
    }

    /**
     * Deletes an article from the database by its ID and refreshes the article list.
     *
     * @param newsId The ID of the article to delete.
     * @param title  The title of the article to delete (for debugging/logging purposes).
     */
    private void deleteArticle(int newsId, String title) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {
            connection.setAutoCommit(false);  // Start a database transaction

            try {
                // Check if the article exists in the database
                PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM news WHERE news_id = ?");
                checkStmt.setInt(1, newsId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Delete related records from the reading_history table first
                    PreparedStatement deleteHistoryStmt = connection.prepareStatement("DELETE FROM reading_history WHERE article_id = ?");
                    deleteHistoryStmt.setInt(1, newsId);
                    int historyRowsAffected = deleteHistoryStmt.executeUpdate();
                    System.out.println("Related history rows affected: " + historyRowsAffected);

                    // Now delete the article from the news table
                    PreparedStatement deleteArticleStmt = connection.prepareStatement("DELETE FROM news WHERE news_id = ?");
                    deleteArticleStmt.setInt(1, newsId);
                    int articleRowsAffected = deleteArticleStmt.executeUpdate();
                    System.out.println("Article rows affected: " + articleRowsAffected);

                    if (articleRowsAffected > 0) {
                        connection.commit();  // Commit the transaction if the article was deleted successfully
                        showAlert(Alert.AlertType.INFORMATION, "Article and related history deleted successfully!");
                        loadArticles();  // Refresh the list of articles
                    } else {
                        connection.rollback();  // Rollback the transaction if no article was deleted
                        showAlert(Alert.AlertType.ERROR, "No article found with ID: " + newsId);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "No article found with ID: " + newsId);
                    connection.rollback();  // Rollback the transaction if the article doesn't exist
                }

            } catch (SQLException e) {
                connection.rollback();  // Rollback the transaction in case of an error
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error deleting article. Transaction rolled back.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error.");
        }
    }

    /**
     * Displays an alert dialog with the given type and message.
     *
     * @param alertType The type of the alert (e.g., information, error).
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
