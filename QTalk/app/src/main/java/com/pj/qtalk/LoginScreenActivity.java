package com.pj.qtalk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginScreenActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText phoneNoEditText, otpEditText;
    private Button otpButton, loginButton;
    private ImageView googleLoginButton, backIcon;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI Elements
        phoneNoEditText = findViewById(R.id.phoneNo);
        otpEditText = findViewById(R.id.otpText);
        otpButton = findViewById(R.id.otpButton);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleIcon);
        backIcon = findViewById(R.id.backIcon);

        // Back icon to navigate to Intro page
        backIcon.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        });

        // Request OTP with country code
        otpButton.setOnClickListener(v -> {
            String phoneNo = phoneNoEditText.getText().toString().trim();
            if (phoneNo.isEmpty()) {
                Toast.makeText(LoginScreenActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            sendVerificationCode("+91" + phoneNo);
        });

        // Verify OTP
        loginButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(LoginScreenActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(otp);
        });

        // Google Sign-In Button Click Listener
        googleLoginButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void sendVerificationCode(String phoneNo) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNo)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginScreenActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        LoginScreenActivity.this.verificationId = verificationId;
                        Toast.makeText(LoginScreenActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginScreenActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                navigateToNameScreenAfterPhoneLogin();
            } else {
                Toast.makeText(LoginScreenActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                            navigateToNameScreenAfterGoogleLogin();
                        } else {
                            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToNameScreenAfterPhoneLogin() {
        Intent intent = new Intent(LoginScreenActivity.this, NameScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToNameScreenAfterGoogleLogin() {
        Intent intent = new Intent(LoginScreenActivity.this, NameScreenActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}
