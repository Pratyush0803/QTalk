package com.pj.qtalk;

public class ChatItem {
    private String messageContent;
    private String timestamp;
    private boolean isSentByUser;

    // Required empty constructor for Firebase
    public ChatItem() {}

    public ChatItem(String messageContent, String timestamp, boolean isSentByUser) {
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.isSentByUser = isSentByUser;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }
}
