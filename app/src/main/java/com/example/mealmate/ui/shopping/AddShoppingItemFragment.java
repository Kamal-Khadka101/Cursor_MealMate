package com.example.mealmate.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.example.mealmate.R;
import com.example.mealmate.models.ShoppingItem;
import com.example.mealmate.utils.FirebaseManager;

public class AddShoppingItemFragment extends Fragment {
    private EditText nameEditText;
    private EditText quantityEditText;
    private Spinner unitSpinner;
    private Button addButton;
    private DatabaseReference shoppingListRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_shopping_item, container, false);

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        shoppingListRef = firebaseManager.getShoppingListRef();

        // Initialize views
        nameEditText = root.findViewById(R.id.edit_text_name);
        quantityEditText = root.findViewById(R.id.edit_text_quantity);
        unitSpinner = root.findViewById(R.id.spinner_unit);
        addButton = root.findViewById(R.id.button_add);

        // Set up unit spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        // Set click listener
        addButton.setOnClickListener(v -> addShoppingItem());

        return root;
    }

    private void addShoppingItem() {
        String name = nameEditText.getText().toString().trim();
        String quantityStr = quantityEditText.getText().toString().trim();
        String unit = unitSpinner.getSelectedItem().toString();

        if (name.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the item
        String itemId = shoppingListRef.push().getKey();
        if (itemId == null) {
            Toast.makeText(getContext(), "Failed to create item", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and save the shopping item
        ShoppingItem item = new ShoppingItem(itemId, name, quantity, unit);
        shoppingListRef.child(itemId).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    if (getNavController() != null) {
                        getNavController().navigateUp();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to add item: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 