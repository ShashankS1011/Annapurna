package com.example.annapurna;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.OptIn;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.Locale;

public class RecipeDetailActivity extends BaseActivity {

    public static final String EXTRA_RECIPE_ID = "RECIPE_ID";

    private RecipeViewModel recipeViewModel;
    private int recipeId = -1;
    private RecipeEntity currentRecipe;

    // View References
    private ImageView btnBack;
    private ImageButton btnFavorite, btnShare;
    private TextView txtTitle, txtChef, txtCategory, txtSecretTip, txtNotes, txtInstructions;
    private View cardSecretTip, cardNotes;

    // Checklist & Timer References
    private LinearLayout layoutIngredientsContainer;
    private TextView txtTimerDisplay;
    private Button btnStartTimer, btnResetTimer;
    private Button btnEditRecipe, btnOpenExternal, btnDeleteRecipe;

    // Timer Variables
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private long secondsElapsed = 0;
    private boolean isTimerRunning = false;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            secondsElapsed++;
            long mins = secondsElapsed / 60;
            long secs = secondsElapsed % 60;
            if (txtTimerDisplay != null) {
                txtTimerDisplay.setText(String.format(Locale.getDefault(), "⏱️ Cooking Timer: %02d:%02d", mins, secs));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    // Media Players
    private PlayerView playerView;
    private WebView webViewPlayer;
    private ExoPlayer player;
    private long playbackPosition = 0;

    // Fullscreen WebView Management
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;
    private WebChromeClient customWebChromeClient;

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

        setupOnBackPressedDispatcher();
        initViews();
        setupViewModel();
    }

    private void setupOnBackPressedDispatcher() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (customView != null && customWebChromeClient != null) {
                    customWebChromeClient.onHideCustomView();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("AnnapurnaPrefs", MODE_PRIVATE);
        boolean isCookingModeEnabled = prefs.getBoolean("cooking_mode", false);

        if (getWindow() != null) {
            getWindow().getDecorView().post(() -> updateCookingMode(isCookingModeEnabled));
        }

        if (webViewPlayer != null) {
            webViewPlayer.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
        if (webViewPlayer != null) {
            webViewPlayer.onPause();
        }
    }

    private void updateCookingMode(boolean enabled) {
        if (enabled) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setKeepScreenOn(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(true);
            }

            acquireWakeLock();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setKeepScreenOn(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setTurnScreenOn(false);
            }

            releaseWakeLock();
        }
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        "Annapurna:CookingModeWakeLock"
                );
            }
        }

        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire(120 * 60 * 1000L);
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
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
        txtNotes = findViewById(R.id.txtDetailNotes);
        txtInstructions = findViewById(R.id.txtDetailInstructions);

        cardSecretTip = findViewById(R.id.cardSecretTip);
        cardNotes = findViewById(R.id.cardNotes);

        layoutIngredientsContainer = findViewById(R.id.layoutIngredientsContainer);
        txtTimerDisplay = findViewById(R.id.txtTimerDisplay);
        btnStartTimer = findViewById(R.id.btnStartTimer);
        btnResetTimer = findViewById(R.id.btnResetTimer);

        btnEditRecipe = findViewById(R.id.btnEditRecipe);
        btnOpenExternal = findViewById(R.id.btnOpenExternal);
        btnDeleteRecipe = findViewById(R.id.btnDeleteRecipe);

        playerView = findViewById(R.id.playerView);
        webViewPlayer = findViewById(R.id.webViewPlayer);

        setupWebView();
        setupListeners();
    }

    private void setupListeners() {
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                if (currentRecipe != null) {
                    recipeViewModel.toggleFavorite(currentRecipe.getId(), currentRecipe.isFavorite());
                }
            });
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                if (currentRecipe != null) shareRecipe(currentRecipe);
            });
        }

        // Cooking Timer Controls
        btnStartTimer.setOnClickListener(v -> {
            if (isTimerRunning) {
                timerHandler.removeCallbacks(timerRunnable);
                btnStartTimer.setText("Start");
                isTimerRunning = false;
            } else {
                timerHandler.postDelayed(timerRunnable, 1000);
                btnStartTimer.setText("Pause");
                isTimerRunning = true;
            }
        });

        btnResetTimer.setOnClickListener(v -> {
            timerHandler.removeCallbacks(timerRunnable);
            secondsElapsed = 0;
            isTimerRunning = false;
            txtTimerDisplay.setText("⏱️ Cooking Timer: 00:00");
            btnStartTimer.setText("Start");
        });

        // Bottom Action Buttons
        btnEditRecipe.setOnClickListener(v -> {
            if (currentRecipe != null) {
                Intent intent = new Intent(this, AddRecipeActivity.class);
                intent.putExtra(EXTRA_RECIPE_ID, currentRecipe.getId());
                startActivity(intent);
            }
        });

        btnOpenExternal.setOnClickListener(v -> {
            if (currentRecipe != null && currentRecipe.getVideoPath() != null && !currentRecipe.getVideoPath().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentRecipe.getVideoPath()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No valid link to open", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteRecipe.setOnClickListener(v -> confirmDeletion());
    }

    private void confirmDeletion() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (currentRecipe != null) {
                        recipeViewModel.deleteRecipe(currentRecipe);
                        Toast.makeText(this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
        if (txtChef != null) txtChef.setText("Chef: " + recipe.getChef());
        if (txtCategory != null) txtCategory.setText(recipe.getCategory());

        if (btnFavorite != null) {
            btnFavorite.setImageResource(
                    recipe.isFavorite() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
            );
        }

        // Mom's Secret Tip Card
        if (recipe.getSecretTip() != null && !recipe.getSecretTip().trim().isEmpty()) {
            if (cardSecretTip != null) cardSecretTip.setVisibility(View.VISIBLE);
            if (txtSecretTip != null) txtSecretTip.setText(recipe.getSecretTip());
        } else if (cardSecretTip != null) {
            cardSecretTip.setVisibility(View.GONE);
        }

        // Cooking Notes Card
        if (recipe.getCookingNotes() != null && !recipe.getCookingNotes().trim().isEmpty()) {
            if (cardNotes != null) cardNotes.setVisibility(View.VISIBLE);
            if (txtNotes != null) txtNotes.setText(recipe.getCookingNotes());
        } else if (cardNotes != null) {
            cardNotes.setVisibility(View.GONE);
        }

        // Dynamic Ingredients Checklist
        populateIngredientsChecklist(recipe.getIngredients());

        // Step-by-Step Instructions
        if (txtInstructions != null) {
            if (recipe.getInstructions() != null && !recipe.getInstructions().trim().isEmpty()) {
                txtInstructions.setText(recipe.getInstructions());
            } else {
                txtInstructions.setText("No specific step-by-step instructions provided for this recipe.");
            }
        }

        handleVideoPlayback(recipe);
    }

    private void populateIngredientsChecklist(String rawIngredients) {
        if (layoutIngredientsContainer == null) return;
        layoutIngredientsContainer.removeAllViews();

        if (rawIngredients == null || rawIngredients.trim().isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No ingredients added.");
            emptyText.setTextColor(0xFF888888);
            layoutIngredientsContainer.addView(emptyText);
            return;
        }

        String[] items = rawIngredients.split("\n|,");
        for (String item : items) {
            String trimmed = item.trim();
            if (trimmed.isEmpty()) continue;

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(trimmed);
            checkBox.setTextColor(0xFFFFFFFF);
            checkBox.setPadding(8, 8, 8, 8);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            });

            layoutIngredientsContainer.addView(checkBox);
        }
    }

    private void setupWebView() {
        if (webViewPlayer != null) {
            WebSettings settings = webViewPlayer.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setDatabaseEnabled(true);
            settings.setMediaPlaybackRequiresUserGesture(false);
            settings.setAllowFileAccess(true);
            settings.setAllowContentAccess(true);

            String defaultUserAgent = settings.getUserAgentString();
            settings.setUserAgentString(defaultUserAgent.replace("Android", "Android 10; Mobile"));

            webViewPlayer.setWebViewClient(new WebViewClient());

            customWebChromeClient = new WebChromeClient() {
                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (customView != null) {
                        onHideCustomView();
                        return;
                    }

                    customView = view;
                    customViewCallback = callback;

                    fullscreenContainer = new FrameLayout(RecipeDetailActivity.this);
                    fullscreenContainer.setBackgroundColor(0xFF000000);
                    fullscreenContainer.addView(customView, new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));

                    ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    decorView.addView(fullscreenContainer, new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));

                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                @Override
                public void onHideCustomView() {
                    if (customView == null) return;

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    if (fullscreenContainer != null) {
                        decorView.removeView(fullscreenContainer);
                        fullscreenContainer = null;
                    }

                    customView = null;
                    if (customViewCallback != null) {
                        customViewCallback.onCustomViewHidden();
                    }
                }
            };

            webViewPlayer.setWebChromeClient(customWebChromeClient);
        }
    }

    private void handleVideoPlayback(RecipeEntity recipe) {
        String videoPath = recipe.getVideoPath();
        String source = recipe.getVideoSource();

        if (videoPath == null || videoPath.trim().isEmpty()) {
            if (playerView != null) playerView.setVisibility(View.GONE);
            if (webViewPlayer != null) webViewPlayer.setVisibility(View.GONE);
            return;
        }

        if (LinkParserUtil.SOURCE_YOUTUBE.equalsIgnoreCase(source)) {
            if (playerView != null) playerView.setVisibility(View.GONE);
            if (webViewPlayer != null) {
                webViewPlayer.setVisibility(View.VISIBLE);
                String videoId = LinkParserUtil.extractYouTubeId(videoPath);

                String htmlData = "<!DOCTYPE html><html><head>" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">" +
                        "<style>body{margin:0;padding:0;background-color:#000000;} .iframe-container{position:relative;width:100%;padding-top:56.25%;} .iframe-container iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0;}</style>" +
                        "</head><body>" +
                        "<div class=\"iframe-container\">" +
                        "<iframe src=\"https://www.youtube-nocookie.com/embed/" + videoId + "?playsinline=1&enablejsapi=1&rel=0&fs=1&autoplay=0\" " +
                        "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen\" " +
                        "allowfullscreen></iframe>" +
                        "</div></body></html>";

                webViewPlayer.loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlData, "text/html", "utf-8", null);
            }
        } else if (LinkParserUtil.SOURCE_INSTAGRAM.equalsIgnoreCase(source)) {
            if (playerView != null) playerView.setVisibility(View.GONE);
            if (webViewPlayer != null) {
                webViewPlayer.setVisibility(View.VISIBLE);
                String html = LinkParserUtil.getInstagramEmbedHtml(videoPath);
                webViewPlayer.loadDataWithBaseURL("https://www.instagram.com", html, "text/html", "utf-8", null);
            }
        } else {
            if (webViewPlayer != null) webViewPlayer.setVisibility(View.GONE);
            if (playerView != null) {
                playerView.setVisibility(View.VISIBLE);
                playbackPosition = recipe.getVideoPlaybackPosition();
                initializePlayer(videoPath);
            }
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String videoUriString) {
        if (playerView == null) return;

        if (player != null) {
            player.release();
            player = null;
        }

        try {
            Uri videoUri = Uri.parse(videoUriString);
            try {
                grantUriPermission(getPackageName(), videoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) {}

            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);

            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    SharedPreferences prefs = getSharedPreferences("AnnapurnaPrefs", MODE_PRIVATE);
                    boolean isCookingModeEnabled = prefs.getBoolean("cooking_mode", false);
                    updateCookingMode(isCookingModeEnabled);
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e("RecipeDetail", "ExoPlayer Error: " + error.getMessage());
                    Toast.makeText(RecipeDetailActivity.this, "Cannot access local video file.", Toast.LENGTH_SHORT).show();
                }
            });

            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.seekTo(playbackPosition);
            player.prepare();
            player.setPlayWhenReady(false);

        } catch (Exception e) {
            Log.e("RecipeDetail", "Error launching local video: " + e.getMessage());
            Toast.makeText(this, "Failed to load local video file", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareRecipe(RecipeEntity recipe) {
        StringBuilder shareText = new StringBuilder();
        shareText.append("🍲 *").append(recipe.getName()).append("*\n");
        shareText.append("👨‍🍳 Chef: ").append(recipe.getChef()).append("\n\n");

        if (recipe.getVideoPath() != null && !recipe.getVideoPath().isEmpty()) {
            shareText.append("🎥 Watch Video: ").append(recipe.getVideoPath()).append("\n\n");
        }

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
        timerHandler.removeCallbacks(timerRunnable);
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
        timerHandler.removeCallbacks(timerRunnable);
        releaseWakeLock();
        if (webViewPlayer != null) {
            webViewPlayer.destroy();
        }
    }
}