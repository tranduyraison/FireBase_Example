package vn.edu.ueh.thanhdnh.firebase_example;

public class Article {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String hobby;

    public Article() {
        // Default constructor required for calls to DataSnapshot.getValue(Article.class)
    }

    public Article(String id, String title, String content, String imageUrl, String hobby) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.hobby = hobby;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
