package com.example.mealmate.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.example.mealmate.MainActivity;
import com.example.mealmate.R;
import com.example.mealmate.models.User;
import com.example.mealmate.utils.FirebaseManager;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    TextView textview;
    private TextInputEditText confirmPasswordInput;
    private MaterialButton registerButton;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        auth = firebaseManager.getAuth();
        database = firebaseManager.getUsersRef();

        // Initialize views
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        textview = findViewById(R.id.login_button);

        // Set up click listener
        registerButton.setOnClickListener(v -> attemptRegister());

        textview.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Toast.makeText(RegisterActivity.this, "Going Back to Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void attemptRegister() {
        // Reset errors
        nameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);

        // Get values
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Validate fields
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_empty_fields));
            nameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_empty_fields));
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_empty_fields));
            passwordInput.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError(getString(R.string.error_passwords_not_match));
            confirmPasswordInput.requestFocus();
            return;
        }

        // Show progress
        registerButton.setEnabled(false);
        registerButton.setText(R.string.register + "...");

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Registration success, save user data
                    String userId = auth.getCurrentUser().getUid();
                    User user = new User(userId, email, name);
                    
                    database.child(userId).setValue(user)
                        .addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                // User data saved successfully
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Failed to save user data
                                Toast.makeText(RegisterActivity.this,
                                    getString(R.string.error_register),
                                    Toast.LENGTH_SHORT).show();
                                registerButton.setEnabled(true);
                                registerButton.setText(R.string.register);
                            }
                        });
                } else {
                    // Registration failed
                    Toast.makeText(RegisterActivity.this,
                        getString(R.string.error_register),
                        Toast.LENGTH_SHORT).show();
                    registerButton.setEnabled(true);
                    registerButton.setText(R.string.register);
                }
            });
    }
} 