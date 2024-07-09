package com.example.projectinsta;

public class Post {
    private String postId;
    private String userId;
    private String imageUrl;

    public Post() {
        // Default constructor required for Firebase
    }

    public Post(String postId, String userId, String imageUrl) {
        this.postId = postId;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
