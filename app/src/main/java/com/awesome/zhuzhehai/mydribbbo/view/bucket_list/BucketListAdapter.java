package com.awesome.zhuzhehai.mydribbbo.view.bucket_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.Bucket;
import com.awesome.zhuzhehai.mydribbbo.view.base.BaseViewHolder;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.awesome.zhuzhehai.mydribbbo.view.shot_list.ShotListFragment;

import java.util.List;

public class BucketListAdapter extends InfinitAdapter<Bucket> {

    private boolean isChoosingMode;

    public BucketListAdapter(@NonNull Context context,
                             @NonNull List<Bucket> data,
                             @NonNull LoadMoreListener loadMoreListener,
                             boolean isChoosingMode) {
        super(context, data, loadMoreListener);
        this.isChoosingMode = isChoosingMode;
    }

    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.list_item_bucket, parent, false);
        return new BucketViewHolder(view);
    }

    @Override
    protected void onBindItemViewHolder(BaseViewHolder holder, final int position) {
        final Bucket bucket = getData().get(position);
        final BucketViewHolder bucketViewHolder = (BucketViewHolder) holder;

        bucketViewHolder.bucketName.setText(bucket.name);
        bucketViewHolder.bucketShotCount.setText(formatShotCount(bucket.shots_count));
        if (isChoosingMode) {
            bucketViewHolder.bucketChosen.setVisibility(View.VISIBLE);// set可见
            bucketViewHolder.bucketChosen.setImageDrawable(
                    bucket.isChoosing
                            ? ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_black_24dp)
                            : ContextCompat.getDrawable(getContext(), R.drawable.ic_check_box_outline_blank_black_24dp)
            );
            //设置点击结果
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bucket.isChoosing = !bucket.isChoosing;
                    notifyItemChanged(position);//刷新结果
                }
            });
        } else {
            bucketViewHolder.bucketChosen.setVisibility(View.GONE);
            bucketViewHolder.bucketLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), BucketShotListActivity.class);
                    intent.putExtra(ShotListFragment.KEY_BUCKET_ID, bucket.id);
                    intent.putExtra(BucketShotListActivity.KEY_BUCKET_NAME, bucket.name);
                    getContext().startActivity(intent);
                }
            });
        }
        return;
    }

    @Override
    protected BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    private String formatShotCount(int shotCount) {
        return shotCount == 0
                ? getContext().getString(R.string.shot_count_single, shotCount)
                : getContext().getString(R.string.shot_count_plural, shotCount);
    }
}
