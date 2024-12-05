
package org.example.cw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecommendationEngine {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Future<List<News>> recommendArticlesForUserAsync(int userId) {
        return executor.submit(() -> recommendArticlesForUser(userId));
    }

//    public static List<News> recommendArticlesForUser(int userId) {
//        System.out.println("Generating recommendations for user...");
//        List<News> recommendations = new ArrayList<>();
//        Set<News> addedArticles = new HashSet<>();  // Use a Set to avoid duplicates
//
//        try (Connection conn = DBUtils.getConnection()) {
//            // Fetch user's most read categories
//            String categoryQuery = """
//                SELECT n.category, COUNT(*) as frequency
//                FROM reading_history rh
//                JOIN news n ON rh.article_id = n.news_id
//                WHERE rh.reading_history_user_id = ?
//                GROUP BY n.category
//                ORDER BY frequency DESC
//                LIMIT 6
//            """;
//            PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
//            categoryStmt.setInt(1, CurrentUser.getId());
//            ResultSet categoryRs = categoryStmt.executeQuery();
//
//            List<String> preferredCategories = new ArrayList<>();
//            while (categoryRs.next()) {
//                preferredCategories.add(categoryRs.getString("category"));
//            }
//
//            if (!preferredCategories.isEmpty()) {
//                // Fetch articles in the preferred categories
//                String recommendationsQuery = """
//                    SELECT * FROM news
//                    WHERE category IN (?, ?, ?)
//                    AND news_id NOT IN (SELECT article_id FROM reading_history WHERE reading_history_user_id = ?)
//                    LIMIT 10
//                """;
//                PreparedStatement recommendationsStmt = conn.prepareStatement(recommendationsQuery);
//                for (int i = 0; i < preferredCategories.size(); i++) {
//                    recommendationsStmt.setString(i + 1, preferredCategories.get(i));
//                }
//                recommendationsStmt.setInt(preferredCategories.size() + 1, CurrentUser.getId());
//                ResultSet recommendationsRs = recommendationsStmt.executeQuery();
//
//                while (recommendationsRs.next()) {
//                    News article = new News(
//                            recommendationsRs.getInt("news_id"),
//                            recommendationsRs.getString("title"),
//                            recommendationsRs.getString("body"),
//                            recommendationsRs.getString("category")
//                    );
//
//                    // Avoid duplicates by checking if the article has already been added
//                    if (!addedArticles.contains(article)) {
//                        recommendations.add(article);
//                        addedArticles.add(article);  // Track added articles
//                    }
//                }
//            }
//
//            // Debugging: Print the preferred categories
//            System.out.println("Preferred categories: " + preferredCategories);
//
//            // Debugging: Print the number of recommendations and their titles
//            System.out.println("Number of recommendations: " + recommendations.size());
//            for (News a : recommendations) {
//                System.out.println("Recommended: " + a.getTitle());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return recommendations;
//    }

    public static List<News> recommendArticlesForUser(int userId) {
        System.out.println("Generating recommendations for user...");
        List<News> recommendations = new ArrayList<>();
        Set<News> addedArticles = new HashSet<>();  // Use a Set to avoid duplicates

        try (Connection conn = DBUtils.getConnection()) {
            // Fetch user's most read categories
            String categoryQuery = """
            SELECT n.category, COUNT(*) as frequency
            FROM reading_history rh
            JOIN news n ON rh.article_id = n.news_id
            WHERE rh.reading_history_user_id = ?
            GROUP BY n.category
            ORDER BY frequency DESC
            LIMIT 6
        """;
            PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
            categoryStmt.setInt(1, CurrentUser.getId());
            ResultSet categoryRs = categoryStmt.executeQuery();

            List<String> preferredCategories = new ArrayList<>();
            while (categoryRs.next()) {
                preferredCategories.add(categoryRs.getString("category"));
            }

            if (!preferredCategories.isEmpty()) {
                // Dynamically build the SQL query for the preferred categories
                StringBuilder inClause = new StringBuilder();
                for (int i = 0; i < preferredCategories.size(); i++) {
                    inClause.append("?");
                    if (i < preferredCategories.size() - 1) {
                        inClause.append(", ");
                    }
                }

                String recommendationsQuery = String.format("""
                SELECT * FROM news
                WHERE category IN (%s)
                AND news_id NOT IN (SELECT article_id FROM reading_history WHERE reading_history_user_id = ?)
                LIMIT 10
            """, inClause.toString());

                PreparedStatement recommendationsStmt = conn.prepareStatement(recommendationsQuery);

                // Set category parameters
                for (int i = 0; i < preferredCategories.size(); i++) {
                    recommendationsStmt.setString(i + 1, preferredCategories.get(i));
                }

                // Set user ID as the last parameter
                recommendationsStmt.setInt(preferredCategories.size() + 1, CurrentUser.getId());

                ResultSet recommendationsRs = recommendationsStmt.executeQuery();

                while (recommendationsRs.next()) {
                    News article = new News(
                            recommendationsRs.getInt("news_id"),
                            recommendationsRs.getString("title"),
                            recommendationsRs.getString("body"),
                            recommendationsRs.getString("category")
                    );

                    // Avoid duplicates by checking if the article has already been added
                    if (!addedArticles.contains(article)) {
                        recommendations.add(article);
                        addedArticles.add(article);  // Track added articles
                    }
                }
            }

            // Debugging: Print the preferred categories
            System.out.println("Preferred categories: " + preferredCategories);

            // Debugging: Print the number of recommendations and their titles
            System.out.println("Number of recommendations: " + recommendations.size());
            for (News a : recommendations) {
                System.out.println("Recommended: " + a.getTitle());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendations;
    }


    public void shutdown() {
        executor.shutdown();
    }
}
