package com.awesome.zhuzhehai.mydribbbo.view.about_app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 12/4/16.
 */
public class aboutAppActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.paypal) TextView paypal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }
    private void setupUI() {
        setContentView(R.layout.about_dripppel);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (isBackEnabled()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getActivityTitle());

        paypal.setText("PayPal: zhehaizhu@gmail.com");
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "zhehaizhu@gmail.com");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(aboutAppActivity.this,"Address Copied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBackEnabled() && item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected boolean isBackEnabled() {
        return true;
    }

    @NonNull
    protected String getActivityTitle() {
        return "About Drippple";
    }
}
