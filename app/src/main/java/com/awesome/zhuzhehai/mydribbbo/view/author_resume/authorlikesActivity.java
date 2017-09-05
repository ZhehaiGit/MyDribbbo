package com.awesome.zhuzhehai.mydribbbo.view.author_resume;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.awesome.zhuzhehai.mydribbbo.view.shot_list.ShotListFragment;
import com.awesome.zhuzhehai.mydribbbo.view.base.SingleFragmentActivity;

/**
 * Created by zhuzhehai on 12/3/16.
 */
public class authorlikesActivity extends SingleFragmentActivity {
    public static final String KEY_USER_NAME1 = "user name";
    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(KEY_USER_NAME1)+"'s Likes";
    }
    @NonNull
    @Override
    protected Fragment newFragment() {
        String userS = getIntent().getStringExtra(ShotListFragment.KEY_USER_ID_LIKES);
        return   ShotListFragment.newFollowingLikesInstance(userS);
    }
}
