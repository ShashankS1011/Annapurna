package com.example.annapurna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;

public class AddRecipeActivity extends BaseActivity {

    private EditText editName, editChef, editSecretTip, editOccasion, editNotes;
    private Spinner spinnerCategory;
    private Button btnSelectVideo, btnSaveRecipe;
    private TextView txtSelectedVideoPath;
    private ImageView btnBack;

    private RecipeViewModel recipeViewModel;
    private String selectedVideoUriString = "";

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    if (videoUri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    videoUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        selectedVideoUriString = videoUri.toString();
                        if (txtSelectedVideoPath != null) {
                            txtSelectedVideoPath.setText("Video Selected");
                            txtSelectedVideoPath.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        initViews();
        setupCategorySpinner();
    }

    private void initViews() {
        editName = findViewById(R.id.editRecipeName);
        editChef = findViewById(R.id.editChefName);
        editSecretTip = findViewById(R.id.editSecretTip);
        editOccasion = findViewById(R.id.editOccasion);
        editNotes = findViewById(R.id.editNotes);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        txtSelectedVideoPath = findViewById(R.id.txtSelectedVideoPath);
        btnBack = findViewById(R.id.btnBack);

        // Inside initViews() in AddRecipeActivity.java:
        MaterialCardView cardVideoPicker = findViewById(R.id.cardVideoPicker);
        if (cardVideoPicker != null) {
            cardVideoPicker.setOnClickListener(v -> openVideoPicker());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSelectVideo != null) {
            btnSelectVideo.setOnClickListener(v -> openVideoPicker());
        }

        if (btnSaveRecipe != null) {
            btnSaveRecipe.setOnClickListener(v -> saveRecipe());
        }
    }

    private void setupCategorySpinner() {
        if (spinnerCategory != null) {
            String[] categories = new String[]{"Breakfast", "Main Course", "Snacks", "Dessert", "Beverage", "Other"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    categories
            );
            spinnerCategory.setAdapter(adapter);
        }
    }

    private void openVideoPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        videoPickerLauncher.launch(intent);
    }

    private void saveRecipe() {
        String name = editName != null ? editName.getText().toString().trim() : "";
        String chef = editChef != null ? editChef.getText().toString().trim() : "";
        String category = spinnerCategory != null ? spinnerCategory.getSelectedItem().toString() : "Other";
        String secretTip = editSecretTip != null ? editSecretTip.getText().toString().trim() : "";
        String occasion = editOccasion != null ? editOccasion.getText().toString().trim() : "";
        String notes = editNotes != null ? editNotes.getText().toString().trim() : "";

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a recipe name", Toast.LENGTH_SHORT).show();
            return;
        }

        RecipeEntity newRecipe = new RecipeEntity(name, chef, category, selectedVideoUriString);
        newRecipe.setSecretTip(secretTip);
        newRecipe.setOccasion(occasion);
        newRecipe.setCookingNotes(notes);

        recipeViewModel.insertRecipe(newRecipe);
        Toast.makeText(this, "Recipe Saved Successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}