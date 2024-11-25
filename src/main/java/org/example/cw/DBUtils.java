package org.example.cw;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
        Parent root = null;
        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setUserInformation(username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            try{
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));

            }catch(IOException e){
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event. getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root,600,400));
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root","Onaragamage2005");
            psCheckUserExists = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            psCheckUserExists.setString(1,username);
            resultSet = psCheckUserExists.executeQuery();

            if(resultSet.isBeforeFirst()){
                System.out.println("User already exists!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("You cannot use this username");
                alert.show();
            }else{
              psInsert = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
              psInsert.setString(1,username);
              psInsert.setString(2,password);
              psInsert.executeUpdate();

              changeScene(event, "Home.fxml", "Welcome!", username);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null){
                try{
                    resultSet.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(psCheckUserExists != null){
                try{
                    psCheckUserExists.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(psInsert != null){
                try{
                    psInsert.close();;
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try{
                    connection.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public static void logInUser(ActionEvent event, String username, String password){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root","Onaragamage2005");
            preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()){
                System.out.println("User not found in the database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Provided credentials are incorrect!");
                alert.show();
            }else{
                while(resultSet.next()){
                    String retrievePassword = resultSet.getString("password");
                    if(retrievePassword.equals(password)){
                        // Optionally retrieve preferences
                        List<String> preferences = getUserPreferences(username);
                        System.out.println("User Preferences: " + (preferences != null ? preferences : "None"));

                        // Redirect to Home screen
                        changeScene(event, "Home.fxml", "Welcome!", username);
                    }else{
                        System.out.println("Password did not match!");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("The provided credentials are incorrect!");
                        alert.show();
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(resultSet != null){
                try{
                    resultSet.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(preparedStatement != null){
                try{
                    preparedStatement.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            if(connection != null){
                try{
                   connection.close();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> getUserPreferences(String username) {
        List<String> preferences = new ArrayList<>();
        String query = "SELECT preference FROM preferences WHERE user_id = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
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

    public static List<String> getUserReadingHistory(String username) {
        List<String> readingHistory = new ArrayList<>();
        String query = "SELECT article_title FROM reading_history WHERE reading_history_user_id = (SELECT id FROM users WHERE username = ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "password");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
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

    public static void saveUserPreferences(String username, List<String> preferences) {
        String query = "INSERT INTO preferences (user_id, preference) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {

            // Get user ID
            String userIdQuery = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement userIdStmt = connection.prepareStatement(userIdQuery)) {
                userIdStmt.setString(1, username);
                try (ResultSet rs = userIdStmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");

                        // Insert preferences
                        try (PreparedStatement ps = connection.prepareStatement(query)) {
                            for (String preference : preferences) {
                                ps.setInt(1, userId);
                                ps.setString(2, preference);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserReadingHistory(String username, List<String> readingHistory) {
        String query = "INSERT INTO reading_history (user_id, article_title) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cw", "root", "Onaragamage2005")) {

            // Get user ID
            String userIdQuery = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement userIdStmt = connection.prepareStatement(userIdQuery)) {
                userIdStmt.setString(1, username);
                try (ResultSet rs = userIdStmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("id");

                        // Insert reading history
                        try (PreparedStatement ps = connection.prepareStatement(query)) {
                            for (String article : readingHistory) {
                                ps.setInt(1, userId);
                                ps.setString(2, article);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
