package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.awesome.zhuzhehai.mydribbbo.view.base.SingleFragmentActivity;

/**
 * Created by zhuzhehai on 11/4/16.
 */
public class shotActivity extends SingleFragmentActivity {
    public static final String KEY_SHOT_TITLE = "shot_title";
    @NonNull
    @Override
    protected Fragment newFragment() {
        return ShotFragment.newInstance(getIntent().getExtras());
    }

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_SHOT_TITLE);
    }
}
