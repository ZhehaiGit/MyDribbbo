package com.awesome.zhuzhehai.mydribbbo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by zhuzhehai on 11/6/16.
 */
public class DribbleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
