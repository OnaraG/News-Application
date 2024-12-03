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

    // Initialize the controller
    public void initialize() {
        button_back_to_home.setOnAction(event -> closeWindow());
        displayRecommendations(); // Trigger recommendations when the view loads
    }

    // Fetch and display recommended articles
//    public void displayRecommendations() {
//        List<News> recommendedArticles = getRecommendedArticles(CurrentUser.getId());
//        System.out.println("Recommended articles: " + recommendedArticles.size());
//        if (recommendedArticles == null || recommendedArticles.isEmpty()) {
//            addPlaceholderMessage("No recommendations available.");
//            return;
//        }
//        for (News article : recommendedArticles) {
//            addArticleToView(article);
//        }
//    }

    public void displayRecommendations() {
        RecommendationEngine engine = new RecommendationEngine();

        Task<List<News>> fetchRecommendationsTask = new Task<>() {
            @Override
            protected List<News> call() throws Exception {
                return engine.recommendArticlesForUser(CurrentUser.getId());
            }
        };

        fetchRecommendationsTask.setOnSucceeded(event -> {
            List<News> recommendedArticles = fetchRecommendationsTask.getValue();
            if (recommendedArticles == null || recommendedArticles.isEmpty()) {
                addPlaceholderMessage("No recommendations available.");
            } else {
                for (News article : recommendedArticles) {
                    addArticleToView(article);
                }
            }
        });

        fetchRecommendationsTask.setOnFailed(event -> {
            addPlaceholderMessage("Failed to load recommendations.");
            fetchRecommendationsTask.getException().printStackTrace();
        });

        // Run the task in a background thread
        new Thread(fetchRecommendationsTask).start();
    }



    // Fetch recommended articles using the RecommendationEngine
    private List<News> getRecommendedArticles(int userId) {
        RecommendationEngine engine = new RecommendationEngine();
        return engine.recommendArticlesForUser(userId);
    }

    // Dynamically add an article to the VBox
    private void addArticleToView(News article) {
        HBox articleBox = new HBox(10);
        articleBox.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 5;");

        VBox detailsBox = new VBox(5);

        Label titleLabel = new Label(article.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label previewLabel = new Label(getFirstTwoSentences(article.getBody()));
        previewLabel.setWrapText(true);

        detailsBox.getChildren().addAll(titleLabel, previewLabel);
        articleBox.getChildren().add(detailsBox);

        recommendationsContainer.getChildren().add(articleBox);
    }

    // Add a placeholder if no recommendations are available
    private void addPlaceholderMessage(String message) {
        Label placeholder = new Label(message);
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
        recommendationsContainer.getChildren().add(placeholder);
    }

    // Extract the first two sentences from the article body
    private String getFirstTwoSentences(String text) {
        String[] sentences = text.split("\\.\\s+");
        return sentences.length > 1 ? sentences[0] + ". " + sentences[1] + "." : text;
    }

    private void closeWindow() {
        Stage stage = (Stage) button_back_to_home.getScene().getWindow();
        stage.close();
    }
}
