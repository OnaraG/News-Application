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
    private VBox articleContainer;
    @FXML
    private Button button_back_to_admin;

    /**
     * Initialize the controller and load articles.
     */

    /**
     * Loads articles from the database and displays them in the articleContainer.
     */
    private void loadArticles() {
        articleContainer.getChildren().clear(); // Clear existing articles

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM news")) {

            // Iterate through the result set and create article boxes
            while (resultSet.next()) {
                int articleId = resultSet.getInt("news_id"); // Get the article ID
                String title = resultSet.getString("title");
                String body = resultSet.getString("body");

                VBox articleBox = new VBox();
                articleBox.setSpacing(5);

                Label titleLabel = new Label("Title: " + title);
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                Label bodyLabel = new Label("Body: " + body);
                bodyLabel.setWrapText(true);

                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> deleteArticle(articleId, title)); // Delete article by ID

                // Add labels and button to the article box
                articleBox.getChildren().addAll(titleLabel, bodyLabel, deleteButton);

                // Add the article box to the article container
                articleContainer.getChildren().add(articleBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
            connection.setAutoCommit(false); // Start transaction

            try {
                // Check if the article exists
                PreparedStatement checkStmt = connection.prepareStatement("SELECT COUNT(*) FROM news WHERE news_id = ?");
                checkStmt.setInt(1, newsId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Delete related records from reading_history first
                    PreparedStatement deleteHistoryStmt = connection.prepareStatement("DELETE FROM reading_history WHERE article_id = ?");
                    deleteHistoryStmt.setInt(1, newsId);
                    int historyRowsAffected = deleteHistoryStmt.executeUpdate();
                    System.out.println("Related history rows affected: " + historyRowsAffected);

                    // Now, delete the article
                    PreparedStatement deleteArticleStmt = connection.prepareStatement("DELETE FROM news WHERE news_id = ?");
                    deleteArticleStmt.setInt(1, newsId);
                    int articleRowsAffected = deleteArticleStmt.executeUpdate();
                    System.out.println("Article rows affected: " + articleRowsAffected);

                    if (articleRowsAffected > 0) {
                        connection.commit(); // Commit transaction
                        showAlert(Alert.AlertType.INFORMATION, "Article and related history deleted successfully!");
                        loadArticles(); // Refresh the list of articles
                    } else {
                        connection.rollback(); // Rollback transaction if article was not deleted
                        showAlert(Alert.AlertType.ERROR, "No article found with ID: " + newsId);
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "No article found with ID: " + newsId);
                    connection.rollback(); // Rollback transaction if article doesn't exist
                }

            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction in case of error
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error deleting article. Transaction rolled back.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error.");
        }
    }


    @Override
    public void initialize (URL url, ResourceBundle resourceBundle){
        // Load articles when the page is initialized
        loadArticles();

        // Set up the back button
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }


    private void showAlert (Alert.AlertType alertType, String s){
        Alert alert = new Alert(alertType);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}

