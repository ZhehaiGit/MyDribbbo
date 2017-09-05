package com.awesome.zhuzhehai.mydribbbo.view.comments_list;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.ImageUtils;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.author_resume.authorActivity;
//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.Comment;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by zhuzhehai on 11/23/16.
 */
public class commentListAdapter extends InfinitAdapter<Comment> {
    private final commentListFrangement slf;
    private boolean isFollowing;
    private String id;
    public commentListAdapter(@NonNull commentListFrangement slf,
                           @NonNull List<Comment> data,
                           @NonNull LoadMoreListener loadMoreListener) {

        super(slf.getContext(), data, loadMoreListener);
        this.slf = slf;
    }
    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.list_item_comment, parent, false);
        return new commentViewHolder(view);
    }
    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, int position) {
        commentViewHolder commentViewH = (commentViewHolder) holder;

        final Comment comment = getData().get(position);
        isFollowing = comment.user.foled;
        id = comment.user.id;

        commentViewH.commenterName.setText(String.valueOf(comment.user.username)+"("+ String.valueOf(comment.user.name) +")" );
        commentViewH.comment.setText(Html.fromHtml(
                comment.body == null ? "" : comment.body));
        commentViewH.comment.setMovementMethod(LinkMovementMethod.getInstance());
        ImageUtils.loadUserPicture(getContext(),commentViewH.commenterImage, comment.user.avatar_url);

        commentViewH.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), authorActivity.class);
                intent.putExtra(authorActivity.SHOT_USER,
                        ModelUtils.toString(comment.user, new TypeToken<User>() {
                        }));
                intent.putExtra(authorActivity.FOLLOWING_USER, isFollowing);
                intent.putExtra(authorActivity.RESUME_TITLE, comment.user.name+"'s Resume)");
                slf.startActivityForResult(intent, commentListFrangement.REQ_CODE_RESUME);

            }
        });

    }


}

