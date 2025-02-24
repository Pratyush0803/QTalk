package com.pj.qtalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // User is logged in, go directly to HomeScreenActivity
            startActivity(new Intent(IntroActivity.this, HomeScreenActivity.class));
            finish();
        } else {
            // User is not logged in, show the Get Started button
            Button getStartedButton = findViewById(R.id.getStartedButton);
            getStartedButton.setOnClickListener(view -> {
                startActivity(new Intent(IntroActivity.this, LoginScreenActivity.class));
                finish();
            });
        }
    }
}
