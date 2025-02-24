package com.pj.qtalk;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;  // Import this class
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private TextView profileInitial, userName, userUID, logout, copyUID, editName;
    private EditText editNameField;
    private AppCompatButton saveNameButton; // Change LinearLayout to AppCompatButton
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views by ID
        profileInitial = findViewById(R.id.profileInitial);
        userName = findViewById(R.id.userName);
        userUID = findViewById(R.id.userUID);
        logout = findViewById(R.id.logout);
        copyUID = findViewById(R.id.copyUID);
        editName = findViewById(R.id.editName);
        editNameField = findViewById(R.id.editNameField);
        saveNameButton = findViewById(R.id.saveNameButton);

        // Set user information
        setUserInfo();

        // Handle Edit Name click
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditName(true);
            }
        });

        // Handle Save Name click
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewName();
            }
        });

        // Handle Copy UID click
        copyUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUIDToClipboard();
            }
        });

        // Handle Log Out click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    // Set user info in the UI
    private void setUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userUID.setText("UID: " + uid);

            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String name = document.getString("name");
                                    if (name != null && !name.isEmpty()) {
                                        profileInitial.setText(name.substring(0, 1).toUpperCase());
                                        userName.setText(name);
                                    } else {
                                        profileInitial.setText("A"); // Default initial
                                        userName.setText("Anonymous User"); // Fallback name
                                    }
                                } else {
                                    profileInitial.setText("A");
                                    userName.setText("Anonymous User");
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to retrieve user info", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Copy UID to clipboard
    private void copyUIDToClipboard() {
        // Extract the user ID (without the "UID: " prefix)
        String uidText = userUID.getText().toString();
        String userId = uidText.replace("UID: ", "").trim();  // Remove the "UID: " part

        // Copy to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("User UID", userId);
        clipboard.setPrimaryClip(clip);

        // Confirmation message
        Toast.makeText(this, "UID copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Toggle the visibility of the Edit Name field
    private void toggleEditName(boolean isEditing) {
        if (isEditing) {
            editNameField.setVisibility(View.VISIBLE);
            saveNameButton.setVisibility(View.VISIBLE);
        } else {
            editNameField.setVisibility(View.GONE);
            saveNameButton.setVisibility(View.GONE);
        }
    }

    // Save the new name to Firestore
    private void saveNewName() {
        String newName = editNameField.getText().toString().trim();
        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid)
                    .update("name", newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                userName.setText(newName);
                                profileInitial.setText(newName.substring(0, 1).toUpperCase());
                                toggleEditName(false);  // Hide the Edit Name fields
                                Toast.makeText(SettingsActivity.this, "Name updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Log out the user
    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
