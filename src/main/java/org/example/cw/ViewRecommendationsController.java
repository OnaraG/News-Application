package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewRecommendationsController implements Initializable {
    @FXML
    private VBox recommendationsContainer;
    @FXML
    private Button button_back_to_home_page;
    @FXML
    private Button button_see_more;

    private int userId;
    private int offset = 0; // Tracks how many articles are displayed
    private final int LIMIT = 6; // Number of articles per page

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_back_to_home_page.setOnAction(event ->
                DBUtils.changeScene(event, "Home.fxml", "Home Page", String.valueOf(userId)));
        button_see_more.setOnAction(this::loadRecommendations);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadRecommendations(null);
    }

    private void loadRecommendations(ActionEvent event) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {
            String query = """
                SELECT n.news_id, n.title, n.body, n.category
                FROM news n
                JOIN preferences p ON n.category = p.preference
                WHERE p.user_id = ?
                LIMIT ? OFFSET ?
                """;
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.setInt(2, LIMIT);
                ps.setInt(3, offset);

                try (ResultSet rs = ps.executeQuery()) {
                    List<News> articles = new ArrayList<>();
                    while (rs.next()) {
                        int newsId = rs.getInt("news_id");
                        String title = rs.getString("title");
                        String body = rs.getString("body");
                        String category = rs.getString("category");
                        articles.add(new News(newsId, title, body, category));
                    }

                    if (articles.isEmpty() && offset == 0) {
                        System.out.println("No recommendations available.");
                    } else if (articles.isEmpty()) {
                        System.out.println("No more articles to show.");
                    } else {
                        displayRecommendations(articles);
                        offset += articles.size();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayRecommendations(List<News> articles) {
        recommendationsContainer.getChildren().clear();

        for (News article : articles) {
            VBox articleBox = new VBox();
            Label titleLabel = new Label("Title: " + article.getTitle());
            Label categoryLabel = new Label("Category: " + article.getCategory());
            Label bodyLabel = new Label("Content: " + article.getBody());
            Button likeButton = new Button("Like");

            likeButton.setOnAction(event -> addToPreferences(article.getCategory()));

            articleBox.getChildren().addAll(titleLabel, categoryLabel, bodyLabel, likeButton);
            recommendationsContainer.getChildren().add(articleBox);
        }
    }

    private void addToPreferences(String category) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {
            String query = "INSERT INTO preferences (user_id, preference) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.setString(2, category);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
