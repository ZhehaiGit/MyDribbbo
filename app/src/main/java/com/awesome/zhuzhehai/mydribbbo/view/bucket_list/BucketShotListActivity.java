package com.awesome.zhuzhehai.mydribbbo.view.bucket_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.awesome.zhuzhehai.mydribbbo.view.shot_list.ShotListFragment;
import com.awesome.zhuzhehai.mydribbbo.view.base.SingleFragmentActivity;


public class BucketShotListActivity extends SingleFragmentActivity {

    public static final String KEY_BUCKET_NAME = "bucketName";

    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_BUCKET_NAME);
    }

    @NonNull
    @Override
    protected Fragment newFragment() {
        String bucketId = getIntent().getStringExtra(ShotListFragment.KEY_BUCKET_ID);
        return bucketId == null
                ? ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR)
                : ShotListFragment.newBucketListInstance(bucketId);
    }
}
