package com.example.mealmate.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.example.mealmate.R;
import com.example.mealmate.utils.FirebaseManager;

public class LoginFragment extends Fragment {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseManager.getInstance().getAuth();

        // Initialize views
        emailEditText = root.findViewById(R.id.edit_text_email);
        passwordEditText = root.findViewById(R.id.edit_text_password);
        loginButton = root.findViewById(R.id.button_login);
        registerButton = root.findViewById(R.id.button_register);

        // Set click listeners
        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> navigateToRegister());

        return root;
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Navigate to main screen
                    if (getNavController() != null) {
                        getNavController().navigate(R.id.action_login_to_home);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToRegister() {
        if (getNavController() != null) {
            getNavController().navigate(R.id.action_login_to_register);
        }
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 