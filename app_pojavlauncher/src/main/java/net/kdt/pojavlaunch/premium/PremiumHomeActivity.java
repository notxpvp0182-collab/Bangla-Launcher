package net.kdt.pojavlaunch.premium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.banglalauncher.app.R;

public class PremiumHomeActivity extends AppCompatActivity {
    private Button btnPlay;
    private ImageButton btnSettings, navHome, navDownloads, navAccounts, navSettings;
    private TextView textMemory, textJava, textRenderer, textNews;
    private Spinner spinnerVersion, spinnerAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_premium);
        
        // Force landscape
        setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        initializeViews();
        setupListeners();
        updateSystemInfo();
    }

    private void initializeViews() {
        btnPlay = findViewById(R.id.btn_play);
        btnSettings = findViewById(R.id.btn_settings);
        textMemory = findViewById(R.id.text_memory);
        textJava = findViewById(R.id.text_java);
        textRenderer = findViewById(R.id.text_renderer);
        textNews = findViewById(R.id.text_news);
        spinnerVersion = findViewById(R.id.spinner_version);
        spinnerAccount = findViewById(R.id.spinner_account);
        navHome = findViewById(R.id.nav_home);
        navDownloads = findViewById(R.id.nav_downloads);
        navAccounts = findViewById(R.id.nav_accounts);
        navSettings = findViewById(R.id.nav_settings);
    }

    private void setupListeners() {
        btnPlay.setOnClickListener(v -> launchGame());
        btnSettings.setOnClickListener(v -> openSettings());
        navHome.setOnClickListener(v -> { /* Already on home */ });
        navDownloads.setOnClickListener(v -> openDownloads());
        navAccounts.setOnClickListener(v -> openAccounts());
        navSettings.setOnClickListener(v -> openSettings());
    }

    private void updateSystemInfo() {
        // Get runtime memory
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576;
        long maxMemory = runtime.maxMemory() / 1048576;
        textMemory.setText(usedMemory + "MB / " + maxMemory + "MB");
        
        // Get Java version
        String javaVersion = System.getProperty("java.version", "Unknown");
        textJava.setText("Java " + javaVersion.split("\\.")[0]);
        
        // Get Renderer (default to OpenGL)
        textRenderer.setText("OpenGL 4.6");
    }

    private void launchGame() {
        // TODO: Implement game launch logic
    }

    private void openSettings() {
        // TODO: Open settings activity
    }

    private void openDownloads() {
        // TODO: Open downloads activity
    }

    private void openAccounts() {
        // TODO: Open accounts activity
    }
}
