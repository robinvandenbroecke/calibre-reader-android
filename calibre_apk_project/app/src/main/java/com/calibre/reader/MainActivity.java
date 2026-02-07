package com.calibre.reader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.text.InputType;

public class MainActivity extends AppCompatActivity {
    
    private WebView webView;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "CalibrePrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String CALIBRE_URL = "http://100.112.100.14:7081/";
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize preferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Check if first run
        boolean firstRun = prefs.getBoolean(KEY_FIRST_RUN, true);
        
        // Show splash screen for 2 seconds
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            if (firstRun) {
                showCredentialsDialog();
            } else {
                initializeWebView();
            }
        }, 2000);
    }
    
    private void showCredentialsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibre Login");
        builder.setMessage("Enter your Calibre credentials");
        
        // Setup input
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        
        final EditText usernameInput = new EditText(this);
        usernameInput.setHint("Username");
        usernameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(usernameInput);
        
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);
        
        final CheckBox rememberCheck = new CheckBox(this);
        rememberCheck.setText("Remember credentials");
        rememberCheck.setChecked(true);
        layout.addView(rememberCheck);
        
        builder.setView(layout);
        
        // Set buttons
        builder.setPositiveButton("Login", (dialog, which) -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            boolean remember = rememberCheck.isChecked();
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                showCredentialsDialog();
                return;
            }
            
            // Save credentials if remember is checked
            SharedPreferences.Editor editor = prefs.edit();
            if (remember) {
                editor.putString(KEY_USERNAME, username);
                editor.putString(KEY_PASSWORD, password);
                editor.putBoolean(KEY_REMEMBER, true);
            } else {
                editor.remove(KEY_USERNAME);
                editor.remove(KEY_PASSWORD);
                editor.putBoolean(KEY_REMEMBER, false);
            }
            editor.putBoolean(KEY_FIRST_RUN, false);
            editor.apply();
            
            initializeWebView();
        });
        
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        
        builder.setCancelable(false);
        builder.show();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        setContentView(R.layout.activity_main);
        
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        
        // Configure WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        
        // Enable cookies
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        
        // Set custom WebViewClient with Digest auth support
        webView.setWebViewClient(new DigestAuthWebViewClient(
            prefs.getString(KEY_USERNAME, ""),
            prefs.getString(KEY_PASSWORD, "")
        ));
        
        // Set WebChromeClient for progress
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        
        // Load Calibre URL
        webView.loadUrl(CALIBRE_URL);
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}