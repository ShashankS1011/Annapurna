package com.example.annapurna;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility helper to manage theme and appearance settings across the app.
 */
public class ThemeHelper {

    private static final String PREF_NAME = "annapurna_settings";
    public static final String KEY_APPEARANCE = "key_appearance";
    public static final String KEY_KITCHEN_THEME = "key_kitchen_theme";

    public static final int APPEARANCE_LIGHT = 0;
    public static final int APPEARANCE_DARK = 1;
    public static final int APPEARANCE_SYSTEM = 2;

    public static final String THEME_KESAR = "Kesar";
    public static final String THEME_TULSI = "Tulsi";
    public static final String THEME_MITTI = "Mitti";
    public static final String THEME_MASALA = "Masala";
    public static final String THEME_HALDI = "Haldi";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void applySavedAppearance(Context context) {
        int mode = getPrefs(context).getInt(KEY_APPEARANCE, APPEARANCE_SYSTEM);
        switch (mode) {
            case APPEARANCE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case APPEARANCE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case APPEARANCE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Applies the user's selected Kitchen Theme to an Activity.
     * MUST be called before super.onCreate() and setContentView().
     */
    public static void applyKitchenTheme(Activity activity) {
        String theme = getSavedKitchenTheme(activity);
        switch (theme) {
            case THEME_TULSI:
                activity.setTheme(R.style.Theme_Annapurna_Tulsi);
                break;
            case THEME_MITTI:
                activity.setTheme(R.style.Theme_Annapurna_Mitti);
                break;
            case THEME_MASALA:
                activity.setTheme(R.style.Theme_Annapurna_Masala);
                break;
            case THEME_HALDI:
                activity.setTheme(R.style.Theme_Annapurna_Haldi);
                break;
            case THEME_KESAR:
            default:
                activity.setTheme(R.style.Theme_Annapurna_Kesar);
                break;
        }
    }

    public static void saveAppearance(Context context, int appearanceMode) {
        getPrefs(context).edit().putInt(KEY_APPEARANCE, appearanceMode).apply();
        applySavedAppearance(context);
    }

    public static int getSavedAppearance(Context context) {
        return getPrefs(context).getInt(KEY_APPEARANCE, APPEARANCE_SYSTEM);
    }

    public static void saveKitchenTheme(Context context, String themeName) {
        getPrefs(context).edit().putString(KEY_KITCHEN_THEME, themeName).apply();
    }

    public static String getSavedKitchenTheme(Context context) {
        return getPrefs(context).getString(KEY_KITCHEN_THEME, THEME_KESAR);
    }
}