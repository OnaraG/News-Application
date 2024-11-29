package org.example.cw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ArticleCategorizer is a Java program that automatically categorizes articles in the database
 * by analyzing their titles and bodies. The categorized articles are updated in the database.
 */
public class ArticleCategorizer {

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cw";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Onaragamage2005";

    /**
     * Categorizes an article based on keywords found in its title and body.
     *
     * @param title The title of the article.
     * @param body  The body of the article.
     * @return The category of the article.
     */
    static String categorizeArticle(String title, String body) {
        String text = (title + " " + body).toLowerCase();

        if (text.contains("football") || text.contains("cricket") || text.contains("sports")||text.contains("players")||text.contains("playground")){
            return "Sports";
        } else if (text.contains("AI") || text.contains("technology") || text.contains("software")||text.contains("IT")||text.contains("hardware")||text.contains("robots")) {
            return "Technology";
        } else if (text.contains("election") || text.contains("government") || text.contains("politics")){
            return "Politics";
        } else if (text.contains("vaccine") || text.contains("medicine") || text.contains("health")||text.contains("doctors")||text.contains("pandemic")||text.contains("fever")||text.contains("patient")) {
            return "Health";
        } else {
            return "General"; // Default category
        }
    }
}
