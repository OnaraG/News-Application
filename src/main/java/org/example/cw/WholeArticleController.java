package org.example.cw;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WholeArticleController {

    @FXML
    private Label news_title;  // Label to display the article's title
    @FXML
    private Label news_content;  // Label to display the article's content
    @FXML
    private Button button_back_to_recommendations;  // Button to go back to the recommendations page

    /**
     * Initializes the controller and sets up the back button functionality.
     */
    public void initialize() {
        // Set up an action for the back button that closes the current window
        button_back_to_recommendations.setOnAction(event -> closeWindow());
    }

    /**
     * Displays the full article by setting the title and content labels.
     *
     * @param news The article object that contains the title and body.
     */
    public void displayArticle(News news) {
        // Set the text of the news_title and news_content labels from the article data
        news_title.setText(news.getTitle());
        news_content.setText(news.getBody());
    }

    /**
     * Closes the current window (stage).
     */
    private void closeWindow() {
        // Get the current stage (window) and close it
        Stage stage = (Stage) button_back_to_recommendations.getScene().getWindow();
        stage.close();
    }
}
