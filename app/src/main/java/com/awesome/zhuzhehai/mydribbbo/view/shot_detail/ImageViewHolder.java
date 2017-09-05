package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by zhuzhehai on 11/4/16.
 */
class ImageViewHolder extends RecyclerView.ViewHolder{
    SimpleDraweeView image;

    public ImageViewHolder(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView;
    }
}
