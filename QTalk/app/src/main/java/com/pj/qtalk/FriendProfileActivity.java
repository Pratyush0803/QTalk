package com.pj.qtalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;

public class FriendProfileActivity extends AppCompatActivity {

    private TextView profilePicture;
    private TextView username;
    private TextView age;
    private ImageView addFriendIcon;
    private FirebaseFirestore db;
    private ListenerRegistration friendProfileListener;
    private String userId;  // ID of the user being searched for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend);

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        profilePicture = findViewById(R.id.profile_picture);
        username = findViewById(R.id.username);
        age = findViewById(R.id.age);
        addFriendIcon = findViewById(R.id.add_friend_icon);

        // Retrieve userId passed from AddFriendActivity
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId != null) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set click listener for adding a friend
        addFriendIcon.setOnClickListener(view -> sendFriendRequest(userId));
    }

    private void loadUserProfile(String userId) {
        friendProfileListener = db.collection("users").document(userId)
                .addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading profile.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        updateUI(documentSnapshot);
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(@NonNull DocumentSnapshot documentSnapshot) {
        String name = documentSnapshot.getString("name");

        // Safely retrieve the 'age' field and handle potential type issues
        Object ageObj = documentSnapshot.get("age");
        String initial = name != null && !name.isEmpty() ? name.substring(0, 1) : "?" ;

        profilePicture.setText(initial);
        username.setText(name != null ? name : "Unknown");

        if (ageObj instanceof Number) {
            long userAge = ((Number) ageObj).longValue();
            age.setText(String.valueOf(userAge));
        } else {
            age.setText("N/A");  // Display "N/A" if age is not available or not a number
        }
    }

    private void sendFriendRequest(String userId) {
        String profileInitial = profilePicture.getText().toString();
        String name = username.getText().toString();
        String status = "pending"; // Default status

        int ageValue;
        try {
            ageValue = Integer.parseInt(age.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age value", Toast.LENGTH_SHORT).show();
            return; // Exit the method if age parsing fails
        }

        // Get the senderId from FirebaseAuth (this is the user sending the request)
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d("FriendProfileActivity", "Sending friend request from: " + senderId + " to: " + userId + " with name: " + name + " and age: " + ageValue);

        // Create a new FriendRequest instance with the sender's details
        FriendRequest friendRequest = new FriendRequest(senderId, userId, status, profileInitial, name, ageValue);

        // Add the friend request to Firestore
        db.collection("friend_requests").add(friendRequest)
                .addOnSuccessListener(documentReference -> {
                    friendRequest.setRequestId(documentReference.getId()); // Set the generated request ID
                    Toast.makeText(FriendProfileActivity.this, "Friend request sent to " + name, Toast.LENGTH_SHORT).show();

                    // Close FriendProfileActivity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("FriendProfileActivity", "Error sending friend request", e);
                    Toast.makeText(FriendProfileActivity.this, "Failed to send friend request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (friendProfileListener != null) {
            friendProfileListener.remove();
        }
    }
}
