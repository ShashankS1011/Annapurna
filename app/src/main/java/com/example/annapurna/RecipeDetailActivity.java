package com.example.annapurna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "RECIPE_ID";

    private RecipeViewModel recipeViewModel;
    private int recipeId = -1;
    private RecipeEntity currentRecipe;

    private ImageView btnBack;
    private ImageButton btnFavorite, btnShare;
    private TextView txtTitle, txtChef, txtCategory, txtSecretTip, txtOccasion, txtNotes;
    private View cardSecretTip, cardNotes;

    private PlayerView playerView;
    private ExoPlayer player;
    private long playbackPosition = 0;

    // WakeLock for Cooking Mode
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if (getIntent() != null) {
            recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
            if (recipeId == -1) {
                recipeId = getIntent().getIntExtra("EXTRA_RECIPE_ID", -1);
            }
        }

        if (recipeId == -1) {
            Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Default set explicitly to false (OFF by default)
        SharedPreferences prefs = getSharedPreferences("AnnapurnaPrefs", MODE_PRIVATE);
        boolean isCookingModeEnabled = prefs.getBoolean("cooking_mode", false);

        if (getWindow() != null && getWindow().getDecorView() != null) {
            getWindow().getDecorView().post(() -> updateCookingMode(isCookingModeEnabled));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Automatically release WakeLock when leaving the screen or minimizing app
        releaseWakeLock();
    }

    private void updateCookingMode(boolean enabled) {
        Log.d("Annapurna", "Cooking mode status: " + enabled);

        if (enabled) {
            // 1. Apply Window Flags
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (getWindow().getDecorView() != null) {
                getWindow().getDecorView().setKeepScreenOn(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(true);
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }

            // 2. Acquire Hardware-Level WakeLock
            acquireWakeLock();
        } else {
            // Clear Window Flags
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (getWindow().getDecorView() != null) {
                getWindow().getDecorView().setKeepScreenOn(false);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(false);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }

            // Release Hardware WakeLock
            releaseWakeLock();
        }
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                        "Annapurna:CookingModeWakeLock"
                );
            }
        }

        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(120 * 60 * 1000L /* 2 Hours Timeout max */);
            Log.d("Annapurna", "WakeLock Acquired successfully");
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            Log.d("Annapurna", "WakeLock Released successfully");
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnDetailFavorite);
        btnShare = findViewById(R.id.btnDetailShare);

        txtTitle = findViewById(R.id.txtDetailTitle);
        txtChef = findViewById(R.id.txtDetailChef);
        txtCategory = findViewById(R.id.txtDetailCategory);
        txtSecretTip = findViewById(R.id.txtDetailSecretTip);
        txtOccasion = findViewById(R.id.txtDetailOccasion);
        txtNotes = findViewById(R.id.txtDetailNotes);

        cardSecretTip = findViewById(R.id.cardSecretTip);
        cardNotes = findViewById(R.id.cardNotes);

        playerView = findViewById(R.id.playerView);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                if (currentRecipe != null) {
                    recipeViewModel.toggleFavorite(currentRecipe.getId(), currentRecipe.isFavorite());
                }
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (currentRecipe != null) {
                    shareRecipe(currentRecipe);
                }
            });
        }
    }

    private void setupViewModel() {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);

        recipeViewModel.getRecipeById(recipeId).observe(this, recipe -> {
            if (recipe != null) {
                currentRecipe = recipe;
                populateRecipeDetails(recipe);
            }
        });
    }

    private void populateRecipeDetails(RecipeEntity recipe) {
        if (txtTitle != null) txtTitle.setText(recipe.getName());
        if (txtChef != null) txtChef.setText(recipe.getChef());
        if (txtCategory != null) txtCategory.setText(recipe.getCategory());

        if (btnFavorite != null) {
            btnFavorite.setImageResource(
                    recipe.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
        }

        if (recipe.getSecretTip() != null && !recipe.getSecretTip().trim().isEmpty()) {
            if (cardSecretTip != null) cardSecretTip.setVisibility(View.VISIBLE);
            if (txtSecretTip != null) txtSecretTip.setText(recipe.getSecretTip());
        } else if (cardSecretTip != null) {
            cardSecretTip.setVisibility(View.GONE);
        }

        if (recipe.getOccasion() != null && !recipe.getOccasion().trim().isEmpty()) {
            if (txtOccasion != null) {
                txtOccasion.setVisibility(View.VISIBLE);
                txtOccasion.setText(recipe.getOccasion());
            }
        } else if (txtOccasion != null) {
            txtOccasion.setVisibility(View.GONE);
        }

        if (recipe.getCookingNotes() != null && !recipe.getCookingNotes().trim().isEmpty()) {
            if (cardNotes != null) cardNotes.setVisibility(View.VISIBLE);
            if (txtNotes != null) txtNotes.setText(recipe.getCookingNotes());
        } else if (cardNotes != null) {
            cardNotes.setVisibility(View.GONE);
        }

        if (recipe.getVideoPath() != null && !recipe.getVideoPath().trim().isEmpty()) {
            if (playerView != null) playerView.setVisibility(View.VISIBLE);
            playbackPosition = recipe.getVideoPlaybackPosition();
            initializePlayer(recipe.getVideoPath());
        } else if (playerView != null) {
            playerView.setVisibility(View.GONE);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String videoUriString) {
        if (player == null && playerView != null) {
            playerView.setKeepScreenOn(false);

            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            // Re-apply cooking mode on player state transitions to prevent ExoPlayer from clearing wake flags
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    SharedPreferences prefs = getSharedPreferences("AnnapurnaPrefs", MODE_PRIVATE);
                    boolean isCookingModeEnabled = prefs.getBoolean("cooking_mode", false);
                    updateCookingMode(isCookingModeEnabled);
                }
            });

            Uri videoUri = Uri.parse(videoUriString);
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.seekTo(playbackPosition);
            player.prepare();
        }
    }

    private void shareRecipe(RecipeEntity recipe) {
        StringBuilder shareText = new StringBuilder();
        shareText.append("🍲 *").append(recipe.getName()).append("*\n");
        shareText.append("👨‍🍳 Chef: ").append(recipe.getChef()).append("\n");
        shareText.append("📁 Category: ").append(recipe.getCategory()).append("\n\n");

        if (recipe.getSecretTip() != null && !recipe.getSecretTip().trim().isEmpty()) {
            shareText.append("💡 *Mom's Secret Tip:* ").append(recipe.getSecretTip()).append("\n\n");
        }

        if (recipe.getCookingNotes() != null && !recipe.getCookingNotes().trim().isEmpty()) {
            shareText.append("📝 *Notes:* ").append(recipe.getCookingNotes()).append("\n\n");
        }

        shareText.append("Shared via Annapurna App ❤️");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null && currentRecipe != null) {
            currentRecipe.setVideoPlaybackPosition(player.getCurrentPosition());
            recipeViewModel.updateRecipe(currentRecipe);
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }
}