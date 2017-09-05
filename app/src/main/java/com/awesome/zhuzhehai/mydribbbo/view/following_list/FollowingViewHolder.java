package com.awesome.zhuzhehai.mydribbbo.view.following_list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;

//import Drippple.example.zhuzhehai.mydribbbo.R;

import butterknife.BindView;

/**
 * Created by zhuzhehai on 12/3/16.
 */
public class FollowingViewHolder extends BaseViewHolder {

    @BindView(R.id.following_user_prescribe) public TextView userPrescribe;
    @BindView(R.id.following_clickable_cover) public View cover;
    @BindView(R.id.following_user_name) public TextView userName;
    @BindView(R.id.following_user_iamge) public ImageView image;

    public FollowingViewHolder(View itemView) {
        super(itemView);
    }
}
