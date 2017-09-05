package com.awesome.zhuzhehai.mydribbbo.view.comments_list;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.model.Comment;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.awesome.zhuzhehai.mydribbbo.view.base.SpaceItemDecoration;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class commentListFrangement extends Fragment {
//    public static final String KEY_LIST_C_TYPE = "listType";
//    public static final int LIST_TYPE_COMMENT = 100;
//    private int listType;
    public static final String  SHOT_ID = "shot id";
    public static final int REQ_CODE_RESUME = 100;
    private String commentID;
    private Comment comment;

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.recycler_view2) RecyclerView recyclerView;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.swipe_refresh_container2) SwipeRefreshLayout swipeRefreshLayout;

    private commentListAdapter adapter;
    private InfinitAdapter.LoadMoreListener onLoadMore = new InfinitAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadCommentTask(false));
        }
    };
    public  static commentListFrangement newInstance(@Nullable String commentId) {
        Bundle args = new Bundle();
        args.putString(SHOT_ID, commentId);
        commentListFrangement fragment = new commentListFrangement();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.awesome.zhuzhehai.mydribbbo.R.layout.comment_list_recycle, container, false);
        ButterKnife.bind(this, view); //bind(target, source)
        return view;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Bundle args = getArguments();
        commentID = args.getString(SHOT_ID);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadCommentTask(true));
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(com.awesome.zhuzhehai.mydribbbo.R.dimen.comment_spacing_small)));

        adapter = new commentListAdapter(this,new ArrayList<Comment>(),onLoadMore);
        recyclerView.setAdapter(adapter);


    }

    private class LoadCommentTask extends DribbbleTask<Void, Void,List<Comment>> {
        private boolean refresh;

        private LoadCommentTask(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        protected List<Comment> doJob(Void... params) throws DribbbleException {
            int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            //得到一个共同的bucketID;
            //shot里记录的bucket
            return Dribbble.getComments(commentID, page);
        }

        @Override
        protected void onSuccess(List<Comment> commts) {
            adapter.setShowLoading(commts.size() >= Dribbble.COUNT_PER_LOAD);
            if (refresh) {
                swipeRefreshLayout.setRefreshing(false);
                adapter.setData(commts);
            } else {
                swipeRefreshLayout.setEnabled(true);
                adapter.append(commts);
            }
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            //snackbar will show a brief message at the bottom of the screen
        }
    }

}
