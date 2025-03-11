package com.example.mealmate.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mealmate.R;
import com.example.mealmate.utils.FirebaseManager;

public class ProfileFragment extends Fragment {
    private TextView userNameTextView;
    private TextView emailTextView;
    private Button logoutButton;
    private Button editProfileButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        userRef = firebaseManager.getUsersRef();

        // Initialize views
        userNameTextView = root.findViewById(R.id.text_user_name);
        emailTextView = root.findViewById(R.id.text_email);
        logoutButton = root.findViewById(R.id.button_logout);
        editProfileButton = root.findViewById(R.id.button_edit_profile);

        // Set up click listeners
        logoutButton.setOnClickListener(v -> handleLogout());
        editProfileButton.setOnClickListener(v -> navigateToEditProfile());

        // Load user data
        loadUserData();

        return root;
    }

    private void loadUserData() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            emailTextView.setText(currentUser.getEmail());
            
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName = snapshot.child("name").getValue(String.class);
                    if (userName != null) {
                        userNameTextView.setText(userName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    private void handleLogout() {
        firebaseAuth.signOut();
        if (getActivity() != null) {
            getNavController().navigate(R.id.action_profile_to_login);
            getActivity().finish();
        }
    }

    private void navigateToEditProfile() {
        getNavController().navigate(R.id.action_profile_to_edit_profile);
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 