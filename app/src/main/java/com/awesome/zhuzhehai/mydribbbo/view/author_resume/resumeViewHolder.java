package com.awesome.zhuzhehai.mydribbbo.view.author_resume;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class resumeViewHolder extends BaseViewHolder {
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_resume_image) public ImageView autherImage;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_resume_name) public TextView autherName;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_bio) public TextView autherBio;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_twitter) public TextView autherTwitter;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_web) public TextView autherWeb;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_location ) public TextView autherLocation;

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_shots_count) public TextView autherShotsCount;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_likes_count ) public TextView autherLikesCount;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_follower_count ) public TextView autherFollowerCount;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_following_count ) public TextView autherFollowingCount;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.author_team_count ) public TextView autherTeamCount;


    public resumeViewHolder(View itemView) {
        super(itemView);
    }
}
