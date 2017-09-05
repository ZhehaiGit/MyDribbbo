package com.awesome.zhuzhehai.mydribbbo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;


public class ImageUtils {

    public static void loadUserPicture(@NonNull final Context context,
                                       @NonNull ImageView imageView,
                                       @NonNull String url) {
        Glide.with(context)
             .load(url)
             .asBitmap()
             .placeholder(ContextCompat.getDrawable(context, R.drawable.user_picture_placeholder))
             .into(new BitmapImageViewTarget(imageView) {
                 @Override
                 protected void setResource(Bitmap resource) {
                     RoundedBitmapDrawable circularBitmapDrawable =
                             RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                     circularBitmapDrawable.setCircular(true);
                     view.setImageDrawable(circularBitmapDrawable);
                 }
             });
    }

    public static void loadShotImage(@NonNull Shot shot, @NonNull SimpleDraweeView imageView) {
        String imageUrl = shot.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            Uri imageUri = Uri.parse(imageUrl);
            if (shot.animated) {
                GenericDraweeHierarchy hierarchy = imageView.getHierarchy();
                ProgressBarDrawable progressBar =  new ProgressBarDrawable();
//                progressBar.setBackgroundColor(R.color.colorChoice1Primary);

                progressBar.setColor(R.color.colorChoice1Primary);
//                progressBar.setBarWidth(R.dimen.activity_auth_progress_bar_height);

                hierarchy.setProgressBarImage(progressBar);

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                                    .setUri(imageUri)
                                                    .setAutoPlayAnimations(true)
                                                    .build();
                imageView.setController(controller);
            } else {
                imageView.setImageURI(imageUri);
            }
        }
    }
}
