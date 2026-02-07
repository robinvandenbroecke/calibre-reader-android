package com.calibre.reader;

import android.annotation.SuppressLint;
import android.webkit.HttpAuthHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.text.InputType;

/**
 * Custom WebViewClient that handles Digest authentication
 * Required for Calibre Web interface which uses Digest auth
 */
public class DigestAuthWebViewClient extends WebViewClient {
    
    private String username;
    private String password;
    private boolean credentialsProvided = false;
    
    public DigestAuthWebViewClient(String username, String password) {
        this.username = username;
        this.password = password;
        this.credentialsProvided = !username.isEmpty() && !password.isEmpty();
    }
    
    @Override
    public void onReceivedHttpAuthRequest(WebView view, 
                                         HttpAuthHandler handler, 
                                         String host, 
                                         String realm) {
        
        // Check if we have stored credentials
        if (credentialsProvided) {
            // Use stored credentials
            handler.proceed(username, password);
        } else {
            // Show dialog for user to enter credentials
            showAuthDialog(view, handler, host, realm);
        }
    }
    
    private void showAuthDialog(WebView view, HttpAuthHandler handler, String host, String realm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Authentication Required");
        builder.setMessage("Calibre requires authentication\\nRealm: " + realm + "\\nHost: " + host);
        
        // Setup input
        LinearLayout layout = new LinearLayout(view.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        
        final EditText usernameInput = new EditText(view.getContext());
        usernameInput.setHint("Username");
        usernameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(usernameInput);
        
        final EditText passwordInput = new EditText(view.getContext());
        passwordInput.setHint("Password");
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(passwordInput);
        
        final CheckBox rememberCheck = new CheckBox(view.getContext());
        rememberCheck.setText("Remember for this session");
        rememberCheck.setChecked(true);
        layout.addView(rememberCheck);
        
        builder.setView(layout);
        
        // Set buttons
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredUsername = usernameInput.getText().toString().trim();
                String enteredPassword = passwordInput.getText().toString();
                
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    Toast.makeText(view.getContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    showAuthDialog(view, handler, host, realm);
                    return;
                }
                
                // Update credentials for this session
                username = enteredUsername;
                password = enteredPassword;
                credentialsProvided = true;
                
                // Proceed with authentication
                handler.proceed(enteredUsername, enteredPassword);
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
                Toast.makeText(view.getContext(), "Authentication cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setCancelable(false);
        builder.show();
    }
    
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // Handle all URLs within WebView
        if (url.startsWith("http://") || url.startsWith("https://")) {
            view.loadUrl(url);
            return true;
        }
        return false;
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // Page loaded successfully
    }
    
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        
        // Show error message
        String errorMessage = "Error loading page: " + description;
        if (errorCode == ERROR_AUTHENTICATION) {
            errorMessage = "Authentication failed. Please check your credentials.";
        } else if (errorCode == ERROR_CONNECT) {
            errorMessage = "Cannot connect to Calibre server. Please check your network.";
        } else if (errorCode == ERROR_HOST_LOOKUP) {
            errorMessage = "Server not found. Please check the URL.";
        }
        
        // Show error in WebView
        String errorHtml = "<html><body><div style='padding:20px;text-align:center;'>" +
                          "<h3>Error</h3>" +
                          "<p>" + errorMessage + "</p>" +
                          "<p>URL: " + failingUrl + "</p>" +
                          "<button onclick='location.reload()' style='padding:10px 20px;'>Retry</button>" +
                          "</div></body></html>";
        
        view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
    }
}