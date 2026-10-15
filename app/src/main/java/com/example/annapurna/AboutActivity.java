package com.example.annapurna;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * About activity showing app info, developer profile, and family cookbook mission.
 */
public class AboutActivity extends BaseActivity {

    private static final String GITHUB_PROFILE = "https://github.com/ShashankS1011";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView btnBack = findViewById(R.id.btnAboutBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        TextView txtVersion = findViewById(R.id.txtAboutVersion);
        if (txtVersion != null) {
            txtVersion.setText("Version 1.1.0 (Enterprise)");
        }

        TextView txtCreatorCredit = findViewById(R.id.txtCreatorCredit);
        if (txtCreatorCredit != null) {
            txtCreatorCredit.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_PROFILE));
                startActivity(intent);
            });
        }
    }
}
