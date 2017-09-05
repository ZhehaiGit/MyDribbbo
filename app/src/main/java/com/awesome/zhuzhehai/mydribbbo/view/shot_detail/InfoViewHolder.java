package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

/**
 * Created by zhuzhehai on 11/4/16.
 */
class InfoViewHolder extends BaseViewHolder {
    @BindView(R.id.shot_author_title) TextView title;
    @BindView(R.id.shot_description) TextView description;

    @BindView(R.id.shot_author_image) SimpleDraweeView authorPicture;

    @BindView(R.id.shot_author_name) TextView authorName;
    @BindView(R.id.shot_like_count) TextView likeCount;
    @BindView(R.id.shot_view_count) TextView viewCount;
    @BindView(R.id.shot_bucket_count) TextView bucketCount;
    @BindView(R.id.shot_comment_count) TextView commentCount;
    @BindView(R.id.shot_like_click) ImageButton likeButton;
    @BindView(R.id.shot_bucket_click) ImageButton bucketButton;
    @BindView(R.id.shot_comment_click) ImageButton commentButton;
    @BindView(R.id.shot_share_btn) TextView shareButton;

    public InfoViewHolder(View view) {
        super(view);
    }
}
