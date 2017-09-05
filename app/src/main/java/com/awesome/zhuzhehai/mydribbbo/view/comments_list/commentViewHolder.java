package com.awesome.zhuzhehai.mydribbbo.view.comments_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

class commentViewHolder extends BaseViewHolder {

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.commenter_image) public ImageView commenterImage;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.commenter_name) public TextView commenterName;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.comment) public TextView comment;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.comment_clickable_cover) public View cover;

    public commentViewHolder(View itemView) {

        super(itemView);
    }
}
