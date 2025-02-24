package com.pj.qtalk;

public class ChatItemHomeScreen {
    private String id;         // Unique ID for the chat
    private String friendId;   // ID of the friend/user in the chat
    private String friendName; // Name of the friend/user in the chat
    private String lastMessage; // Last message sent/received in the chat
    private long timestamp;     // Timestamp of the last message

    // Default constructor required for calls to DataSnapshot.getValue(ChatItem.class)
    public ChatItemHomeScreen() {
    }

    public ChatItemHomeScreen(String id, String friendId, String friendName, String lastMessage, long timestamp) {
        this.id = id;
        this.friendId = friendId;
        this.friendName = friendName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
