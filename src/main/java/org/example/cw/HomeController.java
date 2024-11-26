package org.example.cw;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Button button_log_out;
    @FXML
    private Label label_home;

    @FXML
    private TextField news_title;

    @FXML
    private TextArea news_body;

    @FXML
    private Button button_like;

    @FXML
    private Button button_next_news;

    private List<News> newsList = new ArrayList<>();
    private int currentNewsIndex = 0;
    private int userId; // Retrieve user ID when the user logs in.



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button_log_out.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event, "LogIn.fxml","Log In!",null);
            }
        });

    }

    public void setUserInformation(String username){

        label_home.setText("Welcome "+username+"!");
        // Fetch the user ID based on username.
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "password")) {
            String query = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Load all news from the database.
        loadNews();
        displayCurrentNews();
    }

    private void loadNews() {
        newsList.clear(); // Clear any existing news.

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "password")) {
            String query = "SELECT * FROM news";
            try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("news_id");
                    String title = rs.getString("title");
                    String body = rs.getString("body");
                    String category = rs.getString("category");

                    newsList.add(new News(id, title, body, category));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayCurrentNews() {
        if (currentNewsIndex >= 0 && currentNewsIndex < newsList.size()) {
            News currentNews = newsList.get(currentNewsIndex);
            news_title.setText(currentNews.getTitle());
            news_body.setText(currentNews.getBody());
        } else {
            news_title.setText("No more news");
            news_body.setText("");
        }
    }

    @FXML
    private void handleLikeButton(ActionEvent event) {
        if (currentNewsIndex >= 0 && currentNewsIndex < newsList.size()) {
            News likedNews = newsList.get(currentNewsIndex);

            // Save to reading history
            saveReadingHistory(likedNews.getNewsId());

            // Add category to preferences
            addToPreferences(likedNews.getCategory());

            // Move to the next news
            currentNewsIndex++;
            displayCurrentNews();
        }
    }

    @FXML
    private void handleNextNewsButton(ActionEvent event) {
        if (currentNewsIndex >= 0 && currentNewsIndex < newsList.size()) {
            News skippedNews = newsList.get(currentNewsIndex);

            // Save to reading history as skipped
            saveReadingHistory(skippedNews.getNewsId());

            // Move to the next news
            currentNewsIndex++;
            displayCurrentNews();
        }
    }

    private void saveReadingHistory(int newsId) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "password")) {
            String query = "INSERT INTO reading_history (user_id, news_id) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.setInt(2, newsId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToPreferences(String category) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "password")) {
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




