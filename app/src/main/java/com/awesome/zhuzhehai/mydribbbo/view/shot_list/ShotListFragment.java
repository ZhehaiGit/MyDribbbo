package com.awesome.zhuzhehai.mydribbbo.view.shot_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.model.User;
//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.base.SpaceItemDecoration;
import com.awesome.zhuzhehai.mydribbbo.view.shot_detail.ShotFragment;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShotListFragment extends Fragment {
    public static final int REQ_CODE_SHOT = 100;
    private static final int COUNT_PAGE = 20;
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final String KEY_USER_ID = "USERId";
    public static final String KEY_USER_ID_LIKES ="use likes";
    private ShotListAdapter adapter;
    private int listType;
    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;
    public static final int LIST_TYPE_RECENTVIEW = 4;
    public static final int LIST_TYPE_ANIMATED = 5;
    public static final int LIST_TYPE_MOSTVIEW = 6;
    public static final int LIST_TYPE_MOSTCOMMENT = 7;
    public static final int LIST_TYPE_FOLLOWING_SHOT = 8;
    public static final int LIST_TYPE_FOLLOWING_LIKES_SHOT = 9;
//    public static final String u = null;
    public User u;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public static ShotListFragment newInstance(int listType) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, listType); // map String "listType", int listType;

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static ShotListFragment newBucketListInstance(@NonNull String bucketId) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_BUCKET);
        args.putString(KEY_BUCKET_ID, bucketId);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);

        return fragment;
    }
    public static ShotListFragment newFollowingInstance(@NonNull String userIds) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_FOLLOWING_SHOT);
        args.putString(KEY_USER_ID, userIds);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);

        return fragment;
    }
    public static ShotListFragment newFollowingLikesInstance(@NonNull String userString) {
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, LIST_TYPE_FOLLOWING_LIKES_SHOT);
        args.putString(KEY_USER_ID_LIKES, userString);

        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //本地数据更新???
        if (requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK) {
            Shot updatedShot = ModelUtils.toObject(data.getStringExtra(ShotFragment.KEY_SHOT),
                    new TypeToken<Shot>(){});
            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, updatedShot.id)) {
                    shot.likes_count = updatedShot.likes_count;
                    shot.buckets_count = updatedShot.buckets_count;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe_recycler_view, container, false);
        ButterKnife.bind(this, view); //bind(target, source)
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listType = getArguments().getInt(KEY_LIST_TYPE);
        //set refresh at first false;
        swipeRefreshLayout.setEnabled(false);
        //when loadShotsTask is true, gte more data using AsyncTask.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadShotsTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new ShotListAdapter(this, new ArrayList<Shot>(), listType,new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (Dribbble.isLoggedIn()) {
                    AsyncTaskCompat.executeParallel(new LoadShotsTask(false));
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }
    public String getUS() {
        return getArguments().getString(KEY_USER_ID);
    }
    private class LoadShotsTask extends DribbbleTask<Void, Void, List<Shot>> {
        //DribbleTask is abstract class;
        private boolean refresh;

        private LoadShotsTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            switch (listType) {
                case LIST_TYPE_POPULAR://主界面 get shots
                    return Dribbble.getShots(page);
                case LIST_TYPE_LIKED:// get user's likes
                    return Dribbble.getLikedShots(page);
                case LIST_TYPE_BUCKET:// get user's buckets.
                    String bucketId = getArguments().getString(KEY_BUCKET_ID);
                    return Dribbble.getBucketShots(bucketId, page);
                case LIST_TYPE_RECENTVIEW:
                    return Dribbble.getRecentViewShots(page);
                case LIST_TYPE_MOSTVIEW:
                    return Dribbble.getMostViewShots(page);
                case LIST_TYPE_MOSTCOMMENT:
                    return Dribbble.getMostCommentShots(page);
                case LIST_TYPE_ANIMATED:
                    return Dribbble.getAnimatedShots(page);
                case LIST_TYPE_FOLLOWING_SHOT:
                     u = ModelUtils.toObject(getArguments().getString(KEY_USER_ID),
                            new TypeToken<User>(){});
                    return Dribbble.getFollowingShot(page, u.id);
                case LIST_TYPE_FOLLOWING_LIKES_SHOT:
                    u = ModelUtils.toObject(getArguments().getString(KEY_USER_ID_LIKES),
                            new TypeToken<User>(){});
                    return Dribbble.getUserLikedShots(page, u.id);
                default:
                    return Dribbble.getShots(page);
            }
        }

        @Override
        protected void onSuccess(List<Shot> shots) {
            adapter.setShowLoading(shots.size() >= Dribbble.COUNT_PER_LOAD);

            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(shots);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(shots);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            //snackbar will show a brief message at the bottom of the screen
        }

    }

}
