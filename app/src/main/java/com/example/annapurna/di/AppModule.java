package com.example.annapurna.di;

import android.content.Context;

import com.example.annapurna.AppDatabase;
import com.example.annapurna.RecipeDao;
import com.example.annapurna.data.remote.NetworkClient;
import com.example.annapurna.data.remote.RecipeApiService;

/**
 * Service locator / Dependency Injection module providing singletons
 * for Room Database, DAOs, and Retrofit API services.
 */
public class AppModule {

    private static RecipeApiService apiService;

    public static AppDatabase provideDatabase(Context context) {
        return AppDatabase.getInstance(context);
    }

    public static RecipeDao provideRecipeDao(Context context) {
        return provideDatabase(context).recipeDao();
    }

    public static synchronized RecipeApiService provideRecipeApiService() {
        if (apiService == null) {
            apiService = NetworkClient.getClient().create(RecipeApiService.class);
        }
        return apiService;
    }
}
