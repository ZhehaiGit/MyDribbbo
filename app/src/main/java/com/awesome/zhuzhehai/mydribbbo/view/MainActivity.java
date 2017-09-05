package com.awesome.zhuzhehai.mydribbbo.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.utils.ImageUtils;
import com.awesome.zhuzhehai.mydribbbo.view.bucket_list.BucketListFragment;
import com.awesome.zhuzhehai.mydribbbo.view.following_list.FollowingListFragment;
import com.awesome.zhuzhehai.mydribbbo.view.shot_list.ShotListFragment;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.view.about_app.ahouAppFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

//    @BindView(R.id.drawer_layout)
//    DrawerLayout drawerLayout;
//    @BindView(R.id.drawer) NavigationView navigationView;
//
//    private ActionBarDrawerToggle drawerToggle;
    private ListView mDrawerList;

     @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.drawer_layout) DrawerLayout drawerLayout;
     @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.drawer) NavigationView navigationView;


     @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.toolbar) Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;// 显示sandwich.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.awesome.zhuzhehai.mydribbbo.R.layout.activity_main);
//        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//三明治
        getSupportActionBar().setHomeButtonEnabled(true);

        setupDrawer();

        if (savedInstanceState == null) {
            //when entry, show shot list fragment
            // fragment manager .. show shot list fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR))
                    .commit();
            //传入layout 启动 shotListFragment.newInstance
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {//把箭头符号转换成三明治
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { // ????
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// show navigation draw when click sandwich
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,          /* DrawerLayout object */
                R.string.open_drawer,         /* "open drawer" description */
                R.string.close_drawer         /* "close drawer" description */
        );



        drawerLayout.setDrawerListener(drawerToggle);///!!!!!

        //View headerView = navigationView.getHeaderView(0);
        //headerView
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ((TextView) headerView.findViewById(R.id.user_name)).setText(
                Dribbble.getCurrentUser().name); // Dribble.getCrurrentUser name

        // load image
        ImageView userPicture = (ImageView) headerView.findViewById(R.id.user_image);
        //using Glide to get the image.
        ImageUtils.loadUserPicture(this, userPicture, Dribbble.getCurrentUser().avatar_url);
        // Dribble.getCrurrentUser image.

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }
                Fragment fragment = null;
                switch(item.getItemId()) {
                    case R.id.drawer_item_home: {
                        // if click item_ home show shot list;
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR);
                        setTitle(R.string.title_home);
                        break;
                    }
                    case R.id.drawer_item_RecentView: {
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_RECENTVIEW);
                        setTitle(R.string.title_recentShot);
                        break;
                    }
                    case R.id.drawer_item_mostView: {
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_MOSTVIEW);
                        setTitle(R.string.title_mostView);
                        break;
                    }
                    case R.id.drawer_item_mostComment: {
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_MOSTCOMMENT);
                        setTitle(R.string.title_mostComent);
                        break;
                    }
                    case R.id.drawer_item_animated: {
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_ANIMATED);
                        setTitle(R.string.title_animated);
                        break;
                    }
                    case R.id.drawer_item_likes: {
                        // if click item_ likes show likes fragment;
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_LIKED);
                        setTitle(R.string.title_likes);
                        break;
                    }
                    case R.id.drawer_item_following: {
                        // if click item_ likes show likes fragment;
                        fragment = FollowingListFragment.newInstance(null);
                        setTitle(R.string.title_following);
                        break;
                    }
                    case R.id.drawer_item_buckets: {
//                        Toast.makeText(MainActivity.this, " buckets clicked", Toast.LENGTH_LONG).show();

                        // if click item_ likes show buckets fragment;
                        fragment = BucketListFragment.newInstance(null, false, null);
                        setTitle(R.string.title_buckets);
                        break;
                    }
                    case R.id.nav_about_app: {
                        fragment = ahouAppFragment.newInstance(null);
                        break;
                    }
                    case R.id.log_out_btn: {
                        Dribbble.logout(MainActivity.this);
                        //intent is use to start activity;
                        //new intent (context, class)???
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        //intent is use to start activity;
                        //new intent (context, class)???

                        startActivity(intent);
                        finish();
                        break;
                    }
                }
                //return true;//show the item selected, if true! 否则 选择的时候 选择框不会动
                drawerLayout.closeDrawers();

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                    return true;
                }
                return true;
            }
        });
    }
}
