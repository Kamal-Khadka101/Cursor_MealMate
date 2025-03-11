package com.example.mealmate.ui.recipes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.mealmate.R;
import com.example.mealmate.models.Recipe;
import com.example.mealmate.utils.FirebaseManager;
import java.util.UUID;

public class AddRecipeFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView recipeImageView;
    private EditText titleEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private Button addImageButton;
    private Button saveButton;
    private Uri imageUri;
    private DatabaseReference recipesRef;
    private StorageReference storageRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Initialize Firebase
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        recipesRef = firebaseManager.getUserRecipesRef();

        // Initialize views
        recipeImageView = root.findViewById(R.id.image_recipe);
        titleEditText = root.findViewById(R.id.edit_text_title);
        ingredientsEditText = root.findViewById(R.id.edit_text_ingredients);
        instructionsEditText = root.findViewById(R.id.edit_text_instructions);
        addImageButton = root.findViewById(R.id.button_add_image);
        saveButton = root.findViewById(R.id.button_save);

        // Set click listeners
        addImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveRecipe());

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder_recipe)
                    .error(R.drawable.error_recipe)
                    .into(recipeImageView);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void saveRecipe() {
        String title = titleEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipeId = recipesRef.push().getKey();
        if (recipeId == null) {
            Toast.makeText(getContext(), "Failed to create recipe", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = FirebaseManager.getInstance().getRecipeImageRef(imageName);
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveRecipeToDatabase(recipeId, title, ingredients, instructions, downloadUri.toString());
                } else {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            saveRecipeToDatabase(recipeId, title, ingredients, instructions, null);
        }
    }

    private void saveRecipeToDatabase(String recipeId, String title, String ingredients,
                                    String instructions, String imageUrl) {
        Recipe recipe = new Recipe(recipeId, title, ingredients, instructions, imageUrl);
        recipesRef.child(recipeId).setValue(recipe)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                    if (getNavController() != null) {
                        getNavController().navigateUp();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to save recipe: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show());
    }

    private androidx.navigation.NavController getNavController() {
        return androidx.navigation.Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }
} 