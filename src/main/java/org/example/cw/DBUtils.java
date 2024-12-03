package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Onaragamage2005";

    // Centralized method for getting a database connection
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Simplified scene-changing method
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            Parent root = loader.load();

            // If a username is provided, pass it to the controller
            if (username != null) {
                HomeController homeController = loader.getController();
                homeController.setUserInformation(username);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 782, 649));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading scene", "Unable to load the specified scene.");
        }
    }

    public static void signUpUser(ActionEvent event, String username, String password) {
        String checkQuery = "SELECT username FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement psCheckUserExists = connection.prepareStatement(checkQuery);
             PreparedStatement psInsert = connection.prepareStatement(insertQuery)) {

            psCheckUserExists.setString(1, username);

            try (ResultSet resultSet = psCheckUserExists.executeQuery()) {
                if (resultSet.next()) {
                    showAlert("Signup Error", "Username already exists. Choose a different one.");
                    return;
                }
            }

            psInsert.setString(1, username);
            psInsert.setString(2, password);
            psInsert.executeUpdate();
            changeScene(event, "Home.fxml", "Welcome!", username);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to sign up. Please try again later.");
        }
    }

    public static void logInUser(ActionEvent event, String username, String password) {
        String query = "SELECT user_id, password FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    showAlert("Login Error", "User not found. Check your credentials.");
                    return;
                }

                int userId = resultSet.getInt("user_id");
                String retrievedPassword = resultSet.getString("password");

                if (retrievedPassword.equals(password)) {
                    User user = new User(userId, username, password);
                    user.setPreferences(getUserPreferences(userId));
                    user.setReadingHistory(getUserReadingHistory(userId));
                    // After validating the user credentials in the database:

                    CurrentUser.setUser(userId, username);

// Redirect to the home page after successful login

                    changeScene(event, "Home.fxml", "Welcome!", username);
                } else {
                    showAlert("Login Error", "Incorrect password. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to log in. Please try again later.");
        }
    }

    public static List<String> getUserPreferences(int userId) {
        List<String> preferences = new ArrayList<>();
        String query = "SELECT preference FROM preferences WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    preferences.add(resultSet.getString("preference"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preferences;
    }

    public static List<String> getUserReadingHistory(int userId) {
        List<String> readingHistory = new ArrayList<>();
        String query = "SELECT article_title FROM reading_history WHERE reading_history_user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    readingHistory.add(resultSet.getString("article_title"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readingHistory;
    }

    public static void saveUserPreferences(int userId, List<String> preferences) {
        String query = "INSERT INTO preferences (user_id, preference) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String preference : preferences) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, preference);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserReadingHistory(int userId, List<String> readingHistory) {
        String query = "INSERT INTO reading_history (reading_history_user_id, article_id) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String articleId : readingHistory) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, Integer.parseInt(articleId));
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

