package com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.Classifier;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.DetectorFactory;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.YoloV5Classifier;

import java.io.IOException;
import java.util.List;



/*===============================     Yolov5训练的  “交通标志物”  识别模型     ===============================*/
public class yolov5demo_2 {

    private  final AssetManager a;

    private YoloV5Classifier detector;

    private Context mContext;
    private int No_Entry=0,No_going=0,limit=0,right=0,left=0,u_turn=0,going=0;
    String carbiaozhi[] = new String[5];
    private int car=0;
    String text3[] = new String[50];
    String CarString[] = new String[50];


    public yolov5demo_2(AssetManager a){
        this.a = a;
    }

    public String start(Bitmap bitmap){
        //   模型初始化
        try {
            detector = DetectorFactory.getDetector(this.a,"yolov5_2/test.tflite");
            detector.useCPU();
            detector.setNumThreads(4);
        }catch (IOException e){
            e.printStackTrace();
        }

        //开始识别
        List<Classifier.Recognition> results = detector.recognizeImage(bitmap);
        if (results == null) {
            Log.d("yolov5识别:", "result = null");
        }
        // 遍历回传
        String text = "";
        String text1 = "";
        String text2 = "";
        String No_Entry_1 = "禁止通行";
        String No_going_1 = "禁止直行";
        String limit_1 = "限速40";
        String right_1 = "右转";
        String left_1 = "左转";
        String u_turn_1 = "转弯";
        String going_1 = "直行";

        int j = 0;
        for (Classifier.Recognition result : results){

            for (int i = 0;i < 5;i ++) {
                String characterAtIndex = result.toString().substring(4, 8+i);
                carbiaozhi[i] = characterAtIndex;
                Log.d("String","characterAtIndex = "+characterAtIndex);
                switch (carbiaozhi[i]) {
                    case "No_Entry":
                        car = 1;
                        No_Entry++;
                        text3[j] = No_Entry_1.toString(); j++;
                        Log.d("light", "禁止通行 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "No_going":
                        car = 2;
                        No_going++;
                        text3[j] = No_going_1.toString(); j++;
                        Log.d("light", "禁止直行 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "limit":
                        car = 3;
                        limit++;
                        text3[j] = limit_1.toString(); j++;
                        Log.d("light", "限速40 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "right":
                        car = 4;
                        right++;
                        text3[j] = right_1.toString(); j++;
                        Log.d("light", "右转 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "left":
                        car = 5;
                        left++;
                        text3[j] = left_1.toString(); j++;
                        Log.d("light", "左转 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "u-turn":
                        car = 6;
                        u_turn++;
                        text3[j] = u_turn_1.toString(); j++;
                        Log.d("light", "转弯 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                    case "going":
                        car = 7;
                        going++;
                        text3[j] = going_1.toString(); j++;
                        Log.d("light", "直行 = " + carbiaozhi[i] + "  and  第" + car + "个标签");
                        break;
                }
            }
            Log.d("交通标志数量", "禁止通行 = " + No_Entry + " 个," + "禁止直行 = " + No_going + " 个," + "限速40 = " + limit + " 个," + "右转 = " + right + " 个," + "左转 = " + left + " 个," + "转弯 = " + u_turn + " 个," + "直行 = " + going + " 个,");
            text1 += result.toString() + "\n";
            text2 = No_Entry_1.toString() + " = " + No_Entry + "个,"  + No_going_1.toString() + " = " + No_going + "个,"  + limit_1.toString() + " = " + limit + "个" + "\n"
                    + right_1.toString() + " = " + right + "个," + left_1.toString() + " = " + left + "个,"  + u_turn_1.toString() + " = " + u_turn + "个,"  + going_1.toString() + " = " + going + "个," ;

        }



        return text2;
    }

      public String[] CarString(){
        for (int i = 0;i < 20;i++){
            CarString[i] = text3[i];

        }
          return CarString;
      }



}
