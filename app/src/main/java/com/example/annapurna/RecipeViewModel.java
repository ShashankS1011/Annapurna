package com.example.annapurna;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {

    private final RecipeRepository repository;
    private final LiveData<List<RecipeEntity>> allRecipes;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecipeRepository(application);
        allRecipes = repository.getAllRecipes();
    }

    public LiveData<List<RecipeEntity>> getAllRecipes() {
        return allRecipes;
    }

    public LiveData<RecipeEntity> getRecipeById(int id) {
        return repository.getRecipeById(id);
    }

    public void insert(RecipeEntity recipe) {
        repository.insert(recipe);
    }

    public void updateRecipe(RecipeEntity recipe) {
        repository.update(recipe);
    }

    public void deleteRecipe(RecipeEntity recipe) {
        repository.delete(recipe);
    }

    public void toggleFavorite(int recipeId, boolean currentFavoriteStatus) {
        repository.updateFavoriteStatus(recipeId, !currentFavoriteStatus);
    }
}