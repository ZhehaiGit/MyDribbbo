package com.awesome.zhuzhehai.mydribbbo.view.comments_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.awesome.zhuzhehai.mydribbbo.view.base.SingleFragmentActivity;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class CommentListActivity extends SingleFragmentActivity {
    public static final String COMMENT_TITLE = "comment_title";
    @NonNull
    @Override
    protected Fragment newFragment() {
        String shotID = getIntent().getExtras().getString(
                commentListFrangement.SHOT_ID);
        return commentListFrangement.newInstance(shotID);
    }
    @NonNull
    @Override
    protected String getActivityTitle() {
        return getIntent().getStringExtra(COMMENT_TITLE);}
}
