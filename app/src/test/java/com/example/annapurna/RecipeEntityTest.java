package com.example.annapurna;

import org.junit.Test;
import static org.junit.Assert.*;

public class RecipeEntityTest {

    @Test
    public void testRecipeEntityCreationAndGetters() {
        RecipeEntity recipe = new RecipeEntity("Shahi Paneer", "Mom", "Main Course", "video.mp4");
        recipe.setId(1);
        recipe.setSecretTip("Add a pinch of kasuri methi at the end.");
        recipe.setOccasion("Diwali Special");
        recipe.setFavorite(true);
        recipe.setVideoSource(LinkParserUtil.SOURCE_YOUTUBE);
        recipe.setThumbnailUrl("https://img.youtube.com/vi/abc/hqdefault.jpg");

        assertEquals(1, recipe.getId());
        assertEquals("Shahi Paneer", recipe.getName());
        assertEquals("Mom", recipe.getChef());
        assertEquals("Main Course", recipe.getCategory());
        assertEquals("video.mp4", recipe.getVideoPath());
        assertEquals("Add a pinch of kasuri methi at the end.", recipe.getSecretTip());
        assertEquals("Diwali Special", recipe.getOccasion());
        assertTrue(recipe.isFavorite());
        assertEquals(LinkParserUtil.SOURCE_YOUTUBE, recipe.getVideoSource());
        assertEquals("https://img.youtube.com/vi/abc/hqdefault.jpg", recipe.getThumbnailUrl());
    }
}
