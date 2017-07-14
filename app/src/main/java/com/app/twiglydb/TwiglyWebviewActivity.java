package com.app.twiglydb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by naresh on 27/01/16.
 */
public class TwiglyWebviewActivity extends AppCompatActivity {


    @BindView(R.id.webView)
    WebView webView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.loading_progress)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twigly_webview_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("webview_title"));
        String method = getIntent().getStringExtra("webview_method");
        String data = getIntent().getStringExtra("webview_data");
        String url = getIntent().getStringExtra("webview_url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new TwiglyWebViewClient(progressBar));
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        if (method == null || !method.equalsIgnoreCase("post")) {
            webView.loadUrl(url);
        } else {
            webView.postUrl(url, data.getBytes());
        }
    }

}



