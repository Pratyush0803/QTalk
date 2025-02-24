package com.pj.qtalk;

public class FriendRequest {

    private String senderId;
    private String receiverId;
    private String status;
    private String profileInitial;
    private String name;
    private int age;  // If age is not always available, consider using Integer instead of int
    private String requestId;  // Optional field, can be null or set later

    // No-argument constructor (required for Firebase deserialization)
    public FriendRequest() {
        // Firebase needs this constructor to create instances of the object
    }

    // Constructor with parameters (for creating FriendRequest objects)
    public FriendRequest(String senderId, String receiverId, String status,
                         String profileInitial, String name, int age) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.profileInitial = profileInitial;
        this.name = name;
        this.age = age;
    }

    // Getters and setters for all fields

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileInitial() {
        return profileInitial;
    }

    public void setProfileInitial(String profileInitial) {
        this.profileInitial = profileInitial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
