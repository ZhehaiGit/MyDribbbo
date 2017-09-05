package com.awesome.zhuzhehai.mydribbbo.view.bucket_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
//import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.model.Bucket;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.base.InfinitAdapter;
import com.awesome.zhuzhehai.mydribbbo.view.base.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BucketListFragment extends Fragment {



    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.recycler_view) RecyclerView recyclerView;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.fab) FloatingActionButton fab;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.swipe_refresh_container) SwipeRefreshLayout swipeRefreshLayout;

    public static final String KEY_CHOOSING_MODE = "choosing_mode";
    public static final String KEY_COLLECTED_BUCKET_IDS = "collected_bucket_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_CHOSEN_BUCKET_IDS = "chosen bucket ids";

    private BucketListAdapter adapter;

    private String userId;
    private boolean isChoosingMode;
    private Set<String> collectedBucketIdSet;


    public static final int REQ_CODE_NEW_BUCKET = 100;

    private InfinitAdapter.LoadMoreListener onLoadMore = new InfinitAdapter.LoadMoreListener() {
        @Override
        public void onLoadMore() {
            AsyncTaskCompat.executeParallel(new LoadBucketsTask(false));
        }
    };
    //加载false 说明是第一次加载不是刷新加载, True 说明是刷新加载。


    public static BucketListFragment newInstance(@Nullable String userId,
                                                 boolean isChoosingMode,
                                                 @Nullable ArrayList<String> chosenBucketIds) {
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        args.putBoolean(KEY_CHOOSING_MODE, isChoosingMode);
        args.putStringArrayList(KEY_COLLECTED_BUCKET_IDS, chosenBucketIds);
        BucketListFragment fragment = new BucketListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true); // 用infater menu 必须先加set
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.awesome.zhuzhehai.mydribbbo.R.layout.fragment_fab_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Bundle args = getArguments();
        userId = args.getString(KEY_USER_ID);
        isChoosingMode = args.getBoolean(KEY_CHOOSING_MODE);

//        data = Dribbble.getUserBuckets(userId, page);
        if (isChoosingMode) {
            // 得到args 里的那串标记为bucket_id的字符!
            List<String> chosenBucketIdList = args.getStringArrayList(KEY_COLLECTED_BUCKET_IDS);
            if(chosenBucketIdList != null) {
                collectedBucketIdSet = new HashSet<>(chosenBucketIdList);///???
            } else {
            collectedBucketIdSet = new HashSet<>();
            }
        } else {
            collectedBucketIdSet = new HashSet<>();
        }


        //set refresh!!
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new LoadBucketsTask(true)); // 多线程加载更多
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //set 间距
        recyclerView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(com.awesome.zhuzhehai.mydribbbo.R.dimen.spacing_medium)));

        adapter = new BucketListAdapter(getContext(),new ArrayList<Bucket>(),onLoadMore,isChoosingMode);

        recyclerView.setAdapter(adapter);


        //create new bucket using dialog
        //floating action bar;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Try replacing the root layout of R.layout.fragment_fab_recycler_view with
//                // FragmentLayout to see what Snackbar looks like
//                Snackbar.make(v, "Fab clicked", Snackbar.LENGTH_LONG).show();
                NewBucketDialogFragment dialogFragment = NewBucketDialogFragment.newInstance();
                dialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                dialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //save button in menu; 只有在choose mode 下才有。
        if (isChoosingMode) {
            inflater.inflate(com.awesome.zhuzhehai.mydribbbo.R.menu.bucket_list_choose_mode_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //点击save 之后
        if (item.getItemId() == com.awesome.zhuzhehai.mydribbbo.R.id.save) {
            ArrayList<String> chosenBucketIds = new ArrayList<>();
            for (Bucket bucket : adapter.getData()) {
                if (bucket.isChoosing) {
                    chosenBucketIds.add(bucket.id);
                }
            }
            //这个最后chosen 的结果, 返回到shot bucket!!!!!!!!!!
            // chosenBucketIDs 是user的buckert id;
            Intent result = new Intent();
            result.putStringArrayListExtra(KEY_CHOSEN_BUCKET_IDS, chosenBucketIds);
            getActivity().setResult(Activity.RESULT_OK, result);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK) {
            String bucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String bucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (!TextUtils.isEmpty(bucketName)) {
                // 新的bucket得到后 要刷新bucket 需要多线程
                AsyncTaskCompat.executeParallel(new NewBucketTask(bucketName, bucketDescription));
            }
        }
    }

    private class LoadBucketsTask extends DribbbleTask<Void, Void, List<Bucket>> {

        private boolean refresh;

        public LoadBucketsTask(boolean refresh) {
            this.refresh = refresh;
        }
        @Override
        protected List<Bucket> doJob(Void... params) throws DribbbleException {
            final int page = refresh ? 1 : adapter.getData().size() / Dribbble.COUNT_PER_LOAD + 1;
            return userId == null
                    ? Dribbble.getUserBuckets(page)
                    : Dribbble.getShotBuckets(userId, page);
        }

        @Override
        protected void onSuccess(List<Bucket> buckets) {
            adapter.setShowLoading(buckets.size() >= Dribbble.COUNT_PER_LOAD);

            for (Bucket bucket : buckets) {
                if (collectedBucketIdSet.contains(bucket.id)) {
                    bucket.isChoosing = true;
                }
            }
            if (refresh) {
                adapter.setData(buckets);
                swipeRefreshLayout.setRefreshing(false);
            } else {
                adapter.append(buckets);
            }
            swipeRefreshLayout.setEnabled(true);
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public class NewBucketTask extends DribbbleTask<Void, Void, Bucket>{
        private String name;
        private String description;

        private NewBucketTask(String name, String description) {
            this.name = name;
            this.description = description;
        }
        @Override
        protected Bucket doJob(Void... params) throws DribbbleException {
            return Dribbble.newBucket(name, description);// 向系统上传bucket 数据;
        }

        @Override
        protected void onSuccess(Bucket bucket) {
            bucket.isChoosing = true;
            //set new  bucket in top.  !! then refresh.
            //singleton list???????
            //"Returns an immutable list containing only the specified object
            adapter.prepend(Collections.singletonList(bucket));
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}
