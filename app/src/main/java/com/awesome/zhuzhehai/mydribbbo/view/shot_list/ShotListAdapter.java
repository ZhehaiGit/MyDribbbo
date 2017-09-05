package com.awesome.zhuzhehai.mydribbbo.view.shot_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.ImageUtils;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.awesome.zhuzhehai.mydribbbo.view.shot_detail.ShotFragment;
import com.awesome.zhuzhehai.mydribbbo.view.shot_detail.shotActivity;
//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

class ShotListAdapter extends InfinitAdapter<Shot> {

    private final ShotListFragment shotListFragment;
    private int listType;

    public ShotListAdapter(@NonNull ShotListFragment shotListFragment,
                           @NonNull List<Shot> data, @NonNull int listType,
                           @NonNull LoadMoreListener loadMoreListener
                           ) {
        super(shotListFragment.getContext(), data,loadMoreListener);
        this.shotListFragment = shotListFragment;
        this.listType = listType;
    }
    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                                  .inflate(R.layout.list_item_shot, parent, false);
        return new ShotViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {

        final Shot shot = getData().get(position);
        ShotViewHolder shotViewHolder = (ShotViewHolder) holder;
        //holder is recyclerView holder,  which is the parent of shot View holder.
        shotViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
        shotViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
        shotViewHolder.viewCount.setText(String.valueOf(shot.views_count));
        shotViewHolder.commentCount.setText(String.valueOf(shot.comments_count));

        ImageUtils.loadShotImage(shot, shotViewHolder.image);// fresco show picture!

        shotViewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start shot Activity;
                Intent intent = new Intent(getContext(), shotActivity.class);
                if (listType == ShotListFragment.LIST_TYPE_FOLLOWING_SHOT) {
                    String us = shotListFragment.getUS();
                    User u = ModelUtils.toObject(us,
                            new TypeToken<User>(){});
                    shot.user = u;
//                    intent.putExtra(ShotFragment.FOLLOWING_USER, us);
                }
                intent.putExtra(ShotFragment.KEY_SHOT,
                        ModelUtils.toString(shot, new TypeToken<Shot>() {
                        }));//数据转换 把shot转化成数据传到下个shotview 里;
                intent.putExtra(shotActivity.KEY_SHOT_TITLE, shot.title);
                shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_CODE_SHOT);

            }
        });
    }



}
