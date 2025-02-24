package com.pj.qtalk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;

    public ChatAdapter(List<ChatItem> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the chat_item layout for each message
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chatItem = chatList.get(position);
        holder.messageTextView.setText(chatItem.getMessageContent());
        holder.messageTime.setText(chatItem.getTimestamp());

        // Set the background drawable based on whether the message was sent by the user
        if (chatItem.isSentByUser()) {
            holder.messageTextView.setBackgroundResource(R.drawable.bubble_background_user); // User's message background
            // Align user's message to the right (optional)
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            // Optionally set the time visibility
            holder.messageTime.setVisibility(View.VISIBLE);
        } else {
            holder.messageTextView.setBackgroundResource(R.drawable.bubble_background_friend); // Friend's message background
            // Align friend's message to the left (optional)
            holder.messageTextView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            // Optionally set the time visibility
            holder.messageTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextViews for message content and timestamp
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}
