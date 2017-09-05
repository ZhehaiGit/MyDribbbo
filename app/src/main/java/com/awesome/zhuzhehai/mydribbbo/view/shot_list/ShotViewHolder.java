package com.awesome.zhuzhehai.mydribbbo.view.shot_list;

import android.view.View;
import android.widget.TextView;

//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;

class ShotViewHolder extends BaseViewHolder {
    //create a class that contain all the view in shot View holder!!!!!!!!!!!
    //ann shot view holder is the children of recycle view
    //view in shot!

    @BindView(R.id.shot_clickable_cover) public View cover;
    @BindView(R.id.shot_like_count) public TextView likeCount;
    @BindView(R.id.shot_bucket_count) public TextView bucketCount;
    @BindView(R.id.shot_view_count) public TextView viewCount;
    @BindView(R.id.shot_comment_count) public TextView commentCount;
    @BindView(R.id.shot_image) public SimpleDraweeView image;

    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
