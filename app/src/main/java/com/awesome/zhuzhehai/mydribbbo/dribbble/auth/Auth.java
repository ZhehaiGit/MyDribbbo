package com.awesome.zhuzhehai.mydribbbo.dribbble.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Auth {

    public static final int REQ_CODE = 100;

    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String CLIENT_ID = "59a98f85d58095ccfbfe3c9ca4c2e7a552f55c59b2dd4b719d703c346c11efb0";
    private static final String CLIENT_SECRET = "477f02bd1ea9d9617abccab17e3d16f8d1f0d4613424d0fc29257ae2670e39cc";

    private static final String SCOPE = "public+write";

    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    public static final String REDIRECT_URI = "http://www.dribop.com";
    private static String getAuthorizeUrl() { //get loading page URL
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();
        // fix encode issue
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;

        return url;
    }

    private static String getTokenUrl(String authCode) {
        return Uri.parse(URI_TOKEN)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .appendQueryParameter(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .appendQueryParameter(KEY_CODE, authCode)
                .appendQueryParameter(KEY_REDIRECT_URI, REDIRECT_URI)
                .build()
                .toString();
    }

    public static void openAuthActivity(@NonNull Activity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL, getAuthorizeUrl());// 官方登入界面//send back the author URL
        activity.startActivityForResult(intent, REQ_CODE);// 返回一个临时code;
    }

    public static String fetchAccessToken(String authCode) //Using auth code exchange access token.
            throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody postBody = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, authCode)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();
        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(postBody)
                .build();
        Response response = client.newCall(request).execute();//execute post to get access token;

        String responseString = response.body().string();//得到response 字符串 是Json String
        try {
            JSONObject obj = new JSONObject(responseString);//string to Json object
            return obj.getString(KEY_ACCESS_TOKEN);//
        } catch (JSONException e) { //java 异常处理 网络强求 文件读写都会出现异常
            e.printStackTrace();
            return "";
        }
    }

}
