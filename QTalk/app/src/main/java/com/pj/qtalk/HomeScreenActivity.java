package com.pj.qtalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private RecyclerView chatListRecyclerView;
    private ChatAdapterHomeScreen chatAdapter;
    private List<ChatItemHomeScreen> chatList;
    private DatabaseReference chatDatabaseReference;
    private DatabaseReference friendsDatabaseReference;
    private ProgressBar progressBar;
    private TextView tabChats, tabFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Initialize UI elements
        chatListRecyclerView = findViewById(R.id.chatList);
        progressBar = findViewById(R.id.progressBar);
        tabChats = findViewById(R.id.tabChats);
        tabFriends = findViewById(R.id.tabFriends);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapterHomeScreen(chatList);
        chatListRecyclerView.setAdapter(chatAdapter);

        // Initialize Firebase Database references
        chatDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");
        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference("friends");

        // Load chat data on startup
        loadChatData();

        // Set up navigation button listeners
        findViewById(R.id.navSettings).setOnClickListener(v -> openSettings());
        findViewById(R.id.navAddFriend).setOnClickListener(v -> openAddFriend());

        // Set up tab click listeners
        tabChats.setOnClickListener(v -> loadChatData());
        tabFriends.setOnClickListener(v -> loadFriendData());
    }

    // Load chat data from Firebase Realtime Database
    private void loadChatData() {
        progressBar.setVisibility(View.VISIBLE);
        chatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItemHomeScreen chatItem = snapshot.getValue(ChatItemHomeScreen.class);
                    if (chatItem != null) {
                        chatList.add(chatItem);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeScreenActivity.this, "Failed to load chat data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Load friend data from Firebase Realtime Database
    private void loadFriendData() {
        progressBar.setVisibility(View.VISIBLE);
        friendsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear(); // Clear the chat list for friends
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FriendItem friendItem = snapshot.getValue(FriendItem.class);
                    if (friendItem != null && friendItem.getUserId() != null) {  // Use getUserId() instead of getFriendId()
                        // Add placeholder for lastMessage and timestamp
                        String lastMessage = "No messages yet";  // Placeholder
                        long timestamp = System.currentTimeMillis();  // Current time as placeholder

                        ChatItemHomeScreen chatItem = new ChatItemHomeScreen(
                                friendItem.getUserId(),  // Friend's unique ID
                                friendItem.getUserId(),  // Same friend ID for this example
                                friendItem.getName(),    // Friend's name
                                lastMessage,             // Placeholder for last message
                                timestamp                // Placeholder for timestamp
                        );
                        chatList.add(chatItem);
                    } else {
                        Toast.makeText(HomeScreenActivity.this, "Friend item is null or missing user ID", Toast.LENGTH_SHORT).show();
                    }
                }
                chatAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeScreenActivity.this, "Failed to load friend data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // Open the Settings screen
    private void openSettings() {
        Intent intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // Open the Add Friend screen
    private void openAddFriend() {
        Intent intent = new Intent(HomeScreenActivity.this, AddFriendActivity.class);
        startActivity(intent);
    }
}
