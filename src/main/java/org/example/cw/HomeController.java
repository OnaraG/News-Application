package org.example.cw;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    // FXML elements connected to the UI
    @FXML
    private VBox articleContainer; // Container for displaying articles
    @FXML
    private Button button_see_more; // Button to load more articles
    @FXML
    private Button button_log_out; // Button to log out
    @FXML
    private Label label_welcome; // Label to display the welcome message

    // Index to keep track of currently loaded articles
    private int articleIndex = 0;

    // Number of articles to load per batch
    private static final int ARTICLES_PER_BATCH = 9;

    // List to store articles fetched from the database
    private List<News> articles;

    // Called automatically during the controller's initialization
    public void initialize() {
        articles = fetchArticlesFromDatabase(); // Fetch all articles from the database
        loadNextArticles(); // Load the first batch of articles into the UI
    }

    // Handles the "See More" button click to load the next batch of articles
    @FXML
    private void handleSeeMoreButton() {
        if (articleIndex < articles.size()) {
            loadNextArticles(); // Load more articles if available
        } else {
            showAlert("No more articles", "All articles have been displayed."); // Alert if no articles remain
        }
    }

    // Loads the next batch of articles into the view
    private void loadNextArticles() {
        int endIndex = Math.min(articleIndex + ARTICLES_PER_BATCH, articles.size());
        for (int i = articleIndex; i < endIndex; i++) {
            News news = articles.get(i); // Get the next article
            addArticleToView(news); // Add the article to the UI
        }
        articleIndex = endIndex; // Update the index for the next batch
    }

    // Adds an individual article to the UI
    private void addArticleToView(News news) {
        HBox articleBox = new HBox(10); // Horizontal box to display article details
        articleBox.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 5;");

        VBox detailsBox = new VBox(15); // Vertical box for article title and preview

        Label titleLabel = new Label(news.getTitle()); // Label for the article title
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Display only the first two sentences of the article body
        Label previewLabel = new Label(getFirstTwoSentences(news.getBody()));
        previewLabel.setWrapText(true);
        previewLabel.setMaxWidth(690); // Limit the width for better layout

        Button readButton = new Button("Read"); // Button to read the full article
        readButton.setOnAction(event -> openFullArticle(news)); // Opens the full article view

        // Add the title, preview, and button to the details box
        detailsBox.getChildren().addAll(titleLabel, previewLabel, readButton);

        // Add the details box to the article container
        articleBox.getChildren().add(detailsBox);
        articleContainer.getChildren().add(articleBox);
    }

    // Retrieves the first two sentences of the article body
    private String getFirstTwoSentences(String text) {
        String[] sentences = text.split("\\.\\s+");
        return sentences.length > 1 ? sentences[0] + ". " + sentences[1] + "." : text;
    }

    // Opens the full article in a new window
    private void openFullArticle(News news) {
        addToReadingHistory(news.getNewsId()); // Track the article in reading history

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cw/WholeArticle.fxml"));
            Stage stage = new Stage(); // Create a new stage for the article
            stage.setScene(new Scene(loader.load()));

            // Pass article details to the WholeArticleController
            WholeArticleController controller = loader.getController();
            controller.displayArticle(news);

            stage.show(); // Show the new stage
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the full article view."); // Show an alert if loading fails
        }
    }

    // Adds the article to the user's reading history in the database
    private void addToReadingHistory(int articleId) {
        try (Connection conn = DBUtils.getConnection()) {
            // Check if the current user exists in the database
            String userCheckQuery = "SELECT user_id FROM users WHERE user_id = ?";
            PreparedStatement userCheckStmt = conn.prepareStatement(userCheckQuery);
            userCheckStmt.setInt(1, CurrentUser.getId());
            ResultSet userResult = userCheckStmt.executeQuery();

            if (!userResult.next()) {
                // User does not exist, log the error and stop
                System.out.println("Error: User does not exist.");
                return;
            }

            // Insert the article into the reading history
            String query = "INSERT INTO reading_history (reading_history_user_id, article_id) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, CurrentUser.getId());
            statement.setInt(2, articleId);
            statement.executeUpdate();
            System.out.println("Article ID: " + articleId + ", User ID: " + CurrentUser.getId());

        } catch (Exception e) {
            e.printStackTrace(); // Print the exception if an error occurs
        }
    }

    // Fetches all articles from the database
    private List<News> fetchArticlesFromDatabase() {
        List<News> articles = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection()) {
            String query = "SELECT * FROM news";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                // Create a News object for each article and add it to the list
                News news = new News(
                        rs.getInt("news_id"),
                        rs.getString("title"),
                        rs.getString("body"),
                        rs.getString("category")
                );
                articles.add(news);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the exception if an error occurs
        }
        return articles; // Return the list of articles
    }

    // Displays an information alert to the user
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handles the "Log Out" button click
    @FXML
    private void handleLogOutButton() {
        CurrentUser.clear(); // Clear the current user session

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Redirect to the login page
            Stage stage = (Stage) button_log_out.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert1("Error", "Unable to load the login page."); // Show an alert if loading fails
        }
    }

    // Sets the welcome message with the user's name
    public void setUserInformation(String username) {
        label_welcome.setText("Welcome, " + username + "!");
    }

    // Displays an error alert to the user
    private void showAlert1(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handles the "See Recommendations" button click
    @FXML
    private void handleSeeRecommendationsButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Recommendations.fxml"));
            Stage stage = new Stage(); // Create a new stage for recommendations
            stage.setScene(new Scene(loader.load()));

            // Pass recommendations to the RecommendationsController
            RecommendationsController controller = loader.getController();
            controller.displayRecommendations();

            stage.show(); // Show the new stage
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load recommendations view."); // Show an alert if loading fails
        }
    }
}
