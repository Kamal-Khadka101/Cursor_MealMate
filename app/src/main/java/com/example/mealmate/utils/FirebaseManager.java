package com.example.mealmate.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.mealmate.models.User;
import com.example.mealmate.models.Recipe;
import com.example.mealmate.models.ShoppingList;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final FirebaseStorage storage;

    private static final String USERS_REF = "users";
    private static final String RECIPES_REF = "recipes";
    private static final String SHOPPING_LISTS_REF = "shopping_lists";
    private static final String RECIPE_IMAGES_REF = "recipe_images";

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getUsersRef() {
        return database.getReference("users");
    }

    public DatabaseReference getUserProfileRef() {
        String userId = auth.getCurrentUser().getUid();
        return database.getReference("users").child(userId);
    }

    public DatabaseReference getRecipesRef() {
        return database.getReference("recipes");
    }

    public DatabaseReference getShoppingListRef() {
        String userId = auth.getCurrentUser().getUid();
        return database.getReference("users").child(userId).child("shopping_list");
    }

    public DatabaseReference getShoppingListsRef() {
        return database.getReference("shopping_lists");
    }

    public DatabaseReference getUserRecipesRef() {
        String userId = auth.getCurrentUser().getUid();
        return database.getReference("users").child(userId).child("recipes");
    }

    public StorageReference getRecipeImageRef(String imageName) {
        return storage.getReference("recipe_images").child(imageName);
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    public DatabaseReference getCurrentUserRef() {
        String userId = getCurrentUserId();
        return userId != null ? getUsersRef().child(userId) : null;
    }

    public DatabaseReference getUserShoppingListsRef() {
        String userId = getCurrentUserId();
        return userId != null ? getShoppingListsRef().child(userId) : null;
    }

    public DatabaseReference getDatabaseReference(String path) {
        return database.getReference(path);
    }
} 