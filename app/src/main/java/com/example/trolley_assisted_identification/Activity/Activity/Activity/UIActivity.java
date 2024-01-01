package com.example.trolley_assisted_identification.Activity.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import org.greenrobot.eventbus.EventBus;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.CameraConnectUtil;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.CameraSearchService;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.ToastUtil;
import com.example.trolley_assisted_identification.R;

//import android.support.v4.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

//import Dialog.ShowDIalog;
import java.util.List;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Dialog.ShowDialog;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.WiFiStateUtil;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.XcApplication;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;


public class UIActivity extends AppCompatActivity implements View.OnClickListener{

    private ToastUtil toastUtil;
    private final String TAG = UIActivity.class.getSimpleName();

    private CameraConnectUtil cameraConnectUtil;
    private EditText mEt1 = null, mEt2 = null, mEt3 = null;
    private Button mBtn1 = null;
    private ImageView remember = null;
    private boolean passwordState = false;
    private TextView mTv1 = null;

    private ProgressDialog dialog = null;

    private String wifiName = "";





    //opencv库加载成功并初始化后的回调函数
    private BaseLoaderCallback mLoaderCallack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case  BaseLoaderCallback.SUCCESS:
                    Log.i(TAG,"加载成功");
                    Toast toast = Toast.makeText(getApplicationContext(),"加载成功！",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG,"加载失败");
                    Toast toast1 = Toast.makeText(getApplicationContext(),"加载失败！",Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.CENTER,0,0);
                    toast1.show();
                    break;
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    void Request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else if (getConnectWifiSsid() == "unknow ssid") {
                toastUtil.ShowToast("当前未接入Wi-Fi网络，请连接小车Wi-Fi");
            } else if (getConnectWifiSsid().contains("BKRC")) {
                toastUtil.ShowToast("当前连接Wi-Fi:" + getConnectWifiSsid().replaceAll("\"", ""));
            } else {
                toastUtil.ShowToast("请检查Wi-Fi是否连接");
            }
        }
    }

    /**
     * 判断是否首次启动
     *
     * @return
     */
    private boolean firstRun() {
        SharedPreferences sharedPreferences = getSharedPreferences("FirstRun", 0);
        String first_run = sharedPreferences.getString("First", "首次启动");
        if (first_run.equals("首次启动")) {
            sharedPreferences.edit().putString("First", "已经正常启动");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 应用升级提醒弹窗
     */

    private void upDialog() {
        ShowDialog showDialog = new ShowDialog();
        showDialog.show(UIActivity.this, "应用更新说明");
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     */

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_uiactivity);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//   设置全屏

        //判断是否平板
        if (isPad(this)) {
            setContentView(R.layout.activity_uiactivity);
        } else {
            setContentView(R.layout.activity_uiactivity);
        }
        EventBus.getDefault().register(this);   //  EventBus消息注册
        cameraConnectUtil = new CameraConnectUtil(this);
        findView();  //控件初始化
        cameraConnectUtil.cameraInit();//摄像头初始化
        Request();
        if (firstRun()) {
            upDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (getConnectWifiSsid() == "<unknow ssid>") {
                        toastUtil.ShowToast("当前未接入Wi-Fi网络，请连接小车Wi-Fi");
                    } else if (getConnectWifiSsid().contains("BKRC")) {
                        toastUtil.ShowToast("当前连接Wi-Fi:" + getConnectWifiSsid().replaceAll("\"", ""));
                    } else {
                        toastUtil.ShowToast("请检查Wi-Fi是否连接");
                    }
                }
        }
    }

    private String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        return wifiInfo.getSSID();
    }

    @Override            //opencv加载
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallack);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    //    UI界面控件的声明定义
    private void findView() {
        toastUtil = new ToastUtil(this);
        mEt1 = findViewById(R.id.mEt_1);
        mEt2 = findViewById(R.id.mEt_2);
        mEt3 = findViewById(R.id.mEt_3);
        mBtn1 = findViewById(R.id.mBtn_1);
        mTv1 = findViewById(R.id.mTv_wifi);
        mBtn1.setOnClickListener(this);
        remember = findViewById(R.id.remember);
        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwordState) {
                    setPasswordState(true);
                }else {
                    setPasswordState(false);
                }
            }
        });


    }

    /**
     * 设置密码隐藏/显示状态
     *
     * @param state state = true : 显示
     *              state = false ： 隐藏
     */

    private void setPasswordState(boolean state){
        if (state){
            mEt3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            remember.setBackground(getResources().getDrawable(R.drawable.ic_on));
            passwordState = true;
        }else {
            mEt3.setTransformationMethod(PasswordTransformationMethod.getInstance());
            remember.setBackground(getResources().getDrawable(R.drawable.ic_off));
            passwordState = false;
        }
    }

    public String setConnectWifiSsidTwo(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert  wifiManager != null;

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID = wifiInfo.getSSID();

        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = wifiManager.getCallerConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks){
            if (wifiConfiguration.networkId == networkId){
                SSID = wifiConfiguration.SSID;
            }
        }
        return  SSID.replace("\"","");
    }


    //      连接按钮的监听
    @Override
    public void onClick(View v) {
        if (v.equals(mBtn1)){
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在连接加速中");
            dialog.show();
            if (XcApplication.isserial == XcApplication.Mode.SOCKET){
                useNetwork();
            }else if (XcApplication.isserial != XcApplication.Mode.SOCKET){
                useUart();
            }
        }
    }


    /**
     * 接收Eventbus消息
     *
     * @param refresh
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
        if (refresh.getRefreshState() == 2) {
            startFirstActivity();
        }
    }

    // 搜索摄像cameraIP
    private void search() {
        Intent intent = new Intent(UIActivity.this, CameraSearchService.class);
        startService(intent);
    }

    private void useUart() {
        // 搜索摄像头然后启动摄像头
        search();
    }

    private void useNetwork() {
        //2.
        if (new WiFiStateUtil(this).wifiInit()) {
            //WiFi初始化成功
            search();
        } else {
            dialog.cancel();
            toastUtil.ShowToast("请确认安卓已连接小车的wifi");
        }
    }

    private void startFirstActivity() {
        dialog.cancel();
        startActivity(new Intent(UIActivity.this, MainActivity.class));
        if (MainActivity.IPCamera.equals("null:81")) {
            toastUtil.ShowToast("摄像头没有找到，快去找找它吧");
        }
        finish();
    }





//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this); //        EventBus消息注册
//        if (dialog != null){
//            dialog.cancel();
//        }
//        Log.d("UIActivity","onDESTroy");
//    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);//        EventBus消息注册
//        if (dialog != null){
//            dialog.cancel();
//        }
//        Log.d("UIActivity","onDESTroy");
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("UIActivity","onRestart");
    }


}