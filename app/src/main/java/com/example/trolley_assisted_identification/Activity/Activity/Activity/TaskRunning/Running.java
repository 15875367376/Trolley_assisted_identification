package com.example.trolley_assisted_identification.Activity.Activity.Activity.TaskRunning;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest.QR.QRtest;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.ToolCv;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Util.WIFIflagtest;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.color.Car_color_test;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest.TestInferDetectionTask;


public class Running {


    static   String [] strs = {"cantGo","back","right","stop","go","left"};
    public Car_color_test color;//交通灯
    public static Context context;
    public  shape_cv shape = new shape_cv();
    public Running()
    {
        EventBus.getDefault().register(this);

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TaskListening(WIFIflagtest wifIflagtest)
    {
        if(wifIflagtest.getFlag()==1)
        {

            ToolCv.saveImageToGallery(context, MainActivity.bitmap);
            new Car_color_test().ColorRun();

        }
        else if (wifIflagtest.getFlag() ==2)
        {
                new QRtest().QrRun(MainActivity.bitmap);

        }
        else if(wifIflagtest.getFlag() ==3)
        {

//            MainActivity.camerastate_control(8);
//            try {
//                Thread.sleep(2000);
//                new CarNumAll().CarNumRun("B");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            MainActivity.camerastate_control(4);

        }
        else if(wifIflagtest.getFlag() ==4)
        {
//             shape.shapeDetcte(MainActivity.bitmap);
        }
        else if (wifIflagtest.getFlag()==5)
        {
//          selectFlagCar();

        }else if (wifIflagtest.getFlag()==6)
        {
//            boolean flag = true;
//            MainActivity.text1.setText(MainActivity.carflag);
//            String str = MainActivity.carflag;
////                Log.e("biaozhi",str);
//            if(str!=null) {
//                for (int i = 0; i < strs.length; i++) {
//                    if (str.contains(strs[i]) & flag) {
//
//                        MainActivity.Connect_Transport.trafficMark(i);
//
//                        flag = false;
//                        break;
//                    }
//                }
//                if (flag) {
//                    MainActivity.Connect_Transport.SendNull((byte) 0x06);
//                }
//            }else {
//                MainActivity.Connect_Transport.SendNull((byte) 0x06);
//            }
            }
        else if (wifIflagtest.getFlag()==7) {
            for (int i = 0; i < 3; i++) {
//                MainActivity.camerastate_control(4);
            }
        }
        else if (wifIflagtest.getFlag()==8)
        {for (int i = 0; i < 3; i++) {
//            MainActivity.camerastate_control(6);
        }
        }
        else if (wifIflagtest.getFlag()==9)
        {for (int i = 0; i < 3; i++) {
//            MainActivity.camerastate_control(8);
        }
        }
        else if (wifIflagtest.getFlag()==10)
        {for (int i = 0; i < 3; i++) {
//            MainActivity.camerastate_control(10);
        }
        }
        }



    public void selectFlagCar()
    {

        Bitmap bitmap2 = ToolCv.getTFT(MainActivity.bitmap,"a");
        MainActivity.img2.setImageBitmap(bitmap2);
        AsyncTask<Void, CharSequence, CharSequence> at = null;
        at =   new TestInferDetectionTask(Running.context,MainActivity.text1,"1F5C-53E2-7159-3321",bitmap2);

        if (at != null) {

            at.execute();
        }

    }
}
