package com.example.annapurna;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object for recipe operations in Room database.
 */
@Dao
public interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(RecipeEntity recipe);

    @Update
    void updateRecipe(RecipeEntity recipe);

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    LiveData<List<RecipeEntity>> getAllRecipes();

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    List<RecipeEntity> getAllRecipesList();

    @Query("DELETE FROM recipes WHERE id IN (:recipeIds)")
    void deleteRecipesByIds(List<Integer> recipeIds);

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    LiveData<RecipeEntity> getRecipeById(int id);

    @Query("SELECT DISTINCT chef FROM recipes WHERE chef IS NOT NULL AND chef != '' ORDER BY chef ASC")
    LiveData<List<String>> getUniqueChefs();

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(int id, boolean isFavorite);

    /**
     * Searches recipes by name or chef with optional category filtering.
     * Uses 'name' to match the column in RecipeEntity.
     */
    @Query("SELECT * FROM recipes WHERE " +
            "(:query = '' OR name LIKE '%' || :query || '%' OR chef LIKE '%' || :query || '%') " +
            "AND (:category = 'All' OR category = :category) " +
            "ORDER BY id DESC")
    LiveData<List<RecipeEntity>> searchRecipes(String query, String category);
}