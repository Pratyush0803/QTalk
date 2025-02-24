package com.pj.qtalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class NameScreenActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText;
    private Button continueButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_screen);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elements
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();

            if (name.isEmpty() || age.isEmpty()) {
                Toast.makeText(NameScreenActivity.this, "Please enter your name and age", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save name and age in Firestore
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                saveUserData(name, age, user.getUid());
            }
        });
    }

    private void saveUserData(String name, String age, String uid) {
        // Parse age to an integer
        int ageInt;
        try {
            ageInt = Integer.parseInt(age); // Convert the age to an integer
        } catch (NumberFormatException e) {
            Toast.makeText(NameScreenActivity.this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;  // Exit if the age is not a valid number
        }

        // Create a map for the user data
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("age", ageInt);  // Store age as an integer

        // Save the data in Firestore
        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Save user login status
                    getSharedPreferences("AppPreferences", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .apply();

                    // Navigate to Home Screen
                    Intent intent = new Intent(NameScreenActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NameScreenActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
