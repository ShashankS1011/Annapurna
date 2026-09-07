package com.example.annapurna;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String chef;
    private String category;
    private String secretTip;
    private String occasion;
    private String cookingNotes;
    private String videoPath;
    private long videoPlaybackPosition;
    private boolean isFavorite;

    // NEW FIELDS FOR YOUTUBE & INSTAGRAM INTEGRATION
    private String videoSource;  // "LOCAL", "YOUTUBE", or "INSTAGRAM"
    private String thumbnailUrl; // Web image URL for YouTube/Instagram preview

    // NEW FIELDS FOR CHECKLIST & INSTRUCTIONS
    private String ingredients;
    private String instructions;

    // Required by Room
    public RecipeEntity() {
        this.videoSource = LinkParserUtil.SOURCE_LOCAL;
    }

    // Parameterized constructor for creation (Ignored by Room)
    @Ignore
    public RecipeEntity(String name, String chef, String category, String videoPath) {
        this.name = name;
        this.chef = chef;
        this.category = category;
        this.videoPath = videoPath;
        this.videoSource = LinkParserUtil.SOURCE_LOCAL;
    }

    // Full constructor if needed elsewhere
    @Ignore
    public RecipeEntity(String name, String chef, String category, String secretTip, String occasion, String cookingNotes, String videoPath) {
        this.name = name;
        this.chef = chef;
        this.category = category;
        this.secretTip = secretTip;
        this.occasion = occasion;
        this.cookingNotes = cookingNotes;
        this.videoPath = videoPath;
        this.videoSource = LinkParserUtil.SOURCE_LOCAL;
    }

    // Primary Key Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChef() {
        return chef;
    }

    public void setChef(String chef) {
        this.chef = chef;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSecretTip() {
        return secretTip;
    }

    public void setSecretTip(String secretTip) {
        this.secretTip = secretTip;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getCookingNotes() {
        return cookingNotes;
    }

    public void setCookingNotes(String cookingNotes) {
        this.cookingNotes = cookingNotes;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public long getVideoPlaybackPosition() {
        return videoPlaybackPosition;
    }

    public void setVideoPlaybackPosition(long videoPlaybackPosition) {
        this.videoPlaybackPosition = videoPlaybackPosition;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getVideoSource() {
        return videoSource != null ? videoSource : LinkParserUtil.SOURCE_LOCAL;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    // GETTERS AND SETTERS FOR INGREDIENTS & INSTRUCTIONS
    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}