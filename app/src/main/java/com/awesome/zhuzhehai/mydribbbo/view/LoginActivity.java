package com.awesome.zhuzhehai.mydribbbo.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.dribbble.auth.Auth;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.dribbble.auth.AuthActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.activity_login_btn) TextView loginBtn;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.activity_signUp_btn) TextView signUpBtn;
    @BindView(com.awesome.zhuzhehai.mydribbbo.R.id.activity_login) View login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.awesome.zhuzhehai.mydribbbo.R.layout.activity_log_in);
        ButterKnife.bind(this);

        // load access token from shared preference
        Dribbble.init(this);

        //set log in button click action
        if (!Dribbble.isLoggedIn()) {
            // if did not have access token, start Auth activity to get an access token;
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Auth.openAuthActivity(LoginActivity.this);// get author code which used to exchange access token;
                }
            });
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("https://dribbble.com/signup/new"));
                    startActivity(intent);
                }
            });
        } else {
            //if have access token, just start MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Auth.REQ_CODE && resultCode == RESULT_OK) {
            final String authCode = data.getStringExtra(AuthActivity.KEY_CODE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = Auth.fetchAccessToken(authCode); //exchange for access Token
                        Dribbble.login(LoginActivity.this, token);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException | DribbbleException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
