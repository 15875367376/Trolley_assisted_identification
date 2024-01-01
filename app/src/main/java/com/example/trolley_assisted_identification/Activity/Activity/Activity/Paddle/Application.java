package com.example.trolley_assisted_identification.Activity.Activity.Activity.Paddle;

import android.util.Log;


public class Application extends android.app.Application {
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
        intPaddleLite();
        super.onCreate();
    }

    private void intPaddleLite(){
        //  加载库，初始化模型
        PaddleOcrAndroidUtils.init(getBaseContext());
        Log.e(TAG,"intPaddleLite:加载库，初始化模型");
    }

}
