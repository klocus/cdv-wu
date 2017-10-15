package com.example.klocusoutlookcom.wirtualnauczelnia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private View mProgressView;
    private WebView mWebView;
    private String url = "https://portal.cdv.pl/";
    private String js = "https://cdv.blutu.pl/wu.min.js";
    private String injection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get JavaScript content into "injection" variable
        getRemoteContent(js);

        // Progressbar
        mProgressView = findViewById(R.id.progressbar);

        // WebView
        mWebView = (WebView) findViewById(R.id.webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new CustomJSInterface(this, mProgressView), "JSInterface");

        // Enable local storage
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // Stop local links and redirects from opening in browser instead of WebView
        mWebView.setWebViewClient(new CustomWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectJS();
            }
        });

        // Download listener to handle any kind of file
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        // Load URL to WebView
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void getRemoteContent(final String href) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(href);
                    String code = new Scanner( url.openStream() ).useDelimiter("\\A").next();
                    byte[] buffer = code.getBytes();
                    injection = Base64.encodeToString(buffer, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void injectJS() {
        mWebView.loadUrl("javascript:" +
            "(function() {" +
            "var script = document.createElement('script');" +
            "script.innerHTML = window.atob('" + injection + "');" +
            "document.body.appendChild(script);" +
            "})()"
        );
    }
}
