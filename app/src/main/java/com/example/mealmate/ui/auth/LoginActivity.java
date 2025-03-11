package com.example.mealmate.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.example.mealmate.MainActivity;
import com.example.mealmate.R;
import com.example.mealmate.utils.FirebaseManager;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseManager.getInstance().getAuth();

        // Initialize views
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        // Set up click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        // Reset errors
        emailInput.setError(null);
        passwordInput.setError(null);

        // Get values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Check for valid email
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_empty_fields));
            emailInput.requestFocus();
            return;
        }

        // Check for valid password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_empty_fields));
            passwordInput.requestFocus();
            return;
        }

        // Show progress
        loginButton.setEnabled(false);
        loginButton.setText(R.string.login + "...");

        // Attempt login with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Login success
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Login failed
                    Toast.makeText(LoginActivity.this,
                        getString(R.string.error_login),
                        Toast.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                    loginButton.setText(R.string.login);
                }
            });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
//    }
} 