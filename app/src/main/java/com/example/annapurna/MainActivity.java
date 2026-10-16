package com.example.annapurna;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView txtGreetingHeader;
    private EditText etSearch;
    private RecyclerView rvRecipes;
    private LinearLayout layoutEmptyState;
    private MaterialButton btnEmptyAddRecipe;
    private FloatingActionButton fabAddRecipe;
    private ImageView btnSettings;

    private RecipeAdapter recipeAdapter;
    private List<RecipeEntity> fullRecipeList = new ArrayList<>();
    private String selectedCategory = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGreetingHeader = findViewById(R.id.txtGreetingHeader);
        etSearch = findViewById(R.id.etSearch);
        rvRecipes = findViewById(R.id.rvRecipes);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        btnEmptyAddRecipe = findViewById(R.id.btnEmptyAddRecipe);
        fabAddRecipe = findViewById(R.id.fabAddRecipe);
        btnSettings = findViewById(R.id.btnSettings);
        ImageView btnGrocery = findViewById(R.id.btnGrocery);

        // Setup RecyclerView
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            startActivity(intent);
        });
        rvRecipes.setAdapter(recipeAdapter);

        // Click listeners linked to AddRecipeActivity
        fabAddRecipe.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddRecipeActivity.class)));
        btnEmptyAddRecipe.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddRecipeActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        if (btnGrocery != null) {
            btnGrocery.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GroceryListActivity.class)));
        }

        ImageView btnThemeToggle = findViewById(R.id.btnThemeToggle);
        if (btnThemeToggle != null) {
            btnThemeToggle.setOnClickListener(v -> {
                int currentMode = ThemeHelper.getSavedAppearance(this);
                int nextMode;
                String modeName;
                if (currentMode == ThemeHelper.APPEARANCE_LIGHT) {
                    nextMode = ThemeHelper.APPEARANCE_DARK;
                    modeName = "Dark Mode";
                } else if (currentMode == ThemeHelper.APPEARANCE_DARK) {
                    nextMode = ThemeHelper.APPEARANCE_SYSTEM;
                    modeName = "System Theme";
                } else {
                    nextMode = ThemeHelper.APPEARANCE_LIGHT;
                    modeName = "Light Mode";
                }
                ThemeHelper.saveAppearance(this, nextMode);
                android.widget.Toast.makeText(this, "Switched to " + modeName, android.widget.Toast.LENGTH_SHORT).show();
                recreate();
            });
        }

        // Category Filter Chips Listener
        ChipGroup chipGroupCategories = findViewById(R.id.chipGroupCategories);
        if (chipGroupCategories != null) {
            chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.chipFavorites) {
                    selectedCategory = "FAVORITES";
                } else if (checkedId == R.id.chipBreakfast) {
                    selectedCategory = "Breakfast";
                } else if (checkedId == R.id.chipMainCourse) {
                    selectedCategory = "Main Course";
                } else if (checkedId == R.id.chipSweets) {
                    selectedCategory = "Sweets";
                } else if (checkedId == R.id.chipSnacks) {
                    selectedCategory = "Snacks";
                } else if (checkedId == R.id.chipBeverages) {
                    selectedCategory = "Beverages";
                } else {
                    selectedCategory = "ALL";
                }
                filterRecipes(etSearch.getText().toString());
            });
        }

        // Live Search Filter Listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreetingHeader();
        loadRecipesFromDb();
    }

    private void updateGreetingHeader() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour >= 4 && hour < 12) {
            greeting = getString(R.string.good_morning);
        } else if (hour >= 12 && hour < 17) {
            greeting = getString(R.string.good_afternoon);
        } else if (hour >= 17 && hour < 22) {
            greeting = getString(R.string.good_evening);
        } else {
            greeting = getString(R.string.good_night);
        }

        txtGreetingHeader.setText(greeting + ", Chef!");
    }

    private void loadRecipesFromDb() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            fullRecipeList = db.recipeDao().getAllRecipesList();

            runOnUiThread(() -> {
                filterRecipes(etSearch.getText().toString());
            });
        }).start();
    }

    private void filterRecipes(String query) {
        String lowerQuery = query.toLowerCase().trim();
        List<RecipeEntity> filtered = new ArrayList<>();

        for (RecipeEntity r : fullRecipeList) {
            boolean matchesCategory = true;
            if ("FAVORITES".equals(selectedCategory)) {
                matchesCategory = r.isFavorite();
            } else if (!"ALL".equals(selectedCategory)) {
                matchesCategory = r.getCategory() != null && r.getCategory().equalsIgnoreCase(selectedCategory);
            }

            boolean matchesSearch = lowerQuery.isEmpty() ||
                    (r.getName() != null && r.getName().toLowerCase().contains(lowerQuery)) ||
                    (r.getChef() != null && r.getChef().toLowerCase().contains(lowerQuery)) ||
                    (r.getCategory() != null && r.getCategory().toLowerCase().contains(lowerQuery));

            if (matchesCategory && matchesSearch) {
                filtered.add(r);
            }
        }

        recipeAdapter.submitList(filtered);
        updateRecipeListVisibility(filtered);
    }

    private void updateRecipeListVisibility(List<RecipeEntity> recipes) {
        if (recipes == null || recipes.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }
}