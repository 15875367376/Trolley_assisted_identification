package com.example.trolley_assisted_identification.Activity.Activity.Activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Paddle.PaddleOcrAndroidUtils;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.TaskRunning.Running;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.Android_Contor_Car_Util;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.CameraConnectUtil;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.OpenCV_ocr;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.ToastUtil;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.XcApplication;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest.QR.QRtest;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.env.Utils;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.yolov5demo_1;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.yolov5demo_2;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.yolov5demo_3;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.yolov5demo_4;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.yolov5demo_5;
import com.example.trolley_assisted_identification.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.hyperai.hyperlpr3.HyperLPR3;
import com.hyperai.hyperlpr3.bean.HyperLPRParameter;
import com.hyperai.hyperlpr3.bean.Plate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;

public class MainActivity extends Activity implements View.OnClickListener{


    // yolov5精度
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.1f;

    //  yolov5模型分辨率调整
    public  final int TF_OD_API_INPUT_SIZE = 320;
    // 摄像头IP
    public static String IPCamera;
    public static String purecameraip ;
    // 设备ip
    public static String IPCar;

    public static TextView text1; //           识别结果行
    private ViewPager viewPager;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;

    private ArrayList<View> pageview;
    //  6个滑动标题
//    private TextView mTvHome,mTvLight,mTvQr,mTvPlateNumber,mTvImage,mTvVoice;

    //   自动按钮和重启按钮
//    private Button mBtnzd = null,mBtncq = null;

    //  启动按钮，二维码按钮,交通灯按钮，车牌按钮，文字按钮，交通标志按钮
    private Button mBtnGo,mBtnQr,mBtnligth,mBtnCarNumber,mBtnOcr,mBtn_traffic_data;

    //  摄像头刷新按键
    private Button reference_Btn;

    public static  ImageView img1,img2,img3;

    private static TextView showip = null;

    public static final String A_S = "com.a_s";

    private boolean flag = true;

    public static Handler recvhandler = null;

    public static ConnectTransport Connect_Transport; //   通讯类
    private CameraConnectUtil cameraConnectUtil;
     public static CameraCommandUtil cameraCommandUtil;

    private ConnectTransport sock_con;

    public Running run  ;

    private WifiManager wifiManager;

    private DhcpInfo dhcpInfo;
    public static long handle;


    public static StmToAndroid STA;
    public static String carflag = null;

    //信心
    public static float  confidence = 0 ;
    public  static String str ;

    //    交通标志的数量
    private int Num1=0,Num2=0,Num3=0,Num4=0,Num5=0,Num6=0,Num7=0;

    //    车型的数量
    private int Num_1=0,Num_2=0,Num_3=0,Num_4=0;

    //    口罩行人的数量
    private int Num_11=0,Num_22=0;

    //  文本识别
    private AlertDialog.Builder alertDialog;

//    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;

    public  static HashMap<String, Mat> FlagAll = new HashMap<>();



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA"};

    /*
     * 对应百度平台上的应用apiKey
     */
    private String apiKey = "2j7aPNqhC8z1ZGogu6PSYivh";
    /*
     * 对应百度平台上的应用secretKey
     */
    private String secretKey = "NSMUjVRx9D5FS5YpfYvyUAFpIVHkSguL";


    public shape_cv shape = new shape_cv();


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*==============================    初始化    ===================================*/


    /*===========================    页面控件初始化     ============    按键控件与滑动冲突，暂时没有解决    ========================*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        IPCar = Formatter.formatIpAddress(dhcpInfo.gateway);
        /*============   这部分初始化很重要    =================*/
        Connect_Transport = new ConnectTransport();
        EventBus.getDefault().register(this);//EventBus消息注册
        Log.d("EventBus消息","注册");
        cameraCommandUtil = new CameraCommandUtil();
        cameraConnectUtil = new CameraConnectUtil(getApplicationContext());
        Log.d("通讯初始化完成","1");
        /*============   Paddle-ocr模型初始化    =================*/
        intPaddleLite();
        /*============   初始化hyperlpr    =================*/
        // License plate recognition algorithm configuration parameters
        HyperLPRParameter parameter = new HyperLPRParameter()
                .setDetLevel(HyperLPR3.DETECT_LEVEL_HIGH)
                .setMaxNum(1)
                .setRecConfidenceThreshold(0.85f);
        // Initialization (performed only once)
        HyperLPR3.getInstance().init(this, parameter);
        Log.d("hyperlpr初始化","1");

//


        sock_con = new ConnectTransport();
//        initTextSDK();

        /*============   控件初始化    =================*/
        showip = (TextView)findViewById(R.id.showip);
        img1= findViewById(R.id.img1);
        img2= findViewById(R.id.img2);
        img3= findViewById(R.id.img5);
        text1 = findViewById(R.id.text1);
        reference_Btn = findViewById(R.id.button);
        // 启动按钮
        mBtnGo = findViewById(R.id.Btn_1);
        // 红绿灯识别按钮
        mBtnligth = findViewById(R.id.Btn_2);
        // 车牌识别按钮
        mBtnCarNumber = findViewById(R.id.Btn_3);
        // 二维码识别按钮
        mBtnQr = findViewById(R.id.Btn_4);
        // 文字识别按钮
        mBtnOcr = findViewById(R.id.Btn_5);
        // 交通标志物识别按钮
        mBtn_traffic_data = findViewById(R.id.Btn_6);
        //  查找布局文件用LayoutInflater.inflate
//        LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(this);
        reference_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectrotationAnim(reference_Btn);
//                generalBasic();
            }
        });

        getCameraPic();
        Log.d("getCameraPic","加载成功");
        if (XcApplication.isserial == XcApplication.Mode.SOCKET && !IPCamera.equals("null:81")) {
            setCameraConnectState(true);
            showip.setText("WiFi-IP：" + MainActivity.IPCar + "\n" + "Camera-IP:" + MainActivity.purecameraip);
        } else if (XcApplication.isserial == XcApplication.Mode.SOCKET && IPCamera.equals("null:81")) {
            showip.setText("WiFi-IP：" + MainActivity.IPCar + "\n" + "请重启您的平台！");
        }

        STA = new StmToAndroid();
        STA.setMainActivity(this);
        Log.d("StmToAndroid","1");

        Android_Contor_Car_Util android_contor_car_util = new Android_Contor_Car_Util();
        /*
                      ===============  启动按钮    ===============
          */
        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect_Transport.autoDrive();
//                android_contor_car_util.Android_Contor_Car(0);
                Log.d(TAG,"启动成功");
            }
        });
        /*
                      ===============  红绿灯识别按钮    ===============
          */
        mBtnligth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RGBlight(MainActivity.bitmap);
            }
        });
        /*
                      ===============  多车牌识别按钮    ===============
         */
        mBtnCarNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Plate_detect(MainActivity.bitmap);        //   这是Yolov5车牌目标检测模型  +   谷歌文本识别OCR识别模型处理的 ,ocr识别精度有点问题就弃了
                Plate_ocr(MainActivity.bitmap);                      //   这是别人开源的移植百度飞桨Paddle-ocr库
//              Plate_Google_ocr(MainActivity.bitmap);    //   谷歌文本识别OCR识别模型处理的
            }
        });
        /*
                        ===============   二维码识别按钮    ===============
         */
         mBtnQr.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new QRtest().QrRun(MainActivity.bitmap);
             }
         });
         /*
                        ===============  文字识别按钮     ===============
           */
        mBtnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Text_ocr(MainActivity.bitmap);
            }
        });
        /*
                      ===============   TFT交通标志物识别按钮     ===============
         */
        mBtn_traffic_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carbiaozhi(MainActivity.bitmap);
            }
        });
//        Bitmap redBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tuxin_1,null);
//        MainActivity.img1.setImageBitmap(redBitmap);
//        Plate_ocr(redBitmap);
//        shape.shapeDetcte(redBitmap);
//        Text_ocr(redBitmap);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        redBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] imageBytes = stream.toByteArray();
//        DebugInfo debugInfo = new DebugInfo();
//        List<Shape> shapes = RecognitionShapeUtils.index(imageBytes,debugInfo);
//        ShapeType type = null;
//        ShapeShape shapeShape = null;
//        String color = null;
//        for (Shape shape : shapes){
//            type = shape.getType();
//            shapeShape = shape.getShape();
//            // 颜色
//            color = shape.getColor();
//            MainActivity.text1.setText(type+","+shapeShape+","+color+"\r\n");
//
//        }

    }

    /*==============================    初始化EN     ===================================*/




     /*
                      Paddle-ocr模型初始化函数
      */
    private void intPaddleLite(){
        //  加载库，初始化模型
        PaddleOcrAndroidUtils.init(getBaseContext());
        Log.e(TAG,"intPaddleLite:加载库，初始化模型");
    }


    /*
                           功能初始化
      */
    private void FunctionInit()
    {
//       FlagAll.put("color",shape_cv.BitmapToMat(BitmapFactory.decodeResource(getResources(), R.mipmap.colorflag2, null)));
        run = new Running();
        Running.context = getApplicationContext();
        //  camerastate_control(6);
    }

    /*
     *        这是百度ocr需要用到的，但是我没成功移植百度ocr进来   用明文ak，sk初始化
     */
    private void initTextSDK() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.d("result-->","成功！"+token);
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.d("result-->","失败！"+error.getMessage());

            }
        }, getApplicationContext(),  apiKey, secretKey);
    }


    /*
    *              按键监听
    * */
    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }






    /*
                             要申请的权限
      */
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /*
                       判断是否需要获取sd卡写权限
     */
    private void checkReadSd() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, permissions, 1);
                // 直接跳转到权限设置界面
                Toast.makeText(this, "打开存储权限后才能进行识别", Toast.LENGTH_LONG).show();
                goToAppSetting();
            }
        }
    }


    /*
                       跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 1);
    }







    /*
                      安卓控制主车启动,执行是主车执行
      */
    public static ToastUtil toastUtil;

    //     启动
    private void autoDriveAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置Title的内容
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("温馨提示");
        // 设置Content来显示一个信息
        builder.setMessage("请确认是否开始自动驾驶！");
        // 设置一个PositiveButton
        builder.setPositiveButton("开始", (dialog, which) -> {
            Connect_Transport.autoDrive();
            toastUtil.ShowToast( "开始自动驾驶，请检查车辆周围环境！");
        });
        // 设置一个NegativeButton
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

     //          重启标志物
    private void BtnRestart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置Title的内容
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("温馨提示");
        // 设置Content来显示一个信息
        builder.setMessage("请确认是否重启标志物！");
        // 设置一个PositiveButton
        builder.setPositiveButton("重启", (dialog, which) -> {
            Connect_Transport.RestartMarker();
            toastUtil.ShowToast( "重启动成功！");
        });
        // 设置一个NegativeButton
        builder.setNegativeButton("取消", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }




    /*==============================    识别模型    ===================================*/

    /*
    RGBlight() : 红绿灯模型
    Carbiaozhi() : 交通标志物模型
    Carxing() : 车型模型
    MaskPerson() : 口罩行人模型
     */


    /*
                  红绿灯识别模型
     */
    private String text33;
    public void RGBlight(Bitmap bitmap){

        text33 = "";

        Log.d("KaiXuan","w: " + bitmap.getWidth() + "h: "+bitmap.getHeight());

        Bitmap bitmap1 = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE);
        MainActivity.img2.setImageBitmap(bitmap1);

        if (bitmap1 == null){
            Log.d("RGBlight:","bitmap = null");
        }
            // 开始识别
            yolov5demo_1 yolov5demo = new yolov5demo_1(getAssets());
            text33 = yolov5demo.start(bitmap1);

        //  显示识别
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.text1.setText(text33);
                lightSend(text33);
                Log.d("RGBlight","test: " + text33);
            }
        });
//        MainActivity.Connect_Transport.Sendtext(text33);
        if (text33 == null){
            Log.d("RGBlight","test = null");
        }
    }
    // 发送红绿灯识别结果
    public void lightSend(String info){
        if (info != null){
            switch (info){
                case "红灯": MainActivity.Connect_Transport.traffic_control(0xAA, 0x40, 0x01);break;
                case "黄灯": MainActivity.Connect_Transport.traffic_control(0xAA, 0x40, 0x03);break;
                case "绿灯": MainActivity.Connect_Transport.traffic_control(0xAA, 0x40, 0x02);break;
            }
        }
    }



    /*
                 交通标志物识别模型
     */
    String CarString22[] = new String[50];
    private String text34;
    private String[] text44;
    public void Carbiaozhi(Bitmap bitmap){

        text34 = "";

        Log.d("KaiXuan","w: " + bitmap.getWidth() + "h: "+bitmap.getHeight());
        Num1 = 0;Num2 = 0;Num3 = 0;Num4 = 0;Num5 = 0;Num6 = 0;Num7 = 0;

        OpenCV_ocr openCV_ocr = new OpenCV_ocr();
        openCV_ocr.setBitmap1(bitmap,2);
        Bitmap bitmap1 = openCV_ocr.getBitmap1();

        Bitmap bitmap2 = Utils.processBitmap(bitmap1, TF_OD_API_INPUT_SIZE);

        img2.setImageBitmap(bitmap2);

        if (bitmap2 == null){
            Log.d("RGBlight:","bitmap = null");
        }
        try {
            Thread.sleep(100);
            // 开始识别
            yolov5demo_2 yolov5demo_1 = new yolov5demo_2(getAssets());
            text34 = yolov5demo_1.start(bitmap2);
            text44 = yolov5demo_1.CarString();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //  显示识别
        text1.setText(text34);
        for (int i = 0;i < 20; i++){
            CarString22[i] = text44[i];
            String car55 = CarString22[i];
            CarbiaozhiPanduan(car55);
            Log.d("交通标志物","CarString22"+car55);
        }
//        MyThread1 mt1 = new MyThread1("窗口1");
//        mt1.start();
        if(text34 == null){
            MainActivity.Connect_Transport.sendNull();
            Log.d("交通标志识别","交通标志为空");
        }
        if(Num1 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x06, Num1);//禁止通行
            Log.d("交通标志物","禁止通行");
        }
        if(Num2 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x05, Num2);//禁止直行
            Log.d("交通标志物","禁止直行");
        }
        if(Num3 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x08, Num3);//限速40
            Log.d("交通标志物","限速40");
        }
        if(Num4 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49, 0x03 ,Num4);//右转
            Log.d("交通标志物","右转");
        }
        if(Num5 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x02, Num5);//左转
            Log.d("交通标志物","左转");
        }
        if(Num6 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x04, Num6);//转弯
            Log.d("交通标志物","转弯");
        }
        if(Num7 != 0){
            MainActivity.Connect_Transport.traffic_signs(0xAA, 0x49 , 0x01, Num7);//直行
            Log.d("交通标志物","直行");
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    // 判断交通标志物识别数量结果
    public void CarbiaozhiPanduan(String info){
        if (info != null){
            switch (info){
                case "禁止通行": Num1++;break;
                case "禁止直行": Num2++;break;
                case "限速40": Num3++;break;
                case "右转": Num4++;break;
                case "左转": Num5++;break;
                case "转弯": Num6++;break;
                case "直行": Num7++;break;
            }
        }
    }

    class MyThread1 extends Thread{
        private String name; //窗口名, 也即是线程的名字
        public MyThread1(String name){
            this.name=name;
        }
        @Override
        public void run() {
            // 发送交通标志物识别数量结果
                if(Num1 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x01, Num1);
                }
                if(Num2 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x02, Num2);
                }
                if(Num3 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x03, Num3);
                }
                if(Num4 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x04, Num4);
                }
                if(Num5 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x05, Num5);
                }
                if(Num6 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x06, Num6);
                }
                if(Num7 != 0){
                    MainActivity.Connect_Transport.traffic_signs(0xFF, 0x03 , 0x07, Num7);
                }

        }
    }


    /*
                     车型识别模型
     */
    String CarString33[] = new String[50];
    private String text35;
    private String[] text45;
    public void Carxing(Bitmap bitmap){

        text35 = "";

        Log.d("KaiXuan","w: " + bitmap.getWidth() + "h: "+bitmap.getHeight());

        Bitmap bitmap1 = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE);

        img2.setImageBitmap(bitmap1);

        if (bitmap1 == null){
            Log.d("Cart:","bitmap = null");
        }
        try {
            Thread.sleep(100);
            // 开始识别
            yolov5demo_3 yolov5demo_3 = new yolov5demo_3(getAssets());
            text35 = yolov5demo_3.start(bitmap1);
            text45 = yolov5demo_3.CarString();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //  显示识别
        text1.setText(text35);
        for (int i = 0;i < 20; i++){
            CarString33[i] = text45[i];
            String car55 = CarString33[i];
            CartxingPanduan(car55);
            Log.d("车型","CarString33      "+car55);
        }
        MyThread2 mt2 = new MyThread2("窗口2");
        mt2.start();
    }

    // 判断车型识别数量结果
    public void CartxingPanduan(String info){
        if (info != null){
            switch (info){
                case "摩托车": Num_1++;break;
                case "自行车": Num_2++;break;
                case "汽车": Num_3++;break;
                case "卡车": Num_4++;break;
            }
        }
    }

    class MyThread2 extends Thread{
        private String name; //窗口名, 也即是线程的名字
        public MyThread2(String name){
            this.name=name;
        }
        @Override
        public void run() {
            // 发送车型识别数量结果
                if(Num_1 != 0){
                    MainActivity.Connect_Transport.Models_cart(0xFF, 0x04 , 0x01, Num_1);
                }
                if(Num_2 != 0){
                    MainActivity.Connect_Transport.Models_cart(0xFF, 0x04 , 0x01, Num_2);
                }
                if(Num_3 != 0){
                    MainActivity.Connect_Transport.Models_cart(0xFF, 0x04 , 0x01, Num_3);
                }
                if(Num_4 != 0){
                    MainActivity.Connect_Transport.Models_cart(0xFF, 0x04 , 0x01, Num_4);
                }
        }
    }



    /*
                         行人口罩识别模型
     */
    String MaskString44[] = new String[50];
    private String text36;
    private String[] text46;
    public void MaskPerson(Bitmap bitmap){

        text36 = "";
        Log.d("KaiXuan","w: " + bitmap.getWidth() + "h: "+bitmap.getHeight());

        Bitmap bitmap1 = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE);

        img2.setImageBitmap(bitmap1);

        if (bitmap1 == null){
            Log.d("MaskPerson:","bitmap = null");
        }

        try {
            Thread.sleep(100);
            // 开始识别
            yolov5demo_4 yolov5demo_4 = new yolov5demo_4(getAssets());
            text36 = yolov5demo_4.start(bitmap1);
            text46 = yolov5demo_4.MaskPersonString();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        //  显示识别
        text1.setText(text36);
        for (int i = 0;i < 20; i++){
            MaskString44[i] = text46[i];
            String mask44 = MaskString44[i];
            CartxingPanduan(mask44);
            Log.d("口罩行人","MaskString44      "+mask44);
        }
        MyThread3 mt3 = new MyThread3("窗口3");
        mt3.start();
    }

    // 判断口罩行人识别结数量果
    public void MaskPersonPanduan(String info){
        if (info != null){
            switch (info){
                case "口罩": Num_11++;break;
                case "行人": Num_22++;break;
            }
        }
    }

    class MyThread3 extends Thread{
        private String name; //窗口名, 也即是线程的名字
        public MyThread3(String name){
            this.name=name;
        }
        @Override
        public void run() {
            // 发送口罩行人识别结数量果
            if(Num_11 != 0){
                MainActivity.Connect_Transport.Mask_Person(0xFF, 0x05 , 0x01, Num_11);
            }
            if(Num_22 != 0){
                MainActivity.Connect_Transport.Mask_Person(0xFF, 0x05 , 0x02, Num_22);
            }
        }
    }



    /*
                车牌检测别模型+谷歌文本识别模型
     */
    String PlateString55[] = new String[50];
    StringBuilder sb = new StringBuilder();
    private static final int REQUEST_CODE_ACCURATE_BASIC = 107;
    private String[] text37;
    private Bitmap bitmap4545;
    private String[] str1;
    public void Plate_detect(Bitmap bitmap){

        Log.d("KaiXuan","w: " + bitmap.getWidth() + "h: "+bitmap.getHeight());

        bitmap4545 = Utils.processBitmap(bitmap, TF_OD_API_INPUT_SIZE);
        final Application app = getApplication();
//        img2.setImageBitmap(bitmap1);

        if (bitmap4545 == null){
            Log.e(TAG,"bitmap = null");
        }

        try {
            Thread.sleep(10);
            // 创建一个新的Bitmap对象，用于存储调整后的图像
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap4545, bitmap4545.getWidth(), bitmap4545.getHeight(), true);
            Canvas canvas = new Canvas(resizedBitmap);
            Paint paint = new Paint();

            // 设置抗锯齿
            paint.setAntiAlias(true);

            // 设置图像的色彩饱和度
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.3f);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);paint.setColorFilter(colorFilter);
            // 绘制调整后的图像
            canvas.drawBitmap(resizedBitmap, 0, 0, paint);
            // 开始识别
            yolov5demo_5 yolov5demo_5 = new yolov5demo_5(getAssets());
            yolov5demo_5.start(bitmap4545);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    /*
                谷歌机器学习-OCR车牌识别模型
     */
    private String text_5;
    private int num;

    public void Plate_Google_ocr(Bitmap bitmap){
        num = 0;text_5 = "";
            if (bitmap != null){
                OpenCV_ocr openCV_ocr = new OpenCV_ocr();
                openCV_ocr.setBitmap1(bitmap,1);
                Bitmap bitmap1 = openCV_ocr.getBitmap1();
                img2.setImageBitmap(bitmap1);
//                Bitmap bitmap2 = PaddleOcrAndroidUtils.ocr1(bitmap1);
                // When using Chinese script library       调用中文包
                TextRecognizer recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                InputImage image = InputImage.fromBitmap(bitmap1,0);
                Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String resultText = text.getText();
                        // 处理识别结果
                        String[] results = resultText.split("\n");  // 假设每个识别结果以换行符分隔
                        for (String result : results) {
                            if (result.contains("A")) {  // 这里可以根据实际情况来判断识别结果是否为车牌
                                    // 在界面上显示识别结果
                                    String strr1 = result.toString().substring(1) + ",";
                                    String strr2 = strr1.replace(" ","");
                                    text_5 += strr2;
                            }
                        }
                        if(text_5.length() > 5){
                            String str = text_5.toString();
                            MainActivity.text1.setText(str);
                            MainActivity.Connect_Transport.sendShape(str);
                            Log.d("谷歌OCR车牌识别", "识别结果：" + str);
                        }else {
                            MainActivity.Connect_Transport.sendNull();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MainActivity.Connect_Transport.sendNull();
                        Log.e("谷歌OCR车牌识别","不符合预期");
                    }
                });
            }else {
                MainActivity.Connect_Transport.sendNull();
                Log.d("谷歌OCR车牌识别","没有车牌照片");
            }
    }


    public static double[][] HSV_VALUE_LOW = {
            {40,45,130},  // 蓝色车牌最低阈值
            {40,10,160}   // 绿色车牌最低阈值
    };

    public static double[][] HSV_VALUE_HIGH = {
            {90,100,210}, // 蓝色车牌最高阈值
            {100,50,210}  // 绿色车牌最高阈值
    };
    /*
                Paddle-OCR车牌识别模型
     */
    private Bitmap resizedBitmap;

    private String str16 ;
//    private String[] str16 = new String[10];
    public void Plate_ocr(Bitmap bitmap){

        str16 = "";

        OpenCV_ocr openCV_ocr = new OpenCV_ocr();

        openCV_ocr.setBitmap1(bitmap,1);
        Bitmap bitmap1 = openCV_ocr.getBitmap1();

        Bitmap preprocessedBitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(preprocessedBitmap);
        Paint paint = new Paint();
        // 调整对比度
//        ColorMatrix contrastMatrix = new ColorMatrix(new float[] {
//                2, 0, 0, 0, -180,
//                0, 2, 0, 0, -180,
//                0, 0, 2, 0, -180,
//                0, 0, 0, 1, 0
//        });
        // 设置抗锯齿
        paint.setAntiAlias(true);

//        // 设置图像的色彩饱和度
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.5f);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);paint.setColorFilter(colorFilter);

        // 去除模糊
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

        // 绘制调整后的图像
        canvas.drawBitmap(bitmap1, 0, 0, paint);


        try {
            Thread.sleep(10);

                if (preprocessedBitmap != null){
                    img2.setImageBitmap(bitmap1);
                    img3.setImageBitmap(preprocessedBitmap);
                    String ocrResult = PaddleOcrAndroidUtils.ocr(preprocessedBitmap);
                    if (ocrResult != null){
                        String[] split = ocrResult.split("\n");
                        for (String label : split ){
                            if (label.matches("^\\d+:.*")){
                                String str1 = label.trim().replaceAll("\\s+","").replaceAll(":","").replaceAll("·","").replaceAll("-","");
                                String str2 = str1.toString().substring(2);
                                if (str2.length() > 5){
                                    if(Character.isUpperCase(str2.charAt(0))){
                                        str16 += str2 + ",";
                                        Log.d("Paddle-ocr车牌识别","符合预期,识别结果为："+str16);
                                    }
                                }
                            }else {
                                Log.d("Paddle-ocr车牌识别","不符合预期");
                            }
                        }

                    }else {
//                        MainActivity.Connect_Transport.sendNull();
                        String str4 = "1";
                        MainActivity.Connect_Transport.sendShape(str4);
                        Log.d("Paddle-ocr车牌识别","未找到车牌");
                    }

                }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        if(str16.length() > 4){
//          String str3 = str16.toString().substring(4);
            String str3 = "";
            str3 = str16;
            MainActivity.text1.setText(str3);
            MainActivity.Connect_Transport.sendShape(str3);
            Log.d("Paddle-ocr车牌识别","识别结果为："+str3);
        }else{
            String str4 = "";
            MainActivity.Connect_Transport.sendShape(str4);
            Log.d("Paddle-ocr车牌识别","车牌不正确");
        }

//        String str4 =str16;
//        MainActivity.text1.setText(str4);

    }


    /*
                Paddle-OCR文字识别模型
     */
    private String text_1;

    public void Text_ocr(Bitmap bitmap){
        text_1 = "";
        Bitmap preprocessedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(preprocessedBitmap);
        Paint paint = new Paint();


        // 设置抗锯齿
        paint.setAntiAlias(true);

//        // 设置图像的色彩饱和度
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.3f);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);paint.setColorFilter(colorFilter);
        // 调整对比度
//        ColorMatrix contrastMatrix = new ColorMatrix(new float[] {
//                2, 0, 0, 0, -150,
//                0, 2, 0, 0, -150,
//                0, 0, 2, 0, -150,
//                0, 0, 0, 1, 0
//        });
//        ColorMatrixColorFilter contrastFilter = new ColorMatrixColorFilter(contrastMatrix);
//        paint.setColorFilter(contrastFilter);

        // 去除模糊
        paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

    // 绘制调整后的图像
       canvas.drawBitmap(bitmap, 0, 0, paint);

        OpenCV_ocr openCV_ocr = new OpenCV_ocr();

        openCV_ocr.setBitmap2(preprocessedBitmap);
        Bitmap bitmap1 = openCV_ocr.getBitmap2();

        try {
            Thread.sleep(10);
            StringBuffer message = new StringBuffer();
            if (preprocessedBitmap != null){
                img2.setImageBitmap(bitmap1);
                String ocrResult = PaddleOcrAndroidUtils.ocr(bitmap1);
                if (ocrResult != null){
                    String[] split = ocrResult.split("\n");
                    for (String label : split ){
                        String str1 = label.trim().replaceAll("\\d+:|\\d+：", "");
                        String str2 = str1.toString();
                        text_1 += str2;
                        Log.d("Paddle-ocr文字识别","符合预期,识别结果为："+str1);
                        Log.d("Paddle-ocr文字识别","符合预期,识别结果为："+text_1);
                    }
                    message.append(text_1);
                    Log.e("Paddle-ocr文字识别","识别结果"+text_1);
                    }


                }else {
                    Log.d("Paddle-ocr文字识别","未找到文字");
                }
            if(text_1.length() > 2){
                String str3 = message.toString();
                MainActivity.text1.setText(str3);
                MainActivity.Connect_Transport.Text_Spend(str3);
            }

//            MainActivity.Connect_Transport.sendShape(str3);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    /*
                谷歌机器学习-OCR文字识别模型
     */
    private String text_2;

    public void Text1_ocr(Bitmap bitmap){
        try {
            Thread.sleep(10);
            if (bitmap != null){
                img2.setImageBitmap(bitmap);
                // When using Chinese script library       调用中文包
                TextRecognizer recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                InputImage image = InputImage.fromBitmap(bitmap,0);
                Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String resultText = text.getText();
                        text_2 = resultText.replaceAll("[^\\u4e00-\\u9fa5]", "");
                        MainActivity.text1.setText(text_2);
                        Log.d("谷歌OCR中文识别","识别结果："+ text_2);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("谷歌OCR中文识别","谷歌OCR文字识别错误");
                    }
                });
            }else {
                Log.d("谷歌OCR中文识别","未找到文字");
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /*
                HyperLPR3单车牌识别,这个识别库只能在安卓上只能实现单车牌，但是他底层不让改我也没办法，只能单车牌了
     */
    public String HyperLPR3_plate_one(Bitmap bitmap){
        String text_3 = "";
        // 使用Bitmap作为图片参数进行车牌识别
        Plate[] plates =  HyperLPR3.getInstance().plateRecognition(bitmap, HyperLPR3.CAMERA_ROTATION_0, HyperLPR3.STREAM_BGRA);
        for (Plate plate: plates) {
            String str1 = plate.getCode().toString().substring(1);
            if(str1.length() > 4){
                text_3 = str1;
                // 打印检测到的车牌号
                Log.d("HyperLPR3_plate", "HyperLPR3车牌识别结果"+text_3);
            }
        }
        return text_3;

    }
    /*
                HyperLPR3车牌识别
     */
//    public void HyperLPR3_plate(Bitmap bitmap)
//
//    }




    /*==============================    识别模型EN    ===================================*/





    
    /*
             获取摄像头的的实时画面
     */
    public static void setBitmap(Bitmap bitmap) {
        MainActivity.bitmap = bitmap;
    }
    @SuppressLint("HandlerLeak")
    public static Handler showidHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"收到");
            if (msg.what == 22) {
                showip.setText(msg.obj + "\n" + "Camera-IP：" + IPCamera);

            }
        }
    };

    /*
            图片
     */
    public static Bitmap bitmap;

     /*
            摄像头连接状态，默认为true
     */
    private boolean cameraConnectState = true;
    public boolean isCameraConnectState() {
        return cameraConnectState;
    }
    public void setCameraConnectState(boolean cameraConnectState) {
        this.cameraConnectState = cameraConnectState;
    }
    private void getCameraPic() {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
                if (IPCamera.equals("null:81")) return;
                while (true) {
                    getBitmap();
                }
            }
        });
    }
    private boolean connectState = true;

    /*
             得到当前摄像头的图片信息
     */
    private void getBitmap() {
        setBitmap(cameraCommandUtil.httpForImage(IPCamera));
        if (bitmap != null) {
            setBitmap(RadiusUtil.roundBitmapByXfermode(bitmap, bitmap.getWidth(), bitmap.getHeight(), 8));
        } else {
            setCameraConnectState(false);
        }
        phHandler.sendEmptyMessage(10);
        Log.d("TAG","实时");
    }

    /*
             显示图片
     */
    @SuppressLint("HandlerLeak")
    private Handler phHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                img1.setImageBitmap(bitmap);
                Log.d("TAG","显示");
            } else if (msg.what == 11) {

            }
        }
    };
    private Bitmap newbitmap = null;
    public static final String TAG = "MainActivity";

    /*
     * 接收Eventbus消息
     *
     * @param refresh
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(DataRefreshBean refresh) {
        if (refresh.getRefreshState() == 1) {
            Log.e(TAG, "onEventMainThread: "  + IPCamera);
            if (IPCamera.equals("null:81")){
                Log.e(TAG, "onEventMainThread: "  + IPCamera);
                cameraConnectUtil.cameraStopService();
                cameraConnectUtil.cameraInit();
                cameraConnectUtil.search();
            }else {
                cameraConnectUtil.cameraStopService();
                cameraConnectUtil.cameraInit();
                cameraConnectUtil.search();
            }
        } else if (refresh.getRefreshState() == 2) {
            getCameraPic();
            if (XcApplication.isserial == XcApplication.Mode.SOCKET && !IPCamera.equals("null:81")) {
                setCameraConnectState(true);
                toastUtil.ShowToast("摄像头已连接");
                showip.setText("WiFi-IP：" + MainActivity.IPCar + "\n" + "Camera-IP:" + MainActivity.purecameraip);
            } else if (XcApplication.isserial == XcApplication.Mode.SOCKET && IPCamera.equals("null:81")) {
                showip.setText("WiFi-IP：" + MainActivity.IPCar + "\n" + "请重启您的平台！");
            }
        } else if (refresh.getRefreshState() == 4) {
            connectState = false;
            if (bitmap != null){
                bitmap.recycle();
            }
            setBitmap(newbitmap);
        }
    }

    /*
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            assert manager != null;
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空
            if (networkInfo != null)
                return networkInfo.isConnected();
        }
        return false;
    }

    /*
             滑动实时画面来控制摄像头向上，向下，向左，向右，不过写的好像有问题，没有实现,可能是跟我的滑动起冲突了
     */
    private float x1 = 0;
    private float y1 = 0;
    private class ontouchlistener1 implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO 自动生成的方法存根
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 点击位置坐标
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                // 弹起坐标
                case MotionEvent.ACTION_UP:
                    float x2 = event.getX();
                    float y2 = event.getY();
                    float xx = x1 > x2 ? x1 - x2 : x2 - x1;
                    float yy = y1 > y2 ? y1 - y2 : y2 - y1;
                    // 判断滑屏趋势
                    int MINLEN = 30;
                    if (xx > yy) {
                        if ((x1 > x2) && (xx > MINLEN)) {        // left
                            toastUtil.ShowToast("向左微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 4, 1);  //左
                                }
                            });

                        } else if ((x1 < x2) && (xx > MINLEN)) { // right
                            toastUtil.ShowToast("向右微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 6, 1);  //右
                                }
                            });
                        }
                    } else {
                        if ((y1 > y2) && (yy > MINLEN)) {        // up
                            toastUtil.ShowToast("向上微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 0, 1);  //上
                                }
                            });
                        } else if ((y1 < y2) && (yy > MINLEN)) { // down
                            toastUtil.ShowToast("向下微调");
                            XcApplication.executorServicetor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    cameraCommandUtil.postHttp(IPCamera, 2, 1);  //下
                                }
                            });
                        }
                    }
                    x1 = 0;
                    x2 = 0;
                    y1 = 0;
                    y2 = 0;

                    break;
            }
            return true;
        }
    }

    /*
     * 刷新按钮实现顺时针360度    按键控件与滑动冲突，暂时没有解决
     *
     * @param view
     */
    private void ObjectrotationAnim(View view) {
        //构造ObjectAnimator对象的方法
        EventBus.getDefault().post(new DataRefreshBean(1));
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0.0F, 360.0F);// 设置顺时针360度旋转
        animator.setDuration(1500);//设置旋转时间
        animator.start();//开始执行动画（顺时针旋转动画）
    }




    /*      =========================摄像头控制====================================     */
    public static void camerastate_control(final int set)  {
        XcApplication.executorServicetor.execute(new Runnable() {
            public void run() {
                switch (set) {
                    //上下左右转动
                    case 1:
                        cameraCommandUtil.postHttp(IPCamera,0,1);//  抬头
                        break;
                    case 2:
                        cameraCommandUtil.postHttp(IPCamera,2,1);//  低头
                        break;
                    case 3:
                        cameraCommandUtil.postHttp(IPCamera,4,1);// 设置1
                        break;
                    case 4:
                        cameraCommandUtil.postHttp(IPCamera,6,1);// 调用1   抬头
                        break;
                    case 5 :
                        cameraCommandUtil.postHttp(IPCamera,34,0);//设置2
                        break;
                    case 6:
                        cameraCommandUtil.postHttp(IPCamera,35,0);//调用2  初始
                        break;
                    case 7:
                        cameraCommandUtil.postHttp(IPCamera,36,0);//设置3
                        break;
                    case 8:
                        cameraCommandUtil.postHttp(IPCamera,37,0);//调用3    左
                        break;
                    case 9:
                        cameraCommandUtil.postHttp(IPCamera,38,0);//设置4
                        break;
                    case 10:
                        cameraCommandUtil.postHttp(IPCamera,39,0);//调用4   右
                        break;
                    default:
                        break;
                }

            }
        });
    }

    /*===============================     摄像头控制EN     ===============================*/



    /*
         MainActivity销毁结束
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraConnectUtil.destroy();
        if (XcApplication.isserial == XcApplication.Mode.SOCKET){
            Connect_Transport.destory();

        }
    }
}