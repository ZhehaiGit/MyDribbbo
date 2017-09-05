package com.awesome.zhuzhehai.mydribbbo.view.bucket_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;

import butterknife.BindView;

public class BucketViewHolder extends BaseViewHolder {

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.bucket_layout) View bucketLayout;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.bucket_name) TextView bucketName;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.bucket_shot_count) TextView bucketShotCount;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.bucket_shot_chosen) ImageView bucketChosen;

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
}
