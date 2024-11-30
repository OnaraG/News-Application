package org.example.cw;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class WholeArticleController {

    @FXML
    private Label news_title;

    @FXML
    private Label news_content;

    @FXML
    private Button button_back_to_recommendations;

    public void initialize() {
        button_back_to_recommendations.setOnAction(event -> closeWindow());
    }

    public void displayArticle(News news) {
        news_title.setText(news.getTitle());
        news_content.setText(news.getBody());
    }

    private void closeWindow() {
        Stage stage = (Stage) button_back_to_recommendations.getScene().getWindow();
        stage.close();
    }
}
