package com.awesome.zhuzhehai.mydribbbo.view.shot_detail;

/**
 * Created by zhuzhehai on 11/26/16.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.awesome.zhuzhehai.mydribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageDownLoadFragment extends DialogFragment {
public static final String KEY_IMAGE_NAME = "image_name";
public static final String KEY_IMAGE_FILE = "image_FILE";

@BindView(R.id.new_image_name) EditText imageName;
@BindView(R.id.new_image_file) EditText imageFile;


public static final String TAG = "NewImageDialogFragment";

public static ImageDownLoadFragment newInstance(){
        return new ImageDownLoadFragment();
        }

@NonNull

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.downloadimage,null);
        ButterKnife.bind(this, view);

        return new AlertDialog.Builder(getContext())
        .setView(view)
        .setTitle("New Image")
        // positive Button and negative Button;
        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialogInterface, int i) {
        Intent result = new Intent();
        result.putExtra(KEY_IMAGE_NAME,imageName.getText().toString());
        result.putExtra(KEY_IMAGE_FILE,imageFile.getText().toString());
        getTargetFragment().onActivityResult(ShotFragment.REQ_CODE_NEW_IMAGE,
        Activity.RESULT_OK, result);
        dismiss();
        }
        })
        .setNegativeButton("Cancel", null)
        .show();
    }
}

