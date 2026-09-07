package com.example.annapurna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class AddRecipeActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "RECIPE_ID";

    // View References
    private ImageView btnBack, imgVideoPreview;
    private TextInputEditText editRecipeName, editChefName, editSecretTip, editOccasion, editNotes, editVideoUrl;
    private Spinner spinnerCategory;
    private TextView txtDetectedSource, txtSelectedVideoPath;
    private Button btnSelectVideo, btnSaveRecipe;
    private MaterialCardView cardVideoPicker, cardPreviewContainer;

    // Data State
    private RecipeViewModel recipeViewModel;
    private RecipeEntity existingRecipe;
    private int recipeId = -1;
    private String selectedVideoPath = "";
    private String currentVideoSource = LinkParserUtil.SOURCE_LOCAL;
    private String generatedThumbnailUrl = "";

    // Activity launcher for selecting gallery videos with persistent URI permissions
    private final ActivityResultLauncher<Intent> videoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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

                        selectedVideoPath = videoUri.toString();
                        currentVideoSource = LinkParserUtil.SOURCE_LOCAL;
                        generatedThumbnailUrl = "";

                        // Clear URL input field when local video is chosen
                        editVideoUrl.setText("");
                        txtDetectedSource.setText("Source: Local Gallery Video File");
                        txtSelectedVideoPath.setVisibility(View.VISIBLE);

                        // Show video thumbnail preview using Glide
                        cardPreviewContainer.setVisibility(View.VISIBLE);
                        Glide.with(this).load(videoUri).into(imgVideoPreview);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initViews();
        setupCategorySpinner();

        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        // Check if editing an existing recipe
        if (getIntent() != null) {
            recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
            if (recipeId == -1) {
                recipeId = getIntent().getIntExtra("EXTRA_RECIPE_ID", -1);
            }
        }

        if (recipeId != -1) {
            btnSaveRecipe.setText("Update Recipe");
            loadExistingRecipe(recipeId);
        } else {
            // Handle incoming text if shared via Android Share Sheet
            handleIncomingSharedText(getIntent());
        }

        btnBack.setOnClickListener(v -> finish());

        // Document Picker Intent for local video selection
        View.OnClickListener pickVideoListener = v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            videoPickerLauncher.launch(intent);
        };
        btnSelectVideo.setOnClickListener(pickVideoListener);
        cardVideoPicker.setOnClickListener(pickVideoListener);

        // Real-time TextWatcher for video URL input field
        editVideoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                processEnteredUrl(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSaveRecipe.setOnClickListener(v -> saveRecipe());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        editRecipeName = findViewById(R.id.editRecipeName);
        editChefName = findViewById(R.id.editChefName);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        editSecretTip = findViewById(R.id.editSecretTip);
        editOccasion = findViewById(R.id.editOccasion);
        editNotes = findViewById(R.id.editNotes);

        editVideoUrl = findViewById(R.id.editVideoUrl);
        txtDetectedSource = findViewById(R.id.txtDetectedSource);
        cardVideoPicker = findViewById(R.id.cardVideoPicker);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        txtSelectedVideoPath = findViewById(R.id.txtSelectedVideoPath);

        cardPreviewContainer = findViewById(R.id.cardPreviewContainer);
        imgVideoPreview = findViewById(R.id.imgVideoPreview);

        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
    }

    private void setupCategorySpinner() {
        String[] categories = new String[]{"Main Course", "Breakfast", "Snacks", "Dessert", "Beverage", "Side Dish", "General"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadExistingRecipe(int id) {
        recipeViewModel.getRecipeById(id).observe(this, recipe -> {
            if (recipe != null && existingRecipe == null) {
                existingRecipe = recipe;
                editRecipeName.setText(recipe.getName());
                editChefName.setText(recipe.getChef());
                editSecretTip.setText(recipe.getSecretTip());
                editOccasion.setText(recipe.getOccasion());
                editNotes.setText(recipe.getCookingNotes());

                // Set Spinner Category
                ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                if (adapter != null) {
                    int position = adapter.getPosition(recipe.getCategory());
                    if (position >= 0) {
                        spinnerCategory.setSelection(position);
                    }
                }

                // Populate Video Info
                if (recipe.getVideoPath() != null && !recipe.getVideoPath().isEmpty()) {
                    if (LinkParserUtil.SOURCE_LOCAL.equalsIgnoreCase(recipe.getVideoSource())) {
                        selectedVideoPath = recipe.getVideoPath();
                        currentVideoSource = LinkParserUtil.SOURCE_LOCAL;
                        txtDetectedSource.setText("Source: Local Gallery Video File");
                        txtSelectedVideoPath.setVisibility(View.VISIBLE);
                        cardPreviewContainer.setVisibility(View.VISIBLE);
                        Glide.with(this).load(Uri.parse(selectedVideoPath)).into(imgVideoPreview);
                    } else {
                        editVideoUrl.setText(recipe.getVideoPath());
                    }
                }
            }
        });
    }

    private void handleIncomingSharedText(Intent intent) {
        if (intent != null && Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType())) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && !sharedText.isEmpty()) {
                String extractedUrl = LinkParserUtil.extractUrl(sharedText);
                editVideoUrl.setText(extractedUrl);
            }
        }
    }

    private void processEnteredUrl(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            if (currentVideoSource.equals(LinkParserUtil.SOURCE_LOCAL) && !selectedVideoPath.isEmpty()) {
                txtDetectedSource.setText("Source: Local Gallery Video File");
            } else {
                txtDetectedSource.setText("");
                cardPreviewContainer.setVisibility(View.GONE);
                selectedVideoPath = "";
            }
            return;
        }

        txtSelectedVideoPath.setVisibility(View.GONE);

        String cleanUrl = LinkParserUtil.extractUrl(rawText);
        String detectedSource = LinkParserUtil.detectSource(cleanUrl);

        currentVideoSource = detectedSource;
        selectedVideoPath = cleanUrl;

        if (LinkParserUtil.SOURCE_YOUTUBE.equals(detectedSource)) {
            txtDetectedSource.setText("Source: YouTube Video Detected");
            String ytId = LinkParserUtil.extractYouTubeId(cleanUrl);
            generatedThumbnailUrl = LinkParserUtil.getYouTubeThumbnailUrl(ytId);

            if (generatedThumbnailUrl != null && !generatedThumbnailUrl.isEmpty()) {
                cardPreviewContainer.setVisibility(View.VISIBLE);
                Glide.with(this).load(generatedThumbnailUrl).into(imgVideoPreview);
            }

        } else if (LinkParserUtil.SOURCE_INSTAGRAM.equals(detectedSource)) {
            txtDetectedSource.setText("Source: Instagram Reel Detected");
            generatedThumbnailUrl = "";
            cardPreviewContainer.setVisibility(View.GONE);

        } else {
            txtDetectedSource.setText("Source: External Web Video");
            generatedThumbnailUrl = "";
            cardPreviewContainer.setVisibility(View.GONE);
        }
    }

    private void saveRecipe() {
        String name = editRecipeName.getText() != null ? editRecipeName.getText().toString().trim() : "";
        String chef = editChefName.getText() != null ? editChefName.getText().toString().trim() : "";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "General";
        String secretTip = editSecretTip.getText() != null ? editSecretTip.getText().toString().trim() : "";
        String occasion = editOccasion.getText() != null ? editOccasion.getText().toString().trim() : "";
        String notes = editNotes.getText() != null ? editNotes.getText().toString().trim() : "";

        if (name.isEmpty()) {
            editRecipeName.setError("Recipe title is required");
            editRecipeName.requestFocus();
            return;
        }

        if (chef.isEmpty()) {
            editChefName.setError("Chef / Author is required");
            editChefName.requestFocus();
            return;
        }

        RecipeEntity recipe = existingRecipe != null ? existingRecipe : new RecipeEntity();
        recipe.setName(name);
        recipe.setChef(chef);
        recipe.setCategory(category);
        recipe.setSecretTip(secretTip);
        recipe.setOccasion(occasion);
        recipe.setCookingNotes(notes);

        recipe.setVideoPath(selectedVideoPath);
        recipe.setVideoSource(currentVideoSource);
        recipe.setThumbnailUrl(generatedThumbnailUrl);

        if (existingRecipe != null) {
            recipeViewModel.updateRecipe(recipe);
            Toast.makeText(this, "Recipe Updated!", Toast.LENGTH_SHORT).show();
        } else {
            recipeViewModel.insert(recipe);
            Toast.makeText(this, "Recipe Saved to Vault!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}