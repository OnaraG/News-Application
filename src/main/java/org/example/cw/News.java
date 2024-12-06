package org.example.cw;

// A class representing a news article
public class News {
    // Unique identifier for the news article
    private int newsId;

    // Title of the news article
    private String title;

    // Content or body of the news article
    private String body;

    // Category of the news article (e.g., Sports, Politics, etc.)
    private String category;

    // Constructor to initialize the News object
    public News(int newsId, String title, String body, String category) {
        this.newsId = newsId; // Assign the news ID
        this.title = title;   // Assign the title
        this.body = body;     // Assign the body content
        this.category = category; // Assign the category
    }

    // Getter for the news ID
    public int getNewsId() {
        return newsId;
    }

    // Getter for the title
    public String getTitle() {
        return title;
    }

    // Getter for the body content
    public String getBody() {
        return body;
    }

    // Getter for the category
    public String getCategory() {
        return category;
    }

    // Setter for updating the category
    public void setCategory(String category) {
        this.category = category; // Update the category field
    }
}
