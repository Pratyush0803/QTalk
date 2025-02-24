package com.pj.qtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapterHomeScreen extends RecyclerView.Adapter<ChatAdapterHomeScreen.ChatViewHolder> {

    private List<ChatItemHomeScreen> chatList;
    private Context context;

    public ChatAdapterHomeScreen(List<ChatItemHomeScreen> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_home_screen, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItemHomeScreen chatItem = chatList.get(position);
        holder.friendName.setText(chatItem.getFriendName());
        holder.lastMessage.setText(chatItem.getLastMessage());
        // Optionally, you can set the timestamp in a user-friendly format
        holder.timestamp.setText(formatTimestamp(chatItem.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView lastMessage;
        TextView timestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }

    private String formatTimestamp(long timestamp) {
        // Format the timestamp to a readable string (e.g., "10:30 AM" or "Today")
        // Implement your own formatting logic here
        return ""; // Return formatted timestamp
    }
}
