package com.example.mealmate.ui.shopping;

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
import com.google.firebase.database.DatabaseReference;
import com.example.mealmate.R;
import com.example.mealmate.utils.FirebaseManager;

public class DelegateShoppingFragment extends Fragment {
    private EditText emailEditText;
    private Button delegateButton;
    private DatabaseReference shoppingListRef;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delegate_shopping, container, false);

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseAuth = firebaseManager.getAuth();
        shoppingListRef = firebaseManager.getShoppingListRef();

        // Initialize views
        emailEditText = root.findViewById(R.id.edit_text_email);
        delegateButton = root.findViewById(R.id.button_delegate);

        // Set click listener
        delegateButton.setOnClickListener(v -> handleDelegation());

        return root;
    }

    private void handleDelegation() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user's shopping list reference
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userShoppingListRef = shoppingListRef.child(currentUserId);

        // Create a delegation record
        DatabaseReference delegationsRef = FirebaseManager.getInstance()
                .getDatabaseReference("delegations");
        String delegationId = delegationsRef.push().getKey();

        if (delegationId != null) {
            // Create delegation object
            DelegationRecord delegation = new DelegationRecord(
                    delegationId,
                    currentUserId,
                    email,
                    System.currentTimeMillis()
            );

            // Save delegation record
            delegationsRef.child(delegationId).setValue(delegation)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Shopping list delegated successfully",
                                Toast.LENGTH_SHORT).show();
                        if (getNavController() != null) {
                            getNavController().navigateUp();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Failed to delegate shopping list: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private static class DelegationRecord {
        public String id;
        public String fromUserId;
        public String toUserEmail;
        public long timestamp;

        public DelegationRecord() {
            // Required empty constructor for Firebase
        }

        public DelegationRecord(String id, String fromUserId, String toUserEmail, long timestamp) {
            this.id = id;
            this.fromUserId = fromUserId;
            this.toUserEmail = toUserEmail;
            this.timestamp = timestamp;
        }
    }
} 