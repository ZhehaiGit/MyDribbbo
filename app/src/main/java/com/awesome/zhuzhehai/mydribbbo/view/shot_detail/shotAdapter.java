package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

/**
 * Created by zhuzhehai on 11/4/16.
 */
public class shotAdapter extends RecyclerView.Adapter{
    private static final int view_image = 0;
    private static final int view_info = 1;

    private final ShotFragment shotFragment;
    private final Shot shot;

    public shotAdapter(@NonNull ShotFragment shotFragment, @NonNull Shot shot) {
        this.shotFragment = shotFragment;
        this.shot = shot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType) {
            case view_image: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shot_image,parent,false);
                return new ImageViewHolder (view);
            }
            case view_info: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shot_info,parent,false);
                return new InfoViewHolder(view);

            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        switch (viewType) {
            case view_image:
                //play gif automatically;
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                                    .setUri(Uri.parse(shot.getImageUrl()))
                                                    .setAutoPlayAnimations(true)
                                                    .build();
                ((ImageViewHolder) holder).image.setController(controller);
                break;

            case view_info:
                final InfoViewHolder shotDetailViewHolder = (InfoViewHolder) holder;
                shotDetailViewHolder.title.setText(shot.title);
                shotDetailViewHolder.authorName.setText(shot.user.name);

                shotDetailViewHolder.description.setText(Html.fromHtml(
                        shot.description == null ? "" : shot.description));
                shotDetailViewHolder.description.setMovementMethod(LinkMovementMethod.getInstance());

                shotDetailViewHolder.authorPicture.setImageURI(Uri.parse(shot.user.avatar_url));

                shotDetailViewHolder.authorPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.authorResume(shot.user);
                    }
                });

                shotDetailViewHolder.likeCount.setText(String.valueOf(shot.likes_count));
                shotDetailViewHolder.bucketCount.setText(String.valueOf(shot.buckets_count));
                shotDetailViewHolder.viewCount.setText(String.valueOf(shot.views_count));
                shotDetailViewHolder.commentCount.setText(String.valueOf(shot.comments_count));

                shotDetailViewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.like(shot.id, !shot.liked);
                    }
                });
                shotDetailViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shotFragment.share();
                    }
                });
                shotDetailViewHolder.bucketButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shotFragment.bucket();
                }
            });
                shotDetailViewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shotFragment.comments(shot.id);
//                        Toast toast = Toast.makeText(getContext(),"chengg",Toast.LENGTH_SHORT);
//                        toast.show();

                    }
                });


                Drawable likeDrawable = shot.liked
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_red_300_18dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_18dp);
                shotDetailViewHolder.likeButton.setImageDrawable(likeDrawable);


                Drawable bucketDrawable = shot.bucketed
                        ? ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_red_300_18dp)
                        : ContextCompat.getDrawable(getContext(), R.drawable.ic_inbox_black_18dp);
                shotDetailViewHolder.bucketButton.setImageDrawable(bucketDrawable);
                break;
        }
    }



    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return view_image;
        }
        return view_info;
    }
    @NonNull
    private Context getContext() {
        return shotFragment.getContext();
    }
}
