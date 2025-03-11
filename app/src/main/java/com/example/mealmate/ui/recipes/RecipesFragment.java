package com.example.mealmate.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mealmate.R;
import com.example.mealmate.models.Recipe;
import com.example.mealmate.utils.FirebaseManager;
import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {
    private RecyclerView recipesRecyclerView;
    private FloatingActionButton addRecipeFab;
    private DatabaseReference recipesRef;
    private List<Recipe> recipes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipe_browsing, container, false);

        // Initialize Firebase reference
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        recipesRef = firebaseManager.getUserRecipesRef();

        // Initialize RecyclerView
        recipesRecyclerView = root.findViewById(R.id.recipes_recycler_view);
        recipesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize FAB
        addRecipeFab = root.findViewById(R.id.fab_add_recipe);
        addRecipeFab.setOnClickListener(v -> {
            // Navigate to add recipe screen
            if (getNavController() != null) {
                getNavController().navigate(R.id.action_recipes_to_add_recipe);
            }
        });

        // Initialize recipes list
        recipes = new ArrayList<>();

        // Load recipes
        loadRecipes();

        return root;
    }

    private void loadRecipes() {
        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
                
                // Update RecyclerView
                RecipeAdapter adapter = new RecipeAdapter(recipes, recipe -> {
                    // Navigate to recipe detail
                    if (getNavController() != null) {
                        Bundle args = new Bundle();
                        args.putString("recipe_id", recipe.getId());
                        args.putString("recipe_name", recipe.getName());
                        getNavController().navigate(R.id.action_recipes_to_recipe_detail, args);
                    }
                });
                recipesRecyclerView.setAdapter(adapter);
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