package com.pj.qtalk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendRequestAdapter extends FirestoreRecyclerAdapter<FriendRequest, FriendRequestAdapter.FriendRequestViewHolder> {

    // Listener interface for handling accept and reject actions
    public interface OnFriendRequestActionListener {
        void onAccept(String requestId, String senderId);
        void onReject(String requestId);
    }

    private final OnFriendRequestActionListener listener;

    // Constructor
    public FriendRequestAdapter(@NonNull FirestoreRecyclerOptions<FriendRequest> options, OnFriendRequestActionListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull FriendRequest model) {
        // Fetch the sender's details from Firestore using senderId
        String senderId = model.getSenderId();

        FirebaseFirestore.getInstance().collection("users").document(senderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract the sender's details
                        String senderName = documentSnapshot.getString("name");
                        String senderProfileInitial = documentSnapshot.getString("profileInitial");

                        // Try to retrieve the age field and handle it if it's not a Number
                        Object ageField = documentSnapshot.get("age");
                        if (ageField instanceof Number) {
                            long senderAge = ((Number) ageField).longValue();
                            holder.age.setText(String.valueOf(senderAge));
                        } else {
                            holder.age.setText("N/A"); // Display "N/A" if age is not available or not a number
                        }

                        // Set the extracted details to the respective views
                        holder.username.setText(senderName);
                        holder.profilePicture.setText(senderProfileInitial);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch sender's details
                    holder.username.setText("Error loading details");
                    holder.age.setText("");
                    holder.profilePicture.setText("");
                });

        // Get the requestId (document ID for friend request) for accept/reject functionality
        String requestId = getSnapshots().getSnapshot(position).getId();

        // Set up button listeners for accept and reject actions
        holder.acceptButton.setOnClickListener(v -> listener.onAccept(requestId, senderId));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(requestId));
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the friend request item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_request, parent, false);
        return new FriendRequestViewHolder(view);
    }

    // ViewHolder class for binding data
    class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        TextView username, age, profilePicture;
        ImageView acceptButton, rejectButton;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            // Initialize the views from item layout
            username = itemView.findViewById(R.id.request_user_name);
            age = itemView.findViewById(R.id.request_age_text_view);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}
