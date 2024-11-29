package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewArticlesController implements Initializable{

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
                deleteButton.setOnAction(e -> deleteArticle(title)); // Delete article by title

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
     * Deletes an article from the database by its title and refreshes the article list.
     *
     * @param title The title of the article to delete.
     */
    private void deleteArticle(String title) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM news WHERE title = ?")) {

            preparedStatement.setString(1, title);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Article deleted: " + title);
                loadArticles(); // Refresh the article list
            } else {
                System.out.println("No article found with title: " + title);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load articles when the page is initialized
        loadArticles();

        // Set up the back button
        button_back_to_admin.setOnAction(event1 ->
                DBUtils.changeScene(event1, "Admin.fxml", "Admin Dashboard", null));
    }
}
