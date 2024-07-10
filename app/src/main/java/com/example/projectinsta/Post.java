package com.example.projectinsta;

public class Post {
    private String postId;
    private String userId;
    private String imageUrl;
    private String username;
    private String profilePicUrl;

    public Post() {
        // Default constructor required for Firebase
    }

    public Post(String postId, String userId, String imageUrl, String username, String profilePicUrl) {
        this.postId = postId;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
