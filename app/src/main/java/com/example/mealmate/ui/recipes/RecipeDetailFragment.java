package com.example.mealmate.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.mealmate.R;
import com.example.mealmate.models.Recipe;
import com.example.mealmate.utils.FirebaseManager;

public class RecipeDetailFragment extends Fragment {
    private ImageView recipeImageView;
    private TextView titleTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private DatabaseReference recipeRef;
    private String recipeId;
    private Recipe currentRecipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Get recipe ID from arguments
        if (getArguments() != null) {
            recipeId = getArguments().getString("recipe_id");
        }

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        recipeRef = firebaseManager.getUserRecipesRef().child(recipeId);

        // Initialize views
        recipeImageView = root.findViewById(R.id.image_recipe);
        titleTextView = root.findViewById(R.id.text_title);
        ingredientsTextView = root.findViewById(R.id.text_ingredients);
        instructionsTextView = root.findViewById(R.id.text_instructions);

        // Load recipe data
        loadRecipe();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            navigateToEdit();
            return true;
        } else if (id == R.id.action_delete) {
            deleteRecipe();
            return true;
        } else if (id == R.id.action_share) {
            shareRecipe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRecipe() {
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentRecipe = snapshot.getValue(Recipe.class);
                if (currentRecipe != null) {
                    updateUI(currentRecipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load recipe: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Recipe recipe) {
        titleTextView.setText(recipe.getName());
        ingredientsTextView.setText(recipe.getIngredients());
        instructionsTextView.setText(recipe.getInstructions());

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.error_recipe)
                    .into(recipeImageView);
        }
    }

    private void navigateToEdit() {
        if (getNavController() != null && currentRecipe != null) {
            Bundle args = new Bundle();
            args.putString("recipe_id", currentRecipe.getId());
            getNavController().navigate(R.id.action_recipe_detail_to_edit_recipe, args);
        }
    }

    private void deleteRecipe() {
        recipeRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
                    if (getNavController() != null) {
                        getNavController().navigateUp();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to delete recipe: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private void shareRecipe() {
        if (currentRecipe != null) {
            String shareText = String.format("Check out this recipe!\n\n%s\n\nIngredients:\n%s\n\nInstructions:\n%s",
                    currentRecipe.getName(),
                    currentRecipe.getIngredients(),
                    currentRecipe.getInstructions());

            android.content.Intent shareIntent = new android.content.Intent();
            shareIntent.setAction(android.content.Intent.ACTION_SEND);
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            startActivity(android.content.Intent.createChooser(shareIntent, "Share recipe"));
        }
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 