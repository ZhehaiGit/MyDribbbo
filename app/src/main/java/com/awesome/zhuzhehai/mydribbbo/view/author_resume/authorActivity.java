package com.awesome.zhuzhehai.mydribbbo.view.author_resume;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.model.User;
//import Drippple.example.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.shot_list.ShotListFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 11/24/16.
 */
public class authorActivity extends AppCompatActivity {
    public static final String RESUME_TITLE = "title";
    public static final String SHOT_SHOT = "shot";
    public static final String SHOT_USER = "auther user";
    public static final String FOLLOWING_USER = "following user";
    public static final String FOLLOWING_ID = "following id";
    public final boolean r = true;
    public User user;
    public static final String KEY_FOLLOWING = "Key of following";
//    private Shot shot;
//    private String id;

    @BindView(R.id.author_resume_image) SimpleDraweeView autherImage;
    @BindView(R.id.author_resume_name)  TextView autherName;
    @BindView(R.id.author_bio)  TextView autherBio;
    @BindView(R.id.author_twitter)  TextView autherTwitter;
    @BindView(R.id.author_web)  TextView autherWeb;
    @BindView(R.id.author_location ) TextView autherLocation;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.author_shots_count) public TextView autherShotsCount;
    @BindView(R.id.author_likes_count ) public TextView autherLikesCount;
    @BindView(R.id.author_follower_count ) public TextView autherFollowerCount;
    @BindView(R.id.author_following_count ) public TextView autherFollowingCount;
    @BindView(R.id.author_team_count ) public TextView autherTeamCount;
    @BindView(R.id.fab_following ) public FloatingActionButton feb;

    private boolean isFollowing;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ModelUtils.toObject(getIntent().getExtras().getString(SHOT_USER),
                new TypeToken<User>(){});
        isFollowing = user.foled;
        setupResume();

    }

    private void setupResume() {
        setContentView(R.layout.author_resume);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (isBackEnabled()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getActivityTitle());

        autherName.setText(String.valueOf(user.name)+ " ("+ String.valueOf(user.username)+")");
        autherBio.setText(String.valueOf(user.bio));
        autherTwitter.setText(String.valueOf(user.links.twitter));
        autherTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(user.links.twitter); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        autherWeb.setText(String.valueOf(user.links.web));
        autherWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(user.links.web); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        autherLocation.setText(String.valueOf(user.location));
        autherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(authorActivity.this,user.name +"'s Location: " + user.location, Toast.LENGTH_SHORT).show();
            }
        });
        autherShotsCount.setText(String.valueOf(user.shots_count) + " shots");

        autherShotsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(authorActivity.this, authorShotActivity.class);
//                intent.putExtra(ShotListFragment.KEY_USER_ID, user);
                intent.putExtra(ShotListFragment.KEY_USER_ID,
                        ModelUtils.toString(user, new TypeToken<User>() {
                        }));
                intent.putExtra(authorShotActivity.KEY_USER_NAME, user.name);
                startActivity(intent);
//                ShotListFragment.newFollowingInstance(user.id);

                Toast.makeText(authorActivity.this,"Show "+  user.name +"'s shots list", Toast.LENGTH_SHORT).show();
            }
        });
        autherLikesCount.setText(String.valueOf(user.likes_count) + " likes");

        autherLikesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(authorActivity.this, authorlikesActivity.class);
//                intent.putExtra(ShotListFragment.KEY_USER_ID, user);
                intent.putExtra(ShotListFragment.KEY_USER_ID_LIKES,
                        ModelUtils.toString(user, new TypeToken<User>() {
                        }));
                intent.putExtra(authorlikesActivity.KEY_USER_NAME1, user.name);
                startActivity(intent);
                Toast.makeText(authorActivity.this,"Show "+  user.name +"'s likes list", Toast.LENGTH_SHORT).show();
            }
        });
        autherFollowerCount.setText(String.valueOf(user.followers_count) + " followers");
        autherFollowerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(authorActivity.this,user.name +" has "+ user.followers_count+" followers!", Toast.LENGTH_SHORT).show();
            }
        });
        autherFollowingCount.setText(String.valueOf(user.followings_count) + " followings");
        autherFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(authorActivity.this,user.name +" has "+ user.followings_count+" followers!", Toast.LENGTH_SHORT).show();

            }
        });
        autherTeamCount.setText(String.valueOf(user.teams_count)+ " teams");
        autherTeamCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(authorActivity.this,user.name +" has "+ user.teams_count+" teams!", Toast.LENGTH_SHORT).show();
            }
        });
        autherImage.setImageURI(Uri.parse(user.avatar_url));

        AsyncTaskCompat.executeParallel(new CheckFollowing(user.id));
    }

    public void setupFeb(boolean result) {
        isFollowing = result;
        if (isFollowing) {
            feb.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
        } else {
            feb.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        }
        feb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFollowing) {
                    Toast.makeText(authorActivity.this, "You are following "+ user.name+ " !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(authorActivity.this, "You are not following "+ user.name+ " !", Toast.LENGTH_SHORT).show();
                }
                isFollowing = !isFollowing;
                AsyncTaskCompat.executeParallel(new followingTask(user, isFollowing));
                setupFeb(isFollowing);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isBackEnabled() && item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isBackEnabled() {
        return true;
    }
    @NonNull
    protected String getActivityTitle() {
        return user.name;
    }


    private class followingTask extends DribbbleTask<Void, Void, Void> {


        private User user;
        private boolean isFollow;

        public followingTask(User user, boolean isFollowing) {
            this.user = user;
            this.isFollow = isFollowing;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            if (isFollow) {
                Dribbble.followingUser(user.id);
//                Toast.makeText(authorActivity.this, "You are following "+ user.name+ " !", Toast.LENGTH_SHORT);
            } else {
                Dribbble.unFollowingShot(user.id);
            }
            return null;
        }

        @Override
        protected void onSuccess(Void s) {

        }

        @Override
        protected void onFailed(DribbbleException e) {
        }
    }

    private class CheckFollowing extends DribbbleTask<Void, Void, Boolean> {

        private String id;
        public CheckFollowing(String id) {
            this.id = id;
        }
        @Override
        protected Boolean doJob(Void... params) throws DribbbleException {
            return Dribbble.isFollowingUser(id);
        }

        @Override
        protected void onSuccess(Boolean result) {
            user.foled = result;
            setupFeb(result);
        }

        @Override
        protected void onFailed(DribbbleException e) {
        }
    }
}
