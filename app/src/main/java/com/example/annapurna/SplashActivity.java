package com.example.annapurna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Splash Screen Activity displaying animated Handi logo, tagline, and creator credit.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2400; // 2.4 seconds delay to allow animation to complete smoothly
    private static final String GITHUB_URL = "https://github.com/your-username"; // Replace with your actual GitHub profile URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load and start smooth scale/fade animation
        LinearLayout layoutSplashContent = findViewById(R.id.layoutSplashContent);
        if (layoutSplashContent != null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
            layoutSplashContent.startAnimation(animation);
        }

        // Set up creator click listener for GitHub
        TextView txtSplashCredit = findViewById(R.id.txtSplashCredit);
        if (txtSplashCredit != null) {
            txtSplashCredit.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
                startActivity(browserIntent);
            });
        }

        // Navigate to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}