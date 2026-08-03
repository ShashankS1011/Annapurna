package com.example.annapurna;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    private static final String PREFS_NAME = "AnnapurnaPrefs";
    private static final String KEY_THEME = "selected_theme";
    private static final String KEY_COOKING_MODE = "cooking_mode";

    private RadioGroup rgThemes;
    private RadioButton rbKesar, rbTulsi, rbMitti, rbMasala, rbHaldi;
    private SwitchMaterial switchCookingMode;
    private MaterialButton btnExportJson, btnImportJson, btnClearData, btnAboutUs;
    private ImageView btnBack;

    private final ActivityResultLauncher<String> createJsonLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
                if (uri != null) exportRecipesToJson(uri);
            });

    private final ActivityResultLauncher<String[]> openJsonLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) importRecipesFromJson(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        rgThemes = findViewById(R.id.rgThemes);
        rbKesar = findViewById(R.id.rbKesar);
        rbTulsi = findViewById(R.id.rbTulsi);
        rbMitti = findViewById(R.id.rbMitti);
        rbMasala = findViewById(R.id.rbMasala);
        rbHaldi = findViewById(R.id.rbHaldi);
        switchCookingMode = findViewById(R.id.switchCookingMode);
        btnExportJson = findViewById(R.id.btnExportJson);
        btnImportJson = findViewById(R.id.btnImportJson);
        btnClearData = findViewById(R.id.btnClearData);
        btnAboutUs = findViewById(R.id.btnAboutUs);

        btnBack.setOnClickListener(v -> finish());

        // Pre-select current active theme radio option
        setSelectedThemeRadio();

        // RadioGroup check listener for single theme selection
        rgThemes.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedTheme = "Kesar";

            if (checkedId == R.id.rbTulsi) selectedTheme = "Tulsi";
            else if (checkedId == R.id.rbMitti) selectedTheme = "Mitti";
            else if (checkedId == R.id.rbMasala) selectedTheme = "Masala";
            else if (checkedId == R.id.rbHaldi) selectedTheme = "Haldi";

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String currentTheme = prefs.getString(KEY_THEME, "Kesar");

            if (!currentTheme.equals(selectedTheme)) {
                prefs.edit().putString(KEY_THEME, selectedTheme).apply();
                recreate();
            }
        });

        // Cooking Mode Switcher Setup
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        switchCookingMode.setChecked(prefs.getBoolean(KEY_COOKING_MODE, true));
        switchCookingMode.setOnCheckedChangeListener((btn, isChecked) ->
                prefs.edit().putBoolean(KEY_COOKING_MODE, isChecked).apply()
        );

        // Data Management Action Listeners
        btnExportJson.setOnClickListener(v -> createJsonLauncher.launch("Annapurna_Recipes_Backup.json"));
        btnImportJson.setOnClickListener(v -> openJsonLauncher.launch(new String[]{"application/json"}));
        btnClearData.setOnClickListener(v -> showSelectiveDeleteDialog());

        // About Us Listener
        btnAboutUs.setOnClickListener(v -> showAboutUsDialog());
    }

    private void setSelectedThemeRadio() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "Kesar");

        int activeRadioId = R.id.rbKesar;
        if ("Tulsi".equals(theme)) activeRadioId = R.id.rbTulsi;
        else if ("Mitti".equals(theme)) activeRadioId = R.id.rbMitti;
        else if ("Masala".equals(theme)) activeRadioId = R.id.rbMasala;
        else if ("Haldi".equals(theme)) activeRadioId = R.id.rbHaldi;

        rgThemes.check(activeRadioId);
    }

    /**
     * Displays a custom-styled Material card dialog for About Annapurna.
     */
    private void showAboutUsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_about_us, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseAbout);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Opens a multi-choice checkbox dialog allowing selective recipe deletion.
     */
    private void showSelectiveDeleteDialog() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<RecipeEntity> recipeList = db.recipeDao().getAllRecipesList();

            if (recipeList == null || recipeList.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "No recipes found to delete.", Toast.LENGTH_SHORT).show());
                return;
            }

            String[] recipeTitles = new String[recipeList.size()];
            boolean[] checkedItems = new boolean[recipeList.size()];

            for (int i = 0; i < recipeList.size(); i++) {
                recipeTitles[i] = recipeList.get(i).getName();
                checkedItems[i] = false;
            }

            runOnUiThread(() -> {
                new AlertDialog.Builder(this)
                        .setTitle("Select Recipes to Delete")
                        .setMultiChoiceItems(recipeTitles, checkedItems, (dialog, which, isChecked) -> {
                            checkedItems[which] = isChecked;
                        })
                        .setPositiveButton("Delete Selected", (dialog, which) -> {
                            List<Integer> idsToDelete = new ArrayList<>();
                            for (int i = 0; i < checkedItems.length; i++) {
                                if (checkedItems[i]) {
                                    idsToDelete.add(recipeList.get(i).getId());
                                }
                            }
                            if (!idsToDelete.isEmpty()) {
                                deleteSelectedRecipes(idsToDelete);
                            } else {
                                Toast.makeText(this, "No recipes selected.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }).start();
    }

    private void deleteSelectedRecipes(List<Integer> recipeIds) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                db.recipeDao().deleteRecipesByIds(recipeIds);
                runOnUiThread(() -> Toast.makeText(this, recipeIds.size() + " recipe(s) deleted successfully!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to delete recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void exportRecipesToJson(Uri uri) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                List<RecipeEntity> recipes = db.recipeDao().getAllRecipesList();

                JSONArray jsonArray = new JSONArray();
                for (RecipeEntity r : recipes) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", r.getName());
                    obj.put("chef", r.getChef());
                    obj.put("category", r.getCategory());
                    obj.put("videoPath", r.getVideoPath());
                    obj.put("secretTip", r.getSecretTip());
                    obj.put("occasion", r.getOccasion());
                    obj.put("cookingNotes", r.getCookingNotes());
                    obj.put("isFavorite", r.isFavorite());
                    obj.put("videoPlaybackPosition", r.getVideoPlaybackPosition());
                    jsonArray.put(obj);
                }

                OutputStream os = getContentResolver().openOutputStream(uri);
                if (os != null) {
                    os.write(jsonArray.toString(4).getBytes());
                    os.close();
                    runOnUiThread(() -> Toast.makeText(this, "Recipes exported successfully!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void importRecipesFromJson(Uri uri) {
        new Thread(() -> {
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();

                JSONArray jsonArray = new JSONArray(sb.toString());
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    RecipeEntity r = new RecipeEntity(
                            obj.optString("name"),
                            obj.optString("chef"),
                            obj.optString("category"),
                            obj.optString("videoPath")
                    );

                    r.setSecretTip(obj.optString("secretTip"));
                    r.setOccasion(obj.optString("occasion"));
                    r.setCookingNotes(obj.optString("cookingNotes"));
                    r.setFavorite(obj.optBoolean("isFavorite", false));
                    r.setVideoPlaybackPosition(obj.optLong("videoPlaybackPosition", 0L));

                    db.recipeDao().insertRecipe(r);
                }

                runOnUiThread(() -> Toast.makeText(this, "Recipes imported successfully!", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}