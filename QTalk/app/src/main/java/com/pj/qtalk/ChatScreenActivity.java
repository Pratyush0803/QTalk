package com.pj.qtalk;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ChatScreenActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageView sendButton;
    private TextView profileImage;
    private TextView profileName;
    private TextView onlineStatus;

    private ChatAdapter chatAdapter; // Assuming you have a ChatAdapter for managing chat items
    private List<ChatItem> chatList; // Change to ChatItem for the chat screen
    private DatabaseReference databaseReference; // Reference to Firebase Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        onlineStatus = findViewById(R.id.onlineStatus);

        // Set up RecyclerView
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Firebase reference (assuming "messages" is your database node)
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        // Fetch messages from Firebase
        fetchMessages();

        // Set up send button listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = messageInput.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                    messageInput.setText(""); // Clear input after sending
                }
            }
        });

        // Set up back button listener (if needed)
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });
    }

    // Method to fetch messages from Firebase
    private void fetchMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear(); // Clear the old messages
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItem chatItem = snapshot.getValue(ChatItem.class);
                    if (chatItem != null) {
                        chatList.add(chatItem);
                    }
                }
                chatAdapter.notifyDataSetChanged(); // Notify adapter about data changes
                chatRecyclerView.scrollToPosition(chatList.size() - 1); // Scroll to the last message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    // Method to send a message to Firebase
    private void sendMessage(String messageContent) {
        String timestamp = String.valueOf(System.currentTimeMillis()); // Generate a simple timestamp
        boolean isSentByUser = true; // Assume current user is sending the message

        ChatItem newChatItem = new ChatItem(messageContent, timestamp, isSentByUser);
        databaseReference.push().setValue(newChatItem); // Push new message to Firebase
    }
}
