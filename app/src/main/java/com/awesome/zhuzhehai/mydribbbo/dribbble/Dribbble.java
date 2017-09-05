package com.awesome.zhuzhehai.mydribbbo.dribbble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.awesome.zhuzhehai.mydribbbo.model.Following;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.model.Bucket;
import com.awesome.zhuzhehai.mydribbbo.model.Comment;
import com.awesome.zhuzhehai.mydribbbo.model.Like;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dribbble {

    private static final String TAG = "Dribbble API";

    public static final int COUNT_PER_LOAD = 12;

    private static final String API_URL = "https://api.dribbble.com/v1/";

    private static final String USER_END_POINT = API_URL + "user";
    private static final String USERS_END_POINT = API_URL + "users";
    private static final String SHOTS_END_POINT = API_URL + "shots";
    private static final String BUCKETS_END_POINT = API_URL + "buckets";

    private static final String SP_AUTH = "auth";

    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_NAME = "name";
    private static final String KEY_SHOT_ID = "shot_id";
    private static final String KEY_USER = "user";

    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<Shot> SHOT_TYPE = new TypeToken<Shot>(){};
    private static final TypeToken<List<Shot>> SHOT_LIST_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<Bucket> BUCKET_TYPE = new TypeToken<Bucket>(){};
    private static final TypeToken<List<Bucket>> BUCKET_LIST_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<Like> LIKE_TYPE = new TypeToken<Like>(){};
    private static final TypeToken<List<Comment>> COMMENTS_LIST_TYPE = new TypeToken<List<Comment>>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};
    private static final TypeToken<List<User>> USER_LIST_TYPE = new TypeToken<List<User>>(){};
    private static final TypeToken<List<Following>> FOLLOWING_LIST_TYPE = new TypeToken<List<Following>>(){};

    private static OkHttpClient client = new OkHttpClient();

    private static String accessToken;
    private static User user;

    private static Request.Builder authRequestBuilder(String url) {
        return new Request.Builder()//method is "GET"
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);//确定url 有效?
    }
        // 为什么throws DribbbleException ???????????????
    private static Response makeRequest(Request request) throws DribbbleException {
        try {
            Response response = client.newCall(request).execute(); // 向网站发送请求(OkHttpClient)
            Log.d(TAG, response.header("X-RateLimit-Remaining"));
            return response;
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }
    }

    private static Response makeGetRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url).build(); //通过authRequest build request;
        return makeRequest(request);// 通过request 向网站发送请求(makeRequest) 得到response;
    }

    private static Response makePostRequest(String url,
                                            RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makePutRequest(String url,
                                           RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .put(requestBody)
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete()
                .build();
        return makeRequest(request);
    }

    private static Response makeDeleteRequest(String url,
                                              RequestBody requestBody) throws DribbbleException {
        Request request = authRequestBuilder(url)
                .delete(requestBody)
                .build();
        return makeRequest(request);
    }
    //解析网站传回的数据 得到需要的data. 传入 response 和 type
    private static <T> T parseResponse(Response response,
                                       TypeToken<T> typeToken) throws DribbbleException {

        String responseString;
        try {
            responseString = response.body().string();//get string
        } catch (IOException e) {
            throw new DribbbleException(e.getMessage());
        }

        Log.d(TAG, responseString);

        try {
            return ModelUtils.toObject(responseString, typeToken);//string to object
        } catch (JsonSyntaxException e) {
            throw new DribbbleException(responseString);
        }
    }

    private static void checkStatusCode(Response response,
                                        int statusCode) throws DribbbleException {
        if (response.code() != statusCode) {
            throw new DribbbleException(response.message());
        }
    }

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static boolean isLoggedIn() {
        return accessToken != null;
    }

    public static void login(@NonNull Context context,
                             @NonNull String accessToken) throws DribbbleException {

        Dribbble.accessToken = accessToken;
        // when log in  store accessToken and user
        storeAccessToken(context, accessToken);

        Dribbble.user = getUser();
        storeUser(context, user);
    }

    public static void logout(@NonNull Context context) {
        //clear accessToken and user
        storeAccessToken(context, null);
        storeUser(context, null);

        accessToken = null;
        user = null;
    }

    public static User getCurrentUser() {
        return user;
    }

    public static User getUser() throws DribbbleException {
        return parseResponse(makeGetRequest(USER_END_POINT), USER_TYPE);
    }

    public static List<Like> getLikes(int page) throws DribbbleException {
        //用户的likes.
        String url = USER_END_POINT + "/likes?page=" + page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }

    public static List<Shot> getLikedShots(int page) throws DribbbleException {
        List<Like> likes = getLikes(page);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }
    public static List<Shot> getUserLikedShots(int page, String userID) throws DribbbleException {
        List<Like> likes = getFollowingLikes(page, userID);
        List<Shot> likedShots = new ArrayList<>();
        for (Like like : likes) {
            likedShots.add(like.shot);
        }
        return likedShots;
    }
//    public static List<Like> getFollowingLikesShot(int page, String userID) throws DribbbleException {
//        //用户的likes.
//        String url = USER_END_POINT + "/likes?page=" + page;
//        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
//    }
    public static List<Like> getFollowingLikes(int page, String userID) throws DribbbleException {
        String url = USERS_END_POINT + "/" +userID+"/likes?page="+page;
        return parseResponse(makeGetRequest(url), LIKE_LIST_TYPE);
    }
    public static List<Following> getFollowingUser(int page) throws DribbbleException {
        String url = USER_END_POINT + "/following?page="+page;
        return parseResponse(makeGetRequest(url), FOLLOWING_LIST_TYPE);
    }
    public static List<Shot> getShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);//接续返回数据得到object shot;
    }
    public static User getSingleUser(String  id) throws DribbbleException {
        String url = USERS_END_POINT +"/"+id;
        return parseResponse(makeGetRequest(url), USER_TYPE);//接续返回数据得到object shot;
    }

    public static List<Shot> getRecentViewShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT +"?sort=" + "recent&"+ "page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostViewShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT +"?sort=" + "views&"+ "page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getMostCommentShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT +"?sort=" + "comments&"+ "page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getAnimatedShots(int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "?list=" + "animated&sort=recent&page=" + page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }
    public static List<Shot> getFollowingShot(int page, String userID) throws DribbbleException {
        String url = USERS_END_POINT + "/" +userID+"/shots?page="+page;
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

//    public static List<Shot> getFollowingLikesShot(int page, String userID) throws DribbbleException {
//        String url = USERS_END_POINT + "/" +userID+"/likes?page="+page;
//        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
//    }

    public static List<Comment> getComments(String id, int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id +"/comments?page=" + page;
        return parseResponse(makeGetRequest(url), COMMENTS_LIST_TYPE);
    }

    public static Shot getShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id;
        return parseResponse(makeGetRequest(url), SHOT_TYPE);
    }

    //post like; 向Dribbble发送信息 这个user 喜欢shot
    public static Like likeShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        //make post request!!! 就是把本地数据post到网络数据
        Response response = makePostRequest(url, new FormBody.Builder().build());
        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);

        return parseResponse(response, LIKE_TYPE);
    }
    public static void followingUser(@NonNull String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id + "/follow";
        //make post request!!! 就是把本地数据post到网络数据
        Response response = makePutRequest(url, new FormBody.Builder().build());
        checkStatusCode(response, HttpURLConnection.HTTP_CREATED);
        return;
    }

    public static void unlikeShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        //detete the userId under this shot id's like
        //delete request!!!
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }
    public static void unFollowingShot(@NonNull String id) throws DribbbleException {
        String url = USERS_END_POINT + "/" + id + "/follow";
        //detete the userId under this shot id's like
        //delete request!!!
        Response response = makeDeleteRequest(url);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    //check whether user like the shot!
    public static boolean isLikingShot(@NonNull String id) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + id + "/like";
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_OK:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }
    public static boolean isFollowingUser(@NonNull String id) throws DribbbleException {
        String url = USER_END_POINT + "/following/" + id;
        Response response = makeGetRequest(url);
        switch (response.code()) {
            case HttpURLConnection.HTTP_NO_CONTENT:
                return true;
            case HttpURLConnection.HTTP_NOT_FOUND:
                return false;
            default:
                throw new DribbbleException(response.message());
        }
    }

    public static List<Bucket> getUserBuckets(int page) throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * Will return all the buckets for the logged in user
     * @return
     * @throws DribbbleException
     */
    public static List<Bucket> getUserBuckets() throws DribbbleException {
        String url = USER_END_POINT + "/" + "buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getUserBuckets(@NonNull String userId,
                                              int page) throws DribbbleException {
        String url = USERS_END_POINT + "/" + userId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static List<Bucket> getShotBuckets(@NonNull String shotId,
                                              int page) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?page=" + page;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    /**
     * Will return all the buckets for a certain shot
     * @param shotId
     * @return
     * @throws DribbbleException
     */
    public static List<Bucket> getShotBuckets(@NonNull String shotId) throws DribbbleException {
        String url = SHOTS_END_POINT + "/" + shotId + "/buckets?per_page=" + Integer.MAX_VALUE;
        return parseResponse(makeGetRequest(url), BUCKET_LIST_TYPE);
    }

    public static Bucket newBucket(@NonNull String name,
                                   @NonNull String description) throws DribbbleException {
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, name)
                .add(KEY_DESCRIPTION, description)
                .build();
        return parseResponse(makePostRequest(BUCKETS_END_POINT, formBody), BUCKET_TYPE);
    }

    public static void addBucketShot(@NonNull String bucketId,
                                     @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makePutRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static void removeBucketShot(@NonNull String bucketId,
                                        @NonNull String shotId) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID, shotId)
                .build();

        Response response = makeDeleteRequest(url, formBody);
        checkStatusCode(response, HttpURLConnection.HTTP_NO_CONTENT);
    }

    public static List<Shot> getBucketShots(@NonNull String bucketId,
                                              int page) throws DribbbleException {
        String url = BUCKETS_END_POINT + "/" + bucketId + "/shots";
        return parseResponse(makeGetRequest(url), SHOT_LIST_TYPE);
    }

    public static void storeAccessToken(@NonNull Context context, @Nullable String token) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    public static String loadAccessToken(@NonNull Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static void storeUser(@NonNull Context context, @Nullable User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    public static User loadUser(@NonNull Context context) {
        return ModelUtils.read(context, KEY_USER, new TypeToken<User>(){});
    }

}
