package com.example.trolley_assisted_identification.Activity.Activity.Activity.Util;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.DataRefreshBean;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

public class CameraConnectUtil {

    public CameraConnectUtil(Context context){
        this.context = context;
    }

    private Context context;

    public void cameraInit(){
        //广播接收器注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(A_S);
        context.registerReceiver(myBroadcastReceiver,intentFilter);
    }

    public void cameraStopService(){
        Intent intent = new Intent(context,CameraSearchService.class);
        context.stopService(intent);
    }

    public static final String A_S = "com.a_s";

    public BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.IPCamera = intent.getStringExtra("IP");
            MainActivity.purecameraip = intent.getStringExtra("pureip");
            Log.e("camera ip::","  "+MainActivity.IPCamera);

            EventBus.getDefault().post(new DataRefreshBean(2));
            Log.d("post","准备启动摄像头");
            context.unregisterReceiver(this);
        }
    };

    // 启动摄像头
    public void useUartCamera() {
        Intent ipintent = new Intent();
        //ComponentName的参数1:目标app的包名,参数2:目标app的Service完整类名
        ipintent.setComponent(new ComponentName("com.android.settings", "com.android.settings.ethernet.CameraInitService"));
        //设置要传送的数据
        ipintent.putExtra("purecameraip", MainActivity.purecameraip);
        context.startService(ipintent);   //摄像头设为静态192.168.16.20时，可以不用发送
    }


    // 搜索摄像cameraIP
    public void search() {
        Intent intent = new Intent(context, CameraSearchService.class);
        context.startService(intent);
    }

    public void destroy(){
        try {
            context.unregisterReceiver(myBroadcastReceiver);
        }catch (RuntimeException ignored){

        }
    }



}
