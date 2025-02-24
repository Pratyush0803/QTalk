package com.pj.qtalk;

public interface OnFriendRequestActionListener {
    void onAccept(String requestId, String senderId);
    void onReject(String requestId);
}
