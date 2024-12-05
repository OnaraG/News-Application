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

    @FXML
    private VBox articleContainer;

    @FXML
    private Button button_see_more;

    @FXML
    private Button button_log_out;
    @FXML
    private Label label_welcome;

    private int articleIndex = 0;
    private static final int ARTICLES_PER_BATCH = 9;

    private List<News> articles;

    public void initialize() {
        articles = fetchArticlesFromDatabase(); // Fetch all articles from the database
        loadNextArticles(); // Load the first batch of articles
    }

    @FXML
    private void handleSeeMoreButton() {
        if (articleIndex < articles.size()) {
            loadNextArticles();
        } else {
            showAlert("No more articles", "All articles have been displayed.");
        }
    }

    private void loadNextArticles() {
        int endIndex = Math.min(articleIndex + ARTICLES_PER_BATCH, articles.size());
        for (int i = articleIndex; i < endIndex; i++) {
            News news = articles.get(i);
            addArticleToView(news);
        }
        articleIndex = endIndex;
    }

    private void addArticleToView(News news) {
        HBox articleBox = new HBox(10);
        articleBox.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-radius: 5;");

        VBox detailsBox = new VBox(15);

        Label titleLabel = new Label(news.getTitle());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");


        // Display only the first two sentences
        Label previewLabel = new Label(getFirstTwoSentences(news.getBody()));
        previewLabel.setWrapText(true);
        previewLabel.setMaxWidth(690); // Adjust as needed to fit the window siz

        Button readButton = new Button("Read");
        readButton.setOnAction(event -> openFullArticle(news)); // Open the full article in a new window

        detailsBox.getChildren().addAll(titleLabel, previewLabel, readButton);
        articleBox.getChildren().add(detailsBox);

        articleContainer.getChildren().add(articleBox);
    }


    private String getFirstTwoSentences(String text) {
        String[] sentences = text.split("\\.\\s+");
        return sentences.length > 1 ? sentences[0] + ". " + sentences[1] + "." : text;
    }

    private void openFullArticle(News news) {
        // Track the article in reading history
        addToReadingHistory(news.getNewsId());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cw/WholeArticle.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            WholeArticleController controller = loader.getController();
            controller.displayArticle(news); // Pass the article details to the controller

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the full article view.");
        }
    }

    private void addToReadingHistory(int articleId) {
        try (Connection conn = DBUtils.getConnection()) {
            // Check if the user exists
            String userCheckQuery = "SELECT user_id FROM users WHERE user_id = ?";
            PreparedStatement userCheckStmt = conn.prepareStatement(userCheckQuery);
            userCheckStmt.setInt(1, CurrentUser.getId());
            ResultSet userResult = userCheckStmt.executeQuery();

            if (!userResult.next()) {
                // User doesn't exist, don't proceed with the insertion
                System.out.println("Error: User does not exist.");
                return;
            }

            // Proceed to add to reading history
            String query = "INSERT INTO reading_history (reading_history_user_id, article_id) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, CurrentUser.getId());
            statement.setInt(2, articleId);
            statement.executeUpdate();
            System.out.println("Article ID: " + articleId + ", User ID: " + CurrentUser.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<News> fetchArticlesFromDatabase() {
        List<News> articles = new ArrayList<>();
        try (Connection conn = DBUtils.getConnection()) {
            String query = "SELECT * FROM news";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                News news = new News(
                        rs.getInt("news_id"),
                        rs.getString("title"),
                        rs.getString("body"),
                        rs.getString("category")
                );
                articles.add(news);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogOutButton() {
        // Clear the current user session
        CurrentUser.clear();

        // Redirect to the login page
        try {
            // Load the Login.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the scene
            Stage stage = (Stage) button_log_out.getScene().getWindow();
            stage.setScene(new Scene(root));

            // Optionally, set the title for the login window
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert1("Error", "Unable to load the login page.");
        }
    }
    public void setUserInformation(String username) {
        label_welcome.setText("Welcome , " + username + "!");
    }


    private void showAlert1(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSeeRecommendationsButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Recommendations.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            RecommendationsController controller = loader.getController();
            controller.displayRecommendations(); // Pass user-specific recommendations to the controller

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load recommendations view.");
        }
    }



}
