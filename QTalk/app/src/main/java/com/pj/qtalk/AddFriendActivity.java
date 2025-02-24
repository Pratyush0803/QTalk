package com.pj.qtalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AddFriendActivity extends AppCompatActivity {

    private EditText uidEditText;
    private Button searchButton;
    private RecyclerView friendRequestsRecyclerView;
    private FriendRequestAdapter friendRequestAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration friendRequestsListener;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = "AddFriendActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        // Initialize UI components
        uidEditText = findViewById(R.id.uid_input);
        searchButton = findViewById(R.id.search_button);
        friendRequestsRecyclerView = findViewById(R.id.friend_requests_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Configure RecyclerView
        friendRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load friend requests on activity start
        fetchFriendRequests();

        // Search button functionality
        searchButton.setOnClickListener(v -> {
            String uid = uidEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(uid)) {
                searchForUser(uid);
            } else {
                Toast.makeText(this, "Please enter a UID", Toast.LENGTH_SHORT).show();
            }
        });

        // Refresh functionality to reload friend requests
        swipeRefreshLayout.setOnRefreshListener(this::fetchFriendRequests);
    }

    private void fetchFriendRequests() {
        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "User is not authenticated.");
            return;
        }

        String currentUserId = auth.getCurrentUser().getUid();
        Query friendRequestsQuery = db.collection("friend_requests")
                .whereEqualTo("receiverId", currentUserId);

        if (friendRequestsListener != null) {
            friendRequestsListener.remove(); // Remove previous listener to avoid duplicate listeners
        }

        friendRequestsListener = friendRequestsQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            swipeRefreshLayout.setRefreshing(false);  // Stop the swipe refresh
            if (e != null) {
                Log.e(TAG, "Failed to load friend requests", e);
                Toast.makeText(this, "Failed to load friend requests.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                FirestoreRecyclerOptions<FriendRequest> options = new FirestoreRecyclerOptions.Builder<FriendRequest>()
                        .setQuery(friendRequestsQuery, FriendRequest.class)
                        .build();

                friendRequestAdapter = new FriendRequestAdapter(options, new FriendRequestAdapter.OnFriendRequestActionListener() {
                    @Override
                    public void onAccept(String requestId, String senderId) {
                        acceptFriendRequest(requestId, senderId);
                    }

                    @Override
                    public void onReject(String requestId) {
                        rejectFriendRequest(requestId);
                    }
                });
                friendRequestsRecyclerView.setAdapter(friendRequestAdapter);
                friendRequestAdapter.startListening(); // Start listening for changes in friend requests
                friendRequestsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                friendRequestsRecyclerView.setVisibility(View.GONE);
                Toast.makeText(this, "No friend requests found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchForUser(String uid) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Intent intent = new Intent(AddFriendActivity.this, FriendProfileActivity.class);
                intent.putExtra("userId", uid);
                startActivity(intent);
            } else {
                Toast.makeText(AddFriendActivity.this, "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user: ", e);
            Toast.makeText(AddFriendActivity.this, "Error fetching user.", Toast.LENGTH_SHORT).show();
        });
    }

    private void acceptFriendRequest(String requestId, String senderId) {
        String currentUserId = auth.getCurrentUser().getUid();

        DocumentReference requestRef = db.collection("friend_requests").document(requestId);
        requestRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addFriend(currentUserId, senderId);
            } else {
                Toast.makeText(this, "Failed to accept friend request", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error deleting friend request: ", e);
            Toast.makeText(this, "Error deleting friend request.", Toast.LENGTH_SHORT).show();
        });
    }

    private void rejectFriendRequest(String requestId) {
        DocumentReference requestRef = db.collection("friend_requests").document(requestId);
        requestRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Friend request rejected!", Toast.LENGTH_SHORT).show();
                fetchFriendRequests();
            } else {
                Toast.makeText(this, "Failed to reject friend request", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error rejecting friend request: ", e);
            Toast.makeText(this, "Error rejecting friend request.", Toast.LENGTH_SHORT).show();
        });
    }

    private void addFriend(String userId, String friendId) {
        Toast.makeText(this, "Friend request accepted!", Toast.LENGTH_SHORT).show();
        fetchFriendRequests();  // Refresh friend requests after adding friend
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (friendRequestAdapter != null) {
            friendRequestAdapter.startListening();  // Start listening to changes in the adapter
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (friendRequestAdapter != null) {
            friendRequestAdapter.stopListening();  // Stop listening to changes in the adapter
        }
        if (friendRequestsListener != null) {
            friendRequestsListener.remove();  // Remove listener to avoid memory leaks
        }
    }
}
