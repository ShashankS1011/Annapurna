package com.example.annapurna;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AnnapurnaPrefs";
    private static final String KEY_THEME = "selected_theme";

    private String currentAppliedTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Read theme before super.onCreate()
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        currentAppliedTheme = prefs.getString(KEY_THEME, "Kesar");

        // Apply theme attributes BEFORE view inflation
        applyKitchenTheme(currentAppliedTheme);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-check theme in case it was modified in Settings Activity
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String activeTheme = prefs.getString(KEY_THEME, "Kesar");

        // Force-recreate activity if theme was updated on another screen
        if (!activeTheme.equals(currentAppliedTheme)) {
            currentAppliedTheme = activeTheme;
            recreate();
        }
    }

    private void applyKitchenTheme(String theme) {
        switch (theme) {
            case "Tulsi":
                setTheme(R.style.Theme_Annapurna_Tulsi);
                break;
            case "Mitti":
                setTheme(R.style.Theme_Annapurna_Mitti);
                break;
            case "Masala":
                setTheme(R.style.Theme_Annapurna_Masala);
                break;
            case "Haldi":
                setTheme(R.style.Theme_Annapurna_Haldi);
                break;
            case "Kesar":
            default:
                setTheme(R.style.Theme_Annapurna_Kesar);
                break;
        }
    }
}