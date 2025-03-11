package com.example.mealmate.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mealmate.R;
import com.example.mealmate.models.ShoppingItem;
import com.example.mealmate.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListFragment extends Fragment {
    private RecyclerView shoppingListRecyclerView;
    private FloatingActionButton addItemFab;
    private DatabaseReference shoppingListRef;
    private List<ShoppingItem> shoppingItems;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Initialize Firebase reference
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        shoppingListRef = firebaseManager.getShoppingListRef();

        // Initialize RecyclerView
        shoppingListRecyclerView = root.findViewById(R.id.shopping_list_recycler);
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize FAB
        addItemFab = root.findViewById(R.id.fab_add_item);
        addItemFab.setOnClickListener(v -> {
            // Navigate to add item screen
            if (getNavController() != null) {
                getNavController().navigate(R.id.action_shopping_list_to_add_item);
            }
        });

        // Initialize shopping items list
        shoppingItems = new ArrayList<>();

        // Load shopping items
        loadShoppingItems();

        return root;
    }

    private void loadShoppingItems() {
        shoppingListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ShoppingItem item = itemSnapshot.getValue(ShoppingItem.class);
                    if (item != null) {
                        shoppingItems.add(item);
                    }
                }

                // Update RecyclerView
                ShoppingListAdapter adapter = new ShoppingListAdapter(shoppingItems, item -> {
                    // Toggle item completion status
                    item.setCompleted(!item.isCompleted());
                    shoppingListRef.child(item.getId()).setValue(item);
                });
                shoppingListRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
}
