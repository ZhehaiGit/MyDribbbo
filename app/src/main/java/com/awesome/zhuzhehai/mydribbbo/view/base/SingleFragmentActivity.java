package com.awesome.zhuzhehai.mydribbbo.view.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 11/4/16.
 */
@SuppressWarnings("ConstantConditions")
public abstract class SingleFragmentActivity extends AppCompatActivity {
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.toolbar) Toolbar toolbar;

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.awesome.zhuzhehai.mydribbbo.R.layout.activity_single_fragment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (isBackEnabled()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getActivityTitle());
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(com.awesome.zhuzhehai.mydribbbo.R.id.fragment_container, newFragment())
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBackEnabled() && item.getItemId() == android.R.id.home) {
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
        return "";
    }

    @NonNull
    protected abstract Fragment newFragment();
}
