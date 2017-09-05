package com.awesome.zhuzhehai.mydribbbo.view.about_app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import Drippple.example.zhuzhehai.mydribbbo.R;

import com.awesome.zhuzhehai.mydribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 12/4/16.
 */
public class ahouAppFragment extends Fragment{
//    @BindView(R.id.recycler_view)
//    RecyclerView recyclerView;
//    @BindView(R.id.swipe_refresh_container)
//    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.paypal) TextView paypal;

    public static ahouAppFragment newInstance(@NonNull Bundle args) {
        ahouAppFragment fragment = new ahouAppFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.about_dripppel,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", "zhehaizhu@gmail.com");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),"Address Copied", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
