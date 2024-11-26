package org.example.cw;

public class News {
    private int newsId;
    private String title;
    private String body;
    private String category;

    public News(int newsId, String title, String body, String category) {
        this.newsId = newsId;
        this.title = title;
        this.body = body;
        this.category = category;
    }

    public int getNewsId() { return newsId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getCategory() { return category; }
}
