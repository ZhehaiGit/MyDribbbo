package com.awesome.zhuzhehai.mydribbbo.view.author_resume;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;

import com.google.gson.reflect.TypeToken;

import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class resumeFragment extends Fragment{
    public static final String SHOT_USER = "author";
    public static resumeAdapter adapter;

    private static User user;

    public static resumeFragment newInstance(@NonNull Bundle args) {
        resumeFragment fragment = new resumeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_resume,container,false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        user = ModelUtils.toObject(getArguments().getString(SHOT_USER),
                new TypeToken<User>(){});
        adapter = new resumeAdapter(this, user);
        super.onViewCreated(view, savedInstanceState);
    }
}
