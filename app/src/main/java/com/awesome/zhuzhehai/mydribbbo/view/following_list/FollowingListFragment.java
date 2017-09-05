package com.awesome.zhuzhehai.mydribbbo.view.following_list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.awesome.zhuzhehai.mydribbbo.view.base.SpaceItemDecoration;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.model.Following;
//import Drippple.example.zhuzhehai.mydribbbo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 12/3/16.
 */

public class FollowingListFragment extends Fragment {
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;
    public static final int REQ_CODE_RESUME3 =212;
    private FollowingListAdapter adapter;

    private InfinitAdapter.LoadMoreListener onLoadMore = new InfinitAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (Dribbble.isLoggedIn()) {
                AsyncTaskCompat.executeParallel(new LoadFollowingTask(false));
            }
        }
    };


    public static FollowingListFragment newInstance(@NonNull Bundle args) {
        FollowingListFragment fragment = new FollowingListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_swipe_recycler_view,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadFollowingTask(true));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_medium)));

        adapter = new FollowingListAdapter(this, new ArrayList<Following>(), onLoadMore);
        recyclerView.setAdapter(adapter);

    }

    private class LoadFollowingTask extends DribbbleTask<Void,Void,List<Following>> {
        private boolean refresh;

        private LoadFollowingTask(boolean refresh) {
            this.refresh = refresh;
        }
        @Override
        protected List<Following> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return Dribbble.getFollowingUser(page);
        }

        @Override
        protected void onSuccess(List<Following> followings) {

            adapter.setShowLoading(followings.size() >= Dribbble.COUNT_PER_LOAD);
            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(followings);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(followings);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}