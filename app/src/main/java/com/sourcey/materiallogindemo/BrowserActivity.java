package com.sourcey.materiallogindemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class BrowserActivity extends AppCompatActivity implements View.OnClickListener{

    ClipboardManager clipboard;
    String password;
    String username;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        String URL = null;
        View buttonBrowserExit = findViewById(R.id.button_browser_exit);
        buttonBrowserExit.setOnClickListener(this);
        View buttonCopyPassword = findViewById(R.id.button_copy_password);
        buttonCopyPassword.setOnClickListener(this);
        View buttonCopyUsername = findViewById(R.id.button_copy_username);
        buttonCopyUsername.setOnClickListener(this);


        WebView browser = (WebView) findViewById(R.id.browserView);


        Bundle extras = getIntent().getExtras();
        if (extras != null){
            URL = extras.getString("website");
            password = extras.getString("password");
            username = extras.getString("username");
        }
        URL = addHTTP(URL);
        WebSettings webSettings = browser.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        browser.setWebViewClient(new MyWebViewClient());

        browser.loadUrl(URL);


//        browser.loadUrl("http://facebook.com/");
    }

    @Override
    public void onPause() {
        super.onPause();
        clipboard.setPrimaryClip(ClipData.newPlainText("nope", "nope"));
    }

    @Override
    public void onStop() {
        super.onStop();
        clipboard.setPrimaryClip(ClipData.newPlainText("nope", "nope"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipboard.setPrimaryClip(ClipData.newPlainText("nope", "nope"));
    }

        @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_browser_exit:
                clipboard.setPrimaryClip(ClipData.newPlainText("nope", "nope"));
                finish();
                break;
            case R.id.button_copy_password:
                clipboard.setPrimaryClip(ClipData.newPlainText("password", password));
                Toast.makeText(getApplicationContext(), "Password copied to clipboard", Toast.LENGTH_LONG).show();
                break;
            case R.id.button_copy_username:
                clipboard.setPrimaryClip(ClipData.newPlainText("username", username));
                Toast.makeText(getApplicationContext(), "Username copied to clipboard", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public String addHTTP(String URL){
        if (!URL.contains("http://")) {
            URL = "http://" + URL;
        }
        return URL;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
