package com.example.annapurna.data.remote;

import com.example.annapurna.RecipeEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit API Interface defining remote endpoints for syncing recipes.
 */
public interface RecipeApiService {

    @GET("recipes.json")
    Call<List<RecipeEntity>> getRemoteRecipes();
}
