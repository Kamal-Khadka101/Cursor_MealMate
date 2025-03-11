package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.example.mealmate.utils.FirebaseManager;
import com.example.mealmate.ui.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth
        auth = FirebaseManager.getInstance().getAuth();

        // Check if user is signed in
        if (auth.getCurrentUser() == null) {
            // Not signed in, launch the Login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up the bottom navigation
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        
        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        
        // Get the NavController from the NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            
            // Configure the top level destinations
            appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_recipes,
                R.id.navigation_shopping_list,
                R.id.navigation_profile
            ).build();

            // Set up the ActionBar with NavController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
            || super.onSupportNavigateUp();
    }
} 