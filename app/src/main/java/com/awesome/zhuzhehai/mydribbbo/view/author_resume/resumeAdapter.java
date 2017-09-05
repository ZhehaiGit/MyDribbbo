package com.awesome.zhuzhehai.mydribbbo.view.author_resume;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.utils.ImageUtils;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class resumeAdapter extends RecyclerView.Adapter{

    private final resumeFragment resumf;
    private final User user;

    public resumeAdapter(@NonNull resumeFragment resumeF,
                         @NonNull User data) {
        this.resumf = resumeF;
        this.user = data;

    }
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_resume,parent,false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        resumeViewHolder resumeVH = (resumeViewHolder) holder;
        resumeVH.autherName.setText(String.valueOf(user.name));
        resumeVH.autherBio.setText(String.valueOf(user.bio));
        resumeVH.autherTwitter.setText(String.valueOf(user.links.twitter));
        resumeVH.autherLocation.setText(String.valueOf(user.location));
        resumeVH.autherWeb.setText(String.valueOf(user.links.web));

        resumeVH.autherShotsCount.setText(String.valueOf(user.shots_count));
        resumeVH.autherLikesCount.setText(String.valueOf(user.likes_count));
        resumeVH.autherFollowerCount.setText(String.valueOf(user.followers_count));
        resumeVH.autherFollowingCount.setText(String.valueOf(user.followings_count));
        resumeVH.autherTeamCount.setText(String.valueOf(user.teams_count));

        ImageUtils.loadUserPicture(getContext(),resumeVH.autherImage, user.avatar_url);


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private Context getContext() {
        return resumf.getContext();
    }
}
