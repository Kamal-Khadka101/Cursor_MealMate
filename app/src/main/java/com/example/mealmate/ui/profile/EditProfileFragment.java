package com.example.mealmate.ui.profile;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.example.mealmate.R;
import com.example.mealmate.utils.FirebaseManager;

public class EditProfileFragment extends Fragment {
    private EditText nameEditText;
    private EditText emailEditText;
    private Button saveButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = firebaseManager.getAuth();
        userRef = firebaseManager.getUserProfileRef();

        // Initialize views
        nameEditText = root.findViewById(R.id.edit_text_name);
        emailEditText = root.findViewById(R.id.edit_text_email);
        saveButton = root.findViewById(R.id.button_save);

        // Load current user data
        loadUserData();

        // Set click listener
        saveButton.setOnClickListener(v -> saveProfile());

        return root;
    }

    private void loadUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            emailEditText.setText(user.getEmail());
            userRef.child("name").get().addOnSuccessListener(snapshot -> {
                String name = snapshot.getValue(String.class);
                if (name != null) {
                    nameEditText.setText(name);
                }
            });
        }
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update display name
        userRef.child("name").setValue(name)
                .addOnSuccessListener(aVoid -> {
                    // Update email if changed
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null && !email.equals(user.getEmail())) {
                        user.updateEmail(email)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Profile updated successfully",
                                            Toast.LENGTH_SHORT).show();
                                    navigateBack();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(),
                                        "Failed to update email: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Profile updated successfully",
                                Toast.LENGTH_SHORT).show();
                        navigateBack();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to update profile: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void navigateBack() {
        if (getNavController() != null) {
            getNavController().navigateUp();
        }
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 