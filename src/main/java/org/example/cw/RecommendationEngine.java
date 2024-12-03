package org.example.cw;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecommendationEngine {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Future<List<News>> recommendArticlesForUserAsync(int userId) {
        return executor.submit(() -> recommendArticlesForUser(userId));
    }

    public List<News> recommendArticlesForUser(int userId) {
        List<News> recommendations = new ArrayList<>();

        try (Connection conn = DBUtils.getConnection()) {
            // Fetch user's most read categories
            String categoryQuery = """
                SELECT n.category, COUNT(*) as frequency
                FROM reading_history rh
                JOIN news n ON rh.article_id = n.news_id
                WHERE rh.reading_history_user_id = ?
                GROUP BY n.category
                ORDER BY frequency DESC
                LIMIT 3
            """;
            PreparedStatement categoryStmt = conn.prepareStatement(categoryQuery);
            categoryStmt.setInt(1, userId);
            ResultSet categoryRs = categoryStmt.executeQuery();

            List<String> preferredCategories = new ArrayList<>();
            while (categoryRs.next()) {
                preferredCategories.add(categoryRs.getString("category"));
            }

            if (!preferredCategories.isEmpty()) {
                // Fetch articles in the preferred categories
                String recommendationsQuery = """
                    SELECT * FROM news
                    WHERE category IN (?, ?, ?)
                    AND news_id NOT IN (SELECT article_id FROM reading_history WHERE reading_history_user_id = ?)
                    LIMIT 10
                """;
                PreparedStatement recommendationsStmt = conn.prepareStatement(recommendationsQuery);
                for (int i = 0; i < preferredCategories.size(); i++) {
                    recommendationsStmt.setString(i + 1, preferredCategories.get(i));
                }
                recommendationsStmt.setInt(preferredCategories.size() + 1, userId);
                ResultSet recommendationsRs = recommendationsStmt.executeQuery();

                while (recommendationsRs.next()) {
                    recommendations.add(new News(
                            recommendationsRs.getInt("news_id"),
                            recommendationsRs.getString("title"),
                            recommendationsRs.getString("body"),
                            recommendationsRs.getString("category")
                    ));
                }
            }
            System.out.println("Preferred categories: " + preferredCategories);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendations;
    }
    public void shutdown() {
        executor.shutdown();
    }
}
