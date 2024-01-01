package com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;


import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.Classifier;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.DetectorFactory;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.YoloV5Classifier;

import java.io.IOException;
import java.util.List;



/*===============================     Yolov5训练的  “红绿灯”  识别模型     ===============================*/
public class yolov5demo_1 {

    private  final AssetManager a;

    private YoloV5Classifier detector;

    private Context mContext;
    String car[] = new String[4];
    int RGB;
    public yolov5demo_1(AssetManager a){
        this.a = a;
    }

    public String start(Bitmap bitmap){
        //   模型初始化
        try {
            detector = DetectorFactory.getDetector(this.a,"yolov5_1/test.tflite");
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
        for (Classifier.Recognition result : results){
            text += result.toString() + "\n";
                String characterAtIndex = result.toString().substring(4,5);
                Log.d("String","characterAtIndex = "+characterAtIndex);
//                Log.d("???","执行异常退出？ ");
                switch (characterAtIndex) {
                    case "g":
                        text1 = "绿灯";
                        RGB = 1;
                        Log.d("light", text1 + " = " + characterAtIndex + "  and  第" + RGB + "个标签");
                        break;
                    case "r":
                        text1 = "红灯";
                        RGB = 2;
                        Log.d("light", text1 + " = " + characterAtIndex + "  and  第" + RGB + "个标签");
                        break;
                    case "y":
                        text1 = "黄灯";
                        RGB = 3;
                        Log.d("light", text1 + " = " + characterAtIndex + "  and  第" + RGB + "个标签");
                        break;

            }
        }
        return text1;
    }
    //  自己添加的提取函数
//    String light = "";
//    public  String lightRGB(){
//        if (RGB == 1){
//            light = "绿灯";
//        }
//        if (RGB == 2){
//            light = "红灯";
//        }
//        if (RGB == 3){
//            light = "黄灯";
//        }
//        return light;
//    }



}
