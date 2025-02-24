package com.pj.qtalk;

public class FriendItem {
    private String userId; // User's unique identifier
    private String name;
    private String profileInitial;

    // No-argument constructor required for Firestore deserialization
    public FriendItem() {}

    // Constructor with parameters
    public FriendItem(String userId, String name, String profileInitial) {
        this.userId = userId;
        this.name = name;
        this.profileInitial = profileInitial;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getProfileInitial() {
        return profileInitial;
    }
}
