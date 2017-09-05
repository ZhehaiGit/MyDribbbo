package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.awesome.zhuzhehai.mydribbbo.R;
import com.awesome.zhuzhehai.mydribbbo.dribbble.Dribbble;
import com.awesome.zhuzhehai.mydribbbo.dribbble.DribbbleException;
import com.awesome.zhuzhehai.mydribbbo.model.Bucket;
import com.awesome.zhuzhehai.mydribbbo.model.Shot;
import com.awesome.zhuzhehai.mydribbbo.model.User;
import com.awesome.zhuzhehai.mydribbbo.utils.CapturePhotoUtils;
import com.awesome.zhuzhehai.mydribbbo.utils.ModelUtils;
import com.awesome.zhuzhehai.mydribbbo.view.author_resume.authorActivity;
import com.awesome.zhuzhehai.mydribbbo.view.base.DribbbleTask;
import com.awesome.zhuzhehai.mydribbbo.view.bucket_list.BucketListActivity;
import com.awesome.zhuzhehai.mydribbbo.view.bucket_list.BucketListFragment;
import com.awesome.zhuzhehai.mydribbbo.view.comments_list.CommentListActivity;
import com.awesome.zhuzhehai.mydribbbo.view.comments_list.commentListFrangement;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhuzhehai on 11/4/16.
 */
public class ShotFragment extends Fragment{
    public static final String KEY_SHOT = "shot";
//    public static final String FOLLOWING_USER = "FOLLOWING_USER";
    private static final int REQ_CODE_RESUME =103;
    private static final int REQ_CODE_BUCKET = 100;
    private static final int REQ_CODE_COMMENT =101;
    public static final int REQ_CODE_NEW_IMAGE= 104;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private boolean isLiking;
    private boolean isFollowing;
    private Shot shot;
    private ArrayList<String> collectedBucketIds;


    public static ShotFragment newInstance(@NonNull Bundle args) {
        ShotFragment fragment = new ShotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        shot = ModelUtils.toObject(getArguments().getString(KEY_SHOT),
                new TypeToken<Shot>(){});
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new shotAdapter(this, shot));

//        isLiking = true;
        isLiking = true;
        isFollowing = true;
        //在创建view 的时候  去判断user 是否like this shot;
        AsyncTaskCompat.executeParallel(new CheckLikeTask());
        //在创建view 的时候  去判断user 是否bucket this shot;
//        AsyncTaskCompat.executeParallel(new CheckFollowing());
        AsyncTaskCompat.executeParallel(new LoadBucketsTask());

        super.onViewCreated(view, savedInstanceState);
    }

    public void like(String shotId, boolean like) {
        if (!isLiking) {
            isLiking = true;
            AsyncTaskCompat.executeParallel(new LikeTask(shotId, like));
        }
    }

    public void authorResume(User u) {
        Intent intent = new Intent(getContext(), authorActivity.class);
        intent.putExtra(authorActivity.SHOT_USER,
                ModelUtils.toString(u, new TypeToken<User>() {
                }));
        intent.putExtra(authorActivity.FOLLOWING_USER, u.foled);
        intent.putExtra(authorActivity.RESUME_TITLE, shot.user.name+"'s Resume)");
        startActivityForResult(intent, REQ_CODE_RESUME);
    }
    public void comments(String shotId) {
        Intent intent = new Intent(getContext(), CommentListActivity.class);
        intent.putExtra(commentListFrangement.SHOT_ID, shotId);
        intent.putExtra(CommentListActivity.COMMENT_TITLE, shot.title+"(Comments)");
        startActivityForResult(intent, REQ_CODE_COMMENT);
    }
    public void bucket() {
        Intent intent = new Intent(getContext(), BucketListActivity.class);
        intent.putExtra(BucketListFragment.KEY_CHOOSING_MODE, true);
        intent.putStringArrayListExtra(BucketListFragment.KEY_COLLECTED_BUCKET_IDS,
                collectedBucketIds);
        startActivityForResult(intent, REQ_CODE_BUCKET);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_BUCKET && resultCode == Activity.RESULT_OK) {
            List<String> chosenBucketIds = data.getStringArrayListExtra(
                    BucketListFragment.KEY_CHOSEN_BUCKET_IDS);
            List<String> addedBucketIds = new ArrayList<>();
            List<String> removedBucketIds = new ArrayList<>();
            //对所有buckets 过滤一遍 看是否是选择了的 对的上的说明选择了
            for (String chosenBucketId : chosenBucketIds) {
                if (!collectedBucketIds.contains(chosenBucketId)) {
                    addedBucketIds.add(chosenBucketId);
                }
            }

            for (String collectedBucketId : collectedBucketIds) {
                if (!chosenBucketIds.contains(collectedBucketId)) {
                    removedBucketIds.add(collectedBucketId);
                }
            }
            //需要新线程去更新数据
            AsyncTaskCompat.executeParallel(new UpdateBucketTask(addedBucketIds, removedBucketIds));
        }
        if (requestCode == REQ_CODE_NEW_IMAGE && resultCode == Activity.RESULT_OK) {
            String imageName = data.getStringExtra(ImageDownLoadFragment.KEY_IMAGE_NAME);
            String fileName = data.getStringExtra(ImageDownLoadFragment.KEY_IMAGE_FILE);
//            Bitmap bit =getBitmapFromURL(shot.getImageUrl());
            if (!TextUtils.isEmpty(imageName)) {
                AsyncTaskCompat.executeParallel( new GetImages(shot.getImageUrl(), imageName, fileName));
            }
        }
        if (requestCode == REQ_CODE_RESUME && resultCode == Activity.RESULT_OK) {
            Boolean result = data.getBooleanExtra(authorActivity.KEY_FOLLOWING,false);
            shot.user.foled = result;
        }
    }

    public class GetImages extends DribbbleTask<Void, Void, Bitmap> {
        private String requestUrl, imageName_, dictName;
        public GetImages(String requestUrl, String _imageName_,String DictName) {
            this.requestUrl = requestUrl;
            this.imageName_ = _imageName_ ;
            this.dictName = DictName;
        }
        @Override
        protected Bitmap doJob(Void... params) throws DribbbleException {
            return getBitmapFromURL(requestUrl);
        }
        @Override
        protected void onSuccess(Bitmap bitmap) {
            CapturePhotoUtils.insertImage(getContext().getContentResolver(),bitmap,imageName_, dictName);
        }

        @Override
        protected void onFailed(DribbbleException e) {

        }

//        @Override
//        protected Bitmap doInBackground(Void... params) {
////            if (dictName  == null) {
////                dictName = "Dribbble";
////            }
////            Environment.getExternalStorageDirectory().toString()
////            Environment.getExternalStorageDirectory()
////            + "/"+ dictName
////            File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() );
////            if (!appDir.exists()) {
////                appDir.mkdir();
////            }
////            String imageName = imageName_ + ".jpg";
////            File file = new File(appDir, imageName);
//            //Bitmap bmp = getBitmapFromURL(requestUrl);
//            Bitmap bmp = getBitmapFromURL(requestUrl);
////            try {
////                FileOutputStream fos = new FileOutputStream(file);
//
////                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
////                Toast.makeText(getContext(),"Image Saved",Toast.LENGTH_SHORT).show();
////                fos.flush();
////                fos.close();
////            } catch (FileNotFoundException e) {
////                e.printStackTrace();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//            return bmp;
//        }

//        @Override
//        protected void onPostExecute(Bitmap bmp) {
//
//        }

        //        private String requestUrl, imagename_;
//
//        private Bitmap bitmap ;
//        private FileOutputStream fos;
//        private GetImages(String requestUrl, String _imagename_,String DictName) {
//            this.requestUrl = requestUrl;
//            this.imagename_ = _imagename_ ;
//        }
//
//        @Override
//        protected Object doInBackground(Object... objects) {
//            try {
//                URL url = new URL(requestUrl);
//                URLConnection conn = url.openConnection();
//                bitmap = BitmapFactory.decodeStream(conn.getInputStream());
//            } catch (Exception ex) {
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Object o) {
//            if(!ImageStorage.checkifImageExists(imagename_))
//            {
//                view.setImageBitmap(bitmap);
//                ImageStorage.saveToSdCard(bitmap, imagename_);
//            }
//        }
    }
    // Storage Permissions
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }
    public void imagedownload() {
//        ImageDownLoadFragment dialogFragment = ImageDownLoadFragment.newInstance();
//        dialogFragment.setTargetFragment(ShotFragment.this, REQ_CODE_NEW_IMAGE);
//        dialogFragment.show(getFragmentManager(), ImageDownLoadFragment.TAG);
        Toast.makeText(getContext(),"savededededed",Toast.LENGTH_LONG).show();
        AsyncTaskCompat.executeParallel( new GetImages(shot.getImageUrl(), "madead", "adsad"));
        Toast.makeText(getContext(),"XZXXXXXX",Toast.LENGTH_LONG).show();

    }
//    public void GetImage(String src, String newImageName, String newFileName) {
//        if (newFileName  == null) {
//            newFileName = "Dribbble";
//        }
////        File direct = new File(Environment.getExternalStorageDirectory()
////                + "/" + newFileName);
//        File direct = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                newFileName
//        );
//        if (!direct.exists()) {
//            direct.mkdirs();
//        }
//        DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
//
//        Uri downloadUri = Uri.parse(src);
//        DownloadManager.Request request = new DownloadManager.Request(
//                downloadUri);
//
//        request.setAllowedNetworkTypes(
//                DownloadManager.Request.NETWORK_WIFI
//                        | DownloadManager.Request.NETWORK_MOBILE)
//                .setAllowedOverRoaming(false).setTitle("Demo")
//                .setDescription("Something useful. No, really.")
//                .setDestinationInExternalPublicDir("/"+newFileName, newImageName+ ".jpg");
//
//        mgr.enqueue(request);

//        if (newFileName  == null) {
//            newFileName = "Dribbble";
//        }
//        File appDir = new File(Environment.getExternalStorageDirectory() + "/"+newFileName);
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        String imageName = newImageName + ".jpg";
//        File file = new File(appDir, imageName);
//        Bitmap bmp = getBitmapFromURL(src);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


    private class LikeTask extends DribbbleTask<Void, Void, Void> {

        private String id;
        private boolean like;

        public LikeTask(String id, boolean like) {
            this.id = id;
            this.like = like;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            if (like) {
                Dribbble.likeShot(id);//post like to dribbble.
            } else {
                Dribbble.unlikeShot(id);//post unlike to dribbble.
            }
            return null;
        }

        @Override
        protected void onSuccess(Void s) {
            isLiking = false;
            shot.liked = like;
            shot.likes_count += like ? 1 : -1; //if like is true like +1; if like is false like -1;
            // local data changed.
            recyclerView.getAdapter().notifyDataSetChanged();

            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
//    CheckFollowing
//    private class CheckFollowing extends DribbbleTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doJob(Void... params) throws DribbbleException {
//            return Dribbble.isFollowingUser(shot.user.id);
//        }
//
//        @Override
//        protected void onSuccess(Boolean result) {
//            isFollowing = false;
//            shot.user.foled = result;
////            recyclerView.getAdapter().notifyDataSetChanged();
//        }
//
//        @Override
//        protected void onFailed(DribbbleException e) {
//            isFollowing = false;
//            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
//    }
//    }
    private class CheckLikeTask extends DribbbleTask<Void, Void, Boolean> {
        //doJob is abstract class;
        @Override
        protected Boolean doJob(Void... params) throws DribbbleException {
            return Dribbble.isLikingShot(shot.id);
            //if is ok return true; if not find return false;
        }

        @Override
        //这个result是doJob的结果?????
        protected void onSuccess(Boolean result) {
            isLiking = false;
            shot.liked = result;
            recyclerView.getAdapter().notifyDataSetChanged(); // 刷新数据
        }

        @Override
        protected void onFailed(DribbbleException e) {
            isLiking = false;
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }
    public void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //set the URL
        shareIntent.putExtra(Intent.EXTRA_TEXT, shot.title + " " + shot.html_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_shot)));
    }

    private void setResult() {
        //we must reset result because the like_count changed. shot changed.
        //这个是click like 之后产生的result。
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_SHOT, ModelUtils.toString(shot, new TypeToken<Shot>(){}));
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    private class UpdateBucketTask extends DribbbleTask<Void, Void, Void>{
        private List<String> addedBucketIds;
        private List<String> removedBucketIds;
        public UpdateBucketTask(List<String> addedBucketIds, List<String> removedBucketIds) {
            this.addedBucketIds = addedBucketIds;
            this.removedBucketIds = removedBucketIds;
        }

        @Override
        protected Void doJob(Void... params) throws DribbbleException {
            //网络数据交互!!
            for (String addedId : addedBucketIds) {
                Dribbble.addBucketShot(addedId, shot.id);
            }
            for (String removedId : removedBucketIds) {
                Dribbble.removeBucketShot(removedId, shot.id);
                //需要知道是当前shot 的Id, 还有被取消选择的bucket id.
            }
            return null;
        }

        @Override
        protected void onSuccess(Void aVoid) {
            collectedBucketIds.addAll(addedBucketIds);
            collectedBucketIds.removeAll(removedBucketIds);

            shot.bucketed = !collectedBucketIds.isEmpty();

            shot.buckets_count += addedBucketIds.size() - removedBucketIds.size();

            recyclerView.getAdapter().notifyDataSetChanged();//更新数据

            setResult();
        }

        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private class LoadBucketsTask extends DribbbleTask<Void, Void,List<String>> {


        @Override
        protected List<String> doJob(Void... params) throws DribbbleException {
            //得到一个共同的bucketID;
            //shot里记录的bucket
            List<Bucket> shotBuckets = Dribbble.getShotBuckets(shot.id);
                List<Bucket> userBuckets = Dribbble.getUserBuckets();// user 不需要ID
                Set<String> userBucketIds = new HashSet<>();

                for (Bucket userBucket : userBuckets) {
                    userBucketIds.add(userBucket.id);
                }
                List<String> collectedBucket = new ArrayList<>();
                for (Bucket shotBucket :shotBuckets) {
                    if (userBucketIds.contains(shotBucket.id)) {
                        collectedBucket.add(shotBucket.id);
                    }
            }
            return collectedBucket;
        }

        @Override
        protected void onSuccess(List<String> result) {

            collectedBucketIds = new ArrayList<>(result);//???

            if (result.size()>0) {
                //如果有交集 说明已经加了bucket 所以buckted 显示红色;
                shot.bucketed = true;
                recyclerView.getAdapter().notifyDataSetChanged(); //更新adapter();
            }

        }
        @Override
        protected void onFailed(DribbbleException e) {
            Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

}

