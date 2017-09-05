package com.awesome.zhuzhehai.mydribbbo.view.following_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.os.AsyncTaskCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.model.Following;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.ImageUtils;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.author_resume.authorActivity;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class FollowingListAdapter extends InfinitAdapter<Following>{
    private final FollowingListFragment followingListFragment;

    public FollowingListAdapter (@NonNull FollowingListFragment followingListFragment,
                                 @NonNull List<Following> data,
                                 @NonNull LoadMoreListener loadMoreListener) {
        super(followingListFragment.getContext(), data, loadMoreListener);
        this.followingListFragment = followingListFragment;
    }
    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_following_user, parent, false);
        return new FollowingViewHolder(view);
    }
    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {

        final Following following = getData().get(position);

        FollowingViewHolder FollowingVh = (FollowingViewHolder) holder;
        FollowingVh.userName.setText(String.valueOf(following.followee.name));
        FollowingVh.userPrescribe.setText(Html.fromHtml(
                following.followee.bio == null ? "" : following.followee.bio));
        ImageUtils.loadUserPicture(getContext(),FollowingVh.image, following.followee.avatar_url);

        FollowingVh.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTaskCompat.executeParallel(new getSignleUser(following.followee.id));
            }
        });

    }
    public void authorResume(User user) {

        Intent intent = new Intent(getContext(), authorActivity.class);
        intent.putExtra(authorActivity.SHOT_USER,
                ModelUtils.toString(user, new TypeToken<User>() {
                }));
        intent.putExtra(authorActivity.FOLLOWING_USER, user.foled);
        intent.putExtra(authorActivity.RESUME_TITLE, user.name+"'s Resume)");
        followingListFragment.startActivityForResult(intent, FollowingListFragment.REQ_CODE_RESUME3);
    }
    private class getSignleUser extends DribbbleTask<Void,Void,User> {
        private String id;
        private getSignleUser(String id) {
            this.id = id;
        }
        @Override
        protected User doJob(Void... params) throws DribbbleException {
            return Dribbble.getSingleUser(id);
        }

        @Override
        protected void onSuccess(User user) {
            authorResume(user);
        }

        @Override
        protected void onFailed(DribbbleException e) {
        }
    }

}
