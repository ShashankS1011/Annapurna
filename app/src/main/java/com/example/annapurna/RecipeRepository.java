package com.example.annapurna;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeRepository {

    private final RecipeDao recipeDao;
    private final LiveData<List<RecipeEntity>> allRecipes;
    private final LiveData<List<String>> uniqueChefs;
    private final ExecutorService executorService;

    public RecipeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        recipeDao = db.recipeDao();
        allRecipes = recipeDao.getAllRecipes();
        uniqueChefs = recipeDao.getUniqueChefs();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return allRecipes;
    }

    // ADD THIS METHOD
    public LiveData<List<String>> getUniqueChefs() {
        return uniqueChefs;
    }

    public LiveData<RecipeEntity> getRecipeById(int id) {
        return recipeDao.getRecipeById(id);
    }

    public void insertRecipe(RecipeEntity recipe) {
        executorService.execute(() -> recipeDao.insertRecipe(recipe));
    }

    public void updateRecipe(RecipeEntity recipe) {
        executorService.execute(() -> recipeDao.updateRecipe(recipe));
    }

    public void updateFavoriteStatus(int id, boolean isFavorite) {
        executorService.execute(() -> recipeDao.updateFavoriteStatus(id, isFavorite));
    }
}