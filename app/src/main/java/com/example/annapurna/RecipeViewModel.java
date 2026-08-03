package com.example.annapurna;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class RecipeViewModel extends AndroidViewModel {

    private final RecipeRepository repository;
    private final LiveData<List<RecipeEntity>> allRecipes;
    private final LiveData<List<String>> uniqueChefs;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecipeRepository(application);
        allRecipes = repository.getAllRecipes();
        uniqueChefs = repository.getUniqueChefs();
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return allRecipes;
    }

    // ADD THIS METHOD
    public LiveData<List<String>> getUniqueChefs() {
        return uniqueChefs;
    }

    public LiveData<RecipeEntity> getRecipeById(int id) {
        return repository.getRecipeById(id);
    }

    public void insertRecipe(RecipeEntity recipe) {
        repository.insertRecipe(recipe);
    }

    public void updateRecipe(RecipeEntity recipe) {
        repository.updateRecipe(recipe);
    }

    public void toggleFavorite(int id, boolean currentStatus) {
        repository.updateFavoriteStatus(id, !currentStatus);
    }
}