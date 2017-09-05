package com.awesome.zhuzhehai.mydribbbo.view.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;

import java.util.List;

/**
 * Created by zhuzhehai on 11/6/16.
 */
public abstract class InfinitAdapter<M> extends RecyclerView.Adapter<BaseViewHolder>{
    private List<M> data;
    private boolean showLoading;
    private final Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOADING = 1;
    private LoadMoreListener loadMoreListener;

    public InfinitAdapter(@NonNull Context context, @NonNull List<M> data , @NonNull LoadMoreListener loadMoreListener) {

        this.context = context;
        this.data = data;
        this.loadMoreListener = loadMoreListener;
        showLoading = true;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_loading, parent, false);
            return new BaseViewHolder(view);//recycle View Holde
        }else {
            return onCreateItemViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == TYPE_ITEM) {
            onBindItemViewHolder(holder, position);
        } else {
            loadMoreListener.onLoadMore();
        }
    }

    protected abstract void onBindItemViewHolder(BaseViewHolder holder, int position);

    @Override
    //set count
    public int getItemCount() {
        return showLoading? data.size()+1: data.size();
    }

    @Override
    //set view type
    public int getItemViewType(int position) { //position ???
        if (showLoading) {
            return position == data.size() ? TYPE_LOADING : TYPE_ITEM;
        } else {
            return TYPE_ITEM;
        }
    }

    //set whether loading
    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }
    //add data;
    public void append (@NonNull List<M>moreShots) {
        data.addAll(moreShots);
        notifyDataSetChanged();
    }
    //数据加到最前面
    public void prepend(@NonNull List<M> data) {
        this.data.addAll(0, data);
        notifyDataSetChanged();
    }
    public void setData(@NonNull List<M> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    //Interface loading more!!!!!!!!!
    //!!!!!!!!!!!!!!!!!!!!!!!!!!
    public interface LoadMoreListener {
        void onLoadMore();
    }

    protected abstract BaseViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public List<M> getData() {
        return data;
    }
    protected Context getContext() {
        return context;
    }
}

