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



/*===============================     Yolov5训练的  “口罩行人”  识别模型    ===============================*/
public class yolov5demo_4 {

    private  final AssetManager a;

    private YoloV5Classifier detector;

    public static final String TAG = "yolov5demo_4";

    private Context mContext;
    private int mask_1=0,person_1=0;
    private int mask=0,person;
    String text3[] = new String[50];
    String MaskString[] = new String[50];
    public yolov5demo_4(AssetManager a){
        this.a = a;
    }

    public String start(Bitmap bitmap){
        //   模型初始化
        try {
            detector = DetectorFactory.getDetector(this.a,"yolov5_4/test.tflite");
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
        String Mask = "口罩";
        String Person = "行人";
        int j = 0;
        for (Classifier.Recognition result : results){

                String characterAtIndex = result.toString().substring(4, 5);
                Log.d("String","characterAtIndex = "+characterAtIndex);
                switch (characterAtIndex) {
                    case "m":
                        mask = 1;
                        mask_1++;
                        text3[j] = Mask.toString(); j++;
                        Log.d("Cart", "口罩 = " + characterAtIndex + "  and  第" + mask + "个标签");
                        break;
                    case "p":
                        person = 2;
                        person_1++;
                        text3[j] = Person.toString(); j++;
                        Log.d("Cart", "行人 = " + characterAtIndex + "  and  第" + person + "个标签");
                        break;
                }

            Log.d("口罩和行人数量", "口罩 = " + mask_1 + " 个" + "行人 = " + person_1 + " 个" );
            text1 += result.toString() + "\n";
            text2 = Mask.toString() + "=" + mask_1 + "个" + "\n"  + Person.toString() + "=" + person_1 + "个";

        }
        return text2;
    }

      public String[] MaskPersonString(){
        for (int i = 0;i < 20;i++){
            MaskString[i] = text3[i];

        }
          return MaskString;
      }



}
