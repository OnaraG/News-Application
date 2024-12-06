package org.example.cw;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ArticleCategorizer {
    // ForkJoinPool to parallelize the article categorization process
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Onaragamage2005";

    public static void main(String[] args) {
        categorizeArticlesInDatabase();  // Start the categorization process
    }

    /**
     * Fetches uncategorized articles from the database, categorizes them in parallel,
     * and updates the database with the categorized articles.
     */
    public static void categorizeArticlesInDatabase() {
        List<News> news = fetchArticlesFromDatabase();  // Fetch articles from the database

        if (!news.isEmpty()) {
            // Use ForkJoinPool to categorize articles in parallel
            forkJoinPool.invoke(new CategorizationTask(news));
            updateArticlesInDatabase(news);  // Update the categorized articles back into the database
        } else {
            System.out.println("No articles found to categorize.");
        }
    }

    /**
     * Fetches uncategorized articles from the database where the category is null.
     *
     * @return A list of uncategorized articles.
     */
    private static List<News> fetchArticlesFromDatabase() {
        List<News> articles = new ArrayList<>();
        String query = "SELECT id, title, body FROM news WHERE category IS NULL";  // SQL query to select uncategorized articles

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Iterate through the result set and add articles to the list
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String body = resultSet.getString("body");
                articles.add(new News(id, title, body, null));  // Add each uncategorized article
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }

    /**
     * Updates the categorized articles back into the database.
     *
     * @param news A list of articles with categories assigned.
     */
    private static void updateArticlesInDatabase(List<News> news) {
        String updateQuery = "UPDATE news SET category = ? WHERE id = ?";  // SQL query to update category

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Prepare and add each article to the batch for update
            for (News newss : news) {
                preparedStatement.setString(1, newss.getCategory());
                preparedStatement.setInt(2, newss.getNewsId());
                preparedStatement.addBatch();  // Add each update to batch
            }

            preparedStatement.executeBatch();  // Execute the batch update
            System.out.println("Article categories updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Categorizes an article based on keywords found in its title and body.
     * The method checks for specific keywords to assign the appropriate category.
     *
     * @param title The title of the article.
     * @param body  The body of the article.
     * @return The category of the article.
     */
    public static String categorizeArticle(String title, String body) {
        String text = (title + " " + body).toLowerCase();  // Combine and convert text to lowercase for easier matching

        // Check for specific keywords to categorize the article
        if (text.contains("football") || text.contains("cricket") || text.contains("sports") || text.contains("players") || text.contains("playground")) {
            return "Sports";
        } else if (text.contains("AI") || text.contains("technology") || text.contains("software") || text.contains("IT") || text.contains("hardware") || text.contains("robots")) {
            return "Technology";
        } else if (text.contains("election") || text.contains("government") || text.contains("politics") || text.contains("president")) {
            return "Politics";
        } else if (text.contains("vaccine") || text.contains("medicine") || text.contains("health") || text.contains("doctors") || text.contains("pandemic") || text.contains("fever") || text.contains("patient")) {
            return "Health";
        } else if (text.contains("science") || text.contains("laboratory") || text.contains("scientist") || text.contains("rocket") || text.contains("experiment")) {
            return "Science";
        } else {
            return "General";  // Default category if no keywords match
        }
    }

    /**
     * Represents a categorization task for a list of articles, to be executed in parallel using ForkJoinPool.
     * If the list of articles is large, it splits the work to categorize articles in parallel.
     */
    static class CategorizationTask extends RecursiveAction {
        private static final int THRESHOLD = 10;  // Threshold to split the work for parallel processing
        private final List<News> articles;  // List of articles to categorize

        CategorizationTask(List<News> articles) {
            this.articles = articles;
        }

        @Override
        protected void compute() {
            if (articles.size() <= THRESHOLD) {
                // If the size is below the threshold, categorize articles sequentially
                articles.forEach(article -> {
                    String category = categorizeArticle(article.getTitle(), article.getBody());
                    article.setCategory(category);  // Set the category for each article
                });
            } else {
                // If the list is too large, split it and process both halves in parallel
                int mid = articles.size() / 2;
                invokeAll(
                        new CategorizationTask(articles.subList(0, mid)),
                        new CategorizationTask(articles.subList(mid, articles.size()))
                );
            }
        }
    }
}
