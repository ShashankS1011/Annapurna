package com.example.annapurna;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class BackupHelper {

    public static boolean exportRecipesToJson(Context context, Uri destinationUri, List<RecipeEntity> recipes) {
        try {
            JSONArray array = new JSONArray();

            for (RecipeEntity recipe : recipes) {
                JSONObject obj = new JSONObject();
                obj.put("name", recipe.getName());
                obj.put("chefName", recipe.getChef()); // Fixed: changed getChefName() to getChef()
                obj.put("category", recipe.getCategory());
                obj.put("secretTip", recipe.getSecretTip() != null ? recipe.getSecretTip() : "");
                obj.put("occasion", recipe.getOccasion() != null ? recipe.getOccasion() : "");
                obj.put("cookingNotes", recipe.getCookingNotes() != null ? recipe.getCookingNotes() : "");
                obj.put("isFavorite", recipe.isFavorite());
                obj.put("videoPath", recipe.getVideoPath() != null ? recipe.getVideoPath() : "");
                array.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("version", 1);
            root.put("appName", "Annapurna");
            root.put("recipes", array);

            OutputStream outputStream = context.getContentResolver().openOutputStream(destinationUri);
            if (outputStream != null) {
                outputStream.write(root.toString(2).getBytes());
                outputStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<RecipeEntity> importRecipesFromJson(Context context, Uri sourceUri) {
        List<RecipeEntity> importedRecipes = new ArrayList<>();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject root = new JSONObject(sb.toString());
            if (root.has("recipes")) {
                JSONArray array = root.getJSONArray("recipes");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.optString("name", "");
                    String chef = obj.optString("chefName", "");
                    String category = obj.optString("category", "Main Course");

                    if (!name.isEmpty() && !chef.isEmpty()) {
                        RecipeEntity recipe = new RecipeEntity(name, chef, category, obj.optString("videoPath", ""));
                        recipe.setSecretTip(obj.optString("secretTip", ""));
                        recipe.setOccasion(obj.optString("occasion", ""));
                        recipe.setCookingNotes(obj.optString("cookingNotes", ""));
                        recipe.setFavorite(obj.optBoolean("isFavorite", false));

                        importedRecipes.add(recipe);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return importedRecipes;
    }
}