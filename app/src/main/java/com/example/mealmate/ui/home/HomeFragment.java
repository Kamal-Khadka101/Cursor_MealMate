package com.example.mealmate.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mealmate.R;
import com.example.mealmate.models.Recipe;
import com.example.mealmate.models.ShoppingList;
import com.example.mealmate.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView weeklyMealsRecycler;
    private RecyclerView shoppingListsRecycler;
    private DatabaseReference recipesRef;
    private DatabaseReference shoppingListsRef;
    private List<Recipe> weeklyMeals;
    private List<ShoppingList> shoppingLists;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase references
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        recipesRef = firebaseManager.getUserRecipesRef();
        shoppingListsRef = firebaseManager.getUserShoppingListsRef();

        // Initialize RecyclerViews
        weeklyMealsRecycler = root.findViewById(R.id.today_meals_recycler);
        shoppingListsRecycler = root.findViewById(R.id.shopping_list_recycler);

        // Set up RecyclerViews
        weeklyMealsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists
        weeklyMeals = new ArrayList<>();
        shoppingLists = new ArrayList<>();

        // Load data
        loadWeeklyMeals();
        loadShoppingLists();

        return root;
    }

    private void loadWeeklyMeals() {
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                weeklyMeals.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        weeklyMeals.add(recipe);
                    }
                }
                // Update RecyclerView adapter
                // TODO: Implement RecipeAdapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                    getString(R.string.error_loading_recipes),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadShoppingLists() {
        shoppingListsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingLists.clear();
                for (DataSnapshot listSnapshot : snapshot.getChildren()) {
                    ShoppingList list = listSnapshot.getValue(ShoppingList.class);
                    if (list != null) {
                        shoppingLists.add(list);
                    }
                }
                // Update RecyclerView adapter
                // TODO: Implement ShoppingListAdapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                    getString(R.string.error_loading_shopping_list),
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
} 