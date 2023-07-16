package com.example.mygymy.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.mygymy.R;

public class UserGuide extends AppCompatActivity {
    WebView webView; //initalise the webview

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity as "activity_user_guide".
        setContentView(R.layout.activity_user_guide);
        webView = findViewById(R.id.userguide);
        WebSettings webSettings = webView.getSettings();  // Get the settings for this WebView.
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());
        // Load the "user_guide.html" file from the Android assets directory into the WebView.
        webView.loadUrl("file:///android_asset/user_guide.html");

    }

    private class Callback extends WebViewClient {
        @Override
        // This method is triggered whenever a key event is dispatched to the WebView.
        // It returns false to indicate that the event has not been handled.
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}