package com.example.trolley_assisted_identification.Activity.Activity.Activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.Android_Contor_Car_Util;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.WIFIflagtest;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.XcApplication;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest.QR.QRtest;
import com.hyperai.hyperlpr3.bean.Plate;
import com.hyperai.hyperlpr3.HyperLPR3;
  /*
                           安卓与主车的通信协议
 */
public class StmToAndroid  {


    public  static String str ;
    public static final String TAG = "ToAdnroid";

      public static ConnectTransport connectTransport = new ConnectTransport(); //   通讯类

    private int num=0;

    public StmToAndroid() {
        MainActivity.recvhandler = rehHandler;
        OpenThread();
    }


    public void OpenThread()
    {
        CommunicationThraed();
    }

    private MainActivity mainActivity;


    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    private Android_Contor_Car_Util android_contor_car_util = new Android_Contor_Car_Util();
    private Context context;
    boolean hasEntered = false; // 标志位，表示是否已经进入过这段代码
    @SuppressLint("HandlerLeak")
    private Handler rehHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                byte[] mByte = (byte[]) msg.obj;
                if (mByte != null && mByte.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(mByte.length);
                    for (byte byteChar : mByte)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    Log.e("data ---- ", stringBuilder.toString());
                }
                //       协议区       ===========================================================
                if (mByte[0] == 0x55)
                {

                    Log.e("xinxi", "进入");
                    if(mByte[1] == (byte) 0xFF) // 0xff
                    {
                        switch (mByte[2])
                        {
                            /****************************    握手协议   **********************************/

                            case 0x21 :
                                Log.e("xinxi", "是1");
                                /*
                                          ===============  红绿灯识别    ===============
                                */
//                                    MainActivity.camerastate_control(1);
                                        mainActivity.RGBlight(MainActivity.bitmap);       //  这是Yolov5红绿灯目标检测模型
                                    try {
                                        Thread.sleep(200);
//                                        MainActivity.camerastate_control(2);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                break;

                            case 0x22 :
                                Log.e("xinxi", "是2");
                                 /*
                                           ===============   TFT交通标志物识别     ===============
                                  */
                                        mainActivity.Carbiaozhi(MainActivity.bitmap);          //  这是Yolov5TFT交通标志物目标检测模型
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                break;

                            case 0x23 :
                                Log.e("xinxi", "是3");
                                /*
                                         ===============   车型识别   ===============
                             */
                                    mainActivity.Carxing(MainActivity.bitmap);          //  这是Yolov5车型目标检测模型
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                break;
                            case 0x24 :
                                Log.e("xinxi", "是4");
                                /*
                                            ===============  口罩行人识别    ===============
                                 */
                                    mainActivity.MaskPerson(MainActivity.bitmap);     //  这是Yolov5行人口罩目标检测模型
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                break;
                            case 0x25 :
                                Log.e("xinxi", "是5");
                                /*
                                           ===============   二维码识别    ===============
                                 */
                                    new QRtest().QrRun(MainActivity.bitmap);
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                break;
                            case 0x26:
                                Log.e("xinxi", "是6");
                                /*
                                            ===============  多车牌识别    ===============
                                 */
//                                mainActivity.Plate_detect(MainActivity.bitmap);      //  这是Yolov5车牌目标检测模型  +   谷歌文本识别OCR识别模型处理的 ,ocr识别精度有点问题就弃了
                                mainActivity.Plate_ocr(MainActivity.bitmap);           //   这是别人开源的移植百度飞桨Paddle-ocr库
//                                mainActivity.Plate_Google_ocr(MainActivity.bitmap);

                                break;
                            case 0x27 :
                                Log.e("xinxi", "是27");
                                /*
                                            ===============  文字识别     ===============
                                 */
                                    mainActivity.Text_ocr(MainActivity.bitmap);
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                break;
                            case 0x28 :
                                Log.e("xinxi", "是28");
                                /*
                                         ===============    图形识别   ===============
                                 */
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                try {
                                    Thread.sleep(200);
                                }catch (InterruptedException e){
                                    e.printStackTrace();
                                }
                                break;
                            case 0x29:
                                Log.e("xinxi", "是29");
                                if(mByte[3] == 0x01 ){
                                    num++;
                                    android_contor_car_util.Android_Contor_Car(num);
                                    try {
                                        Thread.sleep(200);
                                    }catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                }

                                break;
                            case 0x07:
                                Log.e("xinxi", "是7");
                                /*
                                          ===============   抬头  ===============
                                 */
                                MainActivity.camerastate_control(1);
                                break;
                            case 0x08:
                                Log.e("xinxi", "是8");
                                /*
                                         ===============    低头   ===============
                                 */
                                MainActivity.camerastate_control(2);
                                break;
                            case 0x09:
                                /*
                                          ===============   左转  ===============
                                 */
                                Log.e("xinxi", "是9");
                                MainActivity.camerastate_control(3);
                                break;
                            case 0x10:
                                Log.e("xinxi", "是10");
                                /*
                                           ===============  右转   ===============
                                 */
                                MainActivity.camerastate_control(4);
                                break;

                            case 0x42 : break;
                            case 0x43 :break;
                            case 0x44 :break;
                            case 0x45 :break;
                            case 0x46 :break;
                            case 0x47 :break;
                        }
                    }
                }
                 /*    ===============  右转   ==============   */

            }
        }
    };


    private  void CommunicationThraed()
    {
        XcApplication.executorServicetor.execute(new Runnable() {
            @Override
            public void run() {
               MainActivity.Connect_Transport.connect(rehHandler,MainActivity.IPCar);
//                Log.e(TAG,rehHandler);
            }
        });
    }







}