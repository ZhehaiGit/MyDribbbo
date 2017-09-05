package com.awesome.zhuzhehai.mydribbbo.dribbble.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("ConstantConditions")
public class AuthActivity extends AppCompatActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_CODE = "code";

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.progress_bar) ProgressBar progressBar;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.webview) WebView webView;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.awesome.zhuzhehai.mydribbbo.R.layout.activity_webview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(com.awesome.zhuzhehai.mydribbbo.R.string.auth_activity_title));

        progressBar.setMax(100);//进度条

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(Auth.REDIRECT_URI)) {// get the string after www.
                    Uri uri = Uri.parse(url);//jiexi URl
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_CODE, uri.getQueryParameter(KEY_CODE));
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }


            // show  progress Bar
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        //进度条的变化
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        String url = getIntent().getStringExtra(KEY_URL);
        webView.loadUrl(url);// load log in web;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
