package com.example.annapurna;

/**
 * Recipe model representing a single dish in Annapurna.
 */
public class Recipe {

    private long id;
    private String title;
    private String chef;
    private String category;
    private String prepTime;
    private String cookTime;
    private String ingredients;
    private String instructions;
    private String notes;
    private String momsSecret;
    private String videoUri;
    private boolean isLocalVideo;
    private String imageUri;
    private boolean isFavorite;

    // Default Constructor
    public Recipe() {
    }

    // Full Constructor
    public Recipe(long id, String title, String chef, String category, String prepTime,
                  String cookTime, String ingredients, String instructions, String notes,
                  String momsSecret, String videoUri, boolean isLocalVideo,
                  String imageUri, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.chef = chef;
        this.category = category;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.notes = notes;
        this.momsSecret = momsSecret;
        this.videoUri = videoUri;
        this.isLocalVideo = isLocalVideo;
        this.imageUri = imageUri;
        this.isFavorite = isFavorite;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMomsSecret() {
        return momsSecret;
    }

    public void setMomsSecret(String momsSecret) {
        this.momsSecret = momsSecret;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public boolean isLocalVideo() {
        return isLocalVideo;
    }

    public void setLocalVideo(boolean localVideo) {
        isLocalVideo = localVideo;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}