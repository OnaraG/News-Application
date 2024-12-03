//package org.example.cw;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.concurrent.ForkJoinPool;
//
///**
// * ArticleCategorizer is a Java program that automatically categorizes articles in the database
// * by analyzing their titles and bodies. The categorized articles are updated in the database.
// */
//public class ArticleCategorizer {
//    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
//
//
//    // Database connection details
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "Onaragamage2005";
//
//    /**
//     * Categorizes an article based on keywords found in its title and body.
//     *
//     * @param title The title of the article.
//     * @param body  The body of the article.
//     * @return The category of the article.
//     */
//    static String categorizeArticle(String title, String body) {
//        String text = (title + " " + body).toLowerCase();
//
//        if (text.contains("football") || text.contains("cricket") || text.contains("sports")||text.contains("players")||text.contains("playground")){
//            return "Sports";
//        } else if (text.contains("AI") || text.contains("technology") || text.contains("software")||text.contains("IT")||text.contains("hardware")||text.contains("robots")) {
//            return "Technology";
//        } else if (text.contains("election") || text.contains("government") || text.contains("politics")){
//            return "Politics";
//        } else if (text.contains("vaccine") || text.contains("medicine") || text.contains("health")||text.contains("doctors")||text.contains("pandemic")||text.contains("fever")||text.contains("patient")) {
//            return "Health";
//        } else {
//            return "General"; // Default category
//        }
//    }
//}

package org.example.cw;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ArticleCategorizer {
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Onaragamage2005";

    public static void main(String[] args) {
        categorizeArticlesInDatabase();
    }

    /**
     * Categorizes all uncategorized articles in the database concurrently.
     */
    public static void categorizeArticlesInDatabase() {
        List<News> news = fetchArticlesFromDatabase();

        if (!news.isEmpty()) {
            forkJoinPool.invoke(new CategorizationTask(news)); // Use ForkJoinPool for categorization
            updateArticlesInDatabase(news);
        } else {
            System.out.println("No articles found to categorize.");
        }
    }

    /**
     * Fetches uncategorized articles from the database.
     *
     * @return A list of uncategorized articles.
     */
    private static List<News> fetchArticlesFromDatabase() {
        List<News> articles = new ArrayList<>();
        String query = "SELECT id, title, body FROM news WHERE category IS NULL";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String body = resultSet.getString("body");
                articles.add(new News(id, title, body, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    /**
     * Updates the categorized articles back into the database.
     *
     * @param news A list of categorized articles.
     */
    private static void updateArticlesInDatabase(List<News> news) {
        String updateQuery = "UPDATE news SET category = ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            for (News newss : news) {
                preparedStatement.setString(1, newss.getCategory());
                preparedStatement.setInt(2, newss.getNewsId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            System.out.println("Article categories updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Categorizes an article based on keywords found in its title and body.
     *
     * @param title The title of the article.
     * @param body  The body of the article.
     * @return The category of the article.
     */
    private static String categorizeArticle(String title, String body) {
        String text = (title + " " + body).toLowerCase();

        if (text.contains("football") || text.contains("cricket") || text.contains("sports") || text.contains("players") || text.contains("playground")) {
            return "Sports";
        } else if (text.contains("AI") || text.contains("technology") || text.contains("software") || text.contains("IT") || text.contains("hardware") || text.contains("robots")) {
            return "Technology";
        } else if (text.contains("election") || text.contains("government") || text.contains("politics")) {
            return "Politics";
        } else if (text.contains("vaccine") || text.contains("medicine") || text.contains("health") || text.contains("doctors") || text.contains("pandemic") || text.contains("fever") || text.contains("patient")) {
            return "Health";
        } else {
            return "General"; // Default category
        }
    }

    /**
     * Represents a categorization task for a list of articles, to be executed in parallel.
     */
    static class CategorizationTask extends RecursiveAction {
        private static final int THRESHOLD = 10;
        private final List<News> articles;

        CategorizationTask(List<News> articles) {
            this.articles = articles;
        }

        @Override
        protected void compute() {
            if (articles.size() <= THRESHOLD) {
                articles.forEach(article -> {
                    String category = categorizeArticle(article.getTitle(), article.getBody());
                    article.setCategory(category);
                });
            } else {
                int mid = articles.size() / 2;
                invokeAll(
                        new CategorizationTask(articles.subList(0, mid)),
                        new CategorizationTask(articles.subList(mid, articles.size()))
                );
            }
        }
    }
}
