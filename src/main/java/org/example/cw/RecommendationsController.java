package org.example.cw;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class RecommendationsController {

    @FXML
    private VBox recommendationsContainer;

    @FXML
    private Button button_back_to_home;

    // Initialize the controller, setting up the "Back to Home" button
    public void initialize() {
        button_back_to_home.setOnAction(event -> closeWindow());
    }

    // Display recommendations for the user asynchronously
    public void displayRecommendations() {

        // Create a background task to fetch recommendations
        Task<List<News>> fetchRecommendationsTask = new Task<>() {
            @Override
            protected List<News> call() throws Exception {
                // Fetch articles for the current user
                return RecommendationEngine.recommendArticlesForUser(CurrentUser.getId());
            }
        };

        // Handle successful fetching of recommendations
        fetchRecommendationsTask.setOnSucceeded(event -> {
            List<News> recommendedArticles = fetchRecommendationsTask.getValue();
            if (recommendedArticles == null || recommendedArticles.isEmpty()) {
                addPlaceholderMessage("No recommendations available.");
            } else {
                for (News article : recommendedArticles) {
                    addArticleToView(article);  // Add each article to the view
                }
            }
        });

        // Handle failure in fetching recommendations
        fetchRecommendationsTask.setOnFailed(event -> {
            addPlaceholderMessage("Failed to load recommendations.");
            fetchRecommendationsTask.getException().printStackTrace();  // Consider handling the exception more gracefully
        });

        // Run the task in a background thread to avoid blocking the UI
        new Thread(fetchRecommendationsTask).start();
    }

    // Add an article to the view (VBox)
    private void addArticleToView(News article) {
        HBox articleBox = new HBox(10);
        articleBox.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 5;");

        VBox detailsBox = new VBox(5);

        Label titleLabel = new Label(article.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");  // Style the title
        Label previewLabel = new Label(getFirstTwoSentences(article.getBody()));  // Show article preview
        previewLabel.setWrapText(true);  // Allow text wrapping

        detailsBox.getChildren().addAll(titleLabel, previewLabel);  // Add labels to details box
        articleBox.getChildren().add(detailsBox);  // Add details box to the article box

        recommendationsContainer.getChildren().add(articleBox);  // Add article to the recommendations container
    }

    // Display a placeholder message when no recommendations are available
    private void addPlaceholderMessage(String message) {
        Label placeholder = new Label(message);
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        recommendationsContainer.getChildren().add(placeholder);  // Add placeholder message to the container
    }

    // Extract the first two sentences from the article body for preview
    private String getFirstTwoSentences(String text) {
        String[] sentences = text.split("\\.\\s+");
        return sentences.length > 1 ? sentences[0] + ". " + sentences[1] + "." : text;
    }

    // Close the current window (stage)
    private void closeWindow() {
        Stage stage = (Stage) button_back_to_home.getScene().getWindow();
        stage.close();  // Close the current window
    }
}
