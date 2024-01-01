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



/*===============================     Yolov5训练的  “车型”  识别模型    ===============================*/
public class yolov5demo_3 {

    private  final AssetManager a;

    private YoloV5Classifier detector;

    private Context mContext;
    private int Motorcycle_1=0,Bike_1=0,Car_1=0,Truck_1=0;
    private int cart=0;
    String text3[] = new String[50];
    String CarString[] = new String[50];
    public yolov5demo_3(AssetManager a){
        this.a = a;
    }

    public String start(Bitmap bitmap){
        //   模型初始化
        try {
            detector = DetectorFactory.getDetector(this.a,"yolov5_3/test.tflite");
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
        String Motorcycle = "摩托车";
        String Bike = "自行车";
        String Car = "汽车";
        String Truck = "卡车";
        int j = 0;
        for (Classifier.Recognition result : results){

                String characterAtIndex = result.toString().substring(4, 5);
                Log.d("String","characterAtIndex = "+characterAtIndex);
                switch (characterAtIndex) {
                    case "M":
                        cart = 1;
                        Motorcycle_1++;
                        text3[j] = Motorcycle.toString(); j++;
                        Log.d("Cart", "摩托车 = " + characterAtIndex + "  and  第" + cart + "个标签");
                        break;
                    case "B":
                        cart = 2;
                        Bike_1++;
                        text3[j] = Bike.toString(); j++;
                        Log.d("Cart", "自行车 = " + characterAtIndex + "  and  第" + cart + "个标签");
                        break;
                    case "C":
                        cart = 3;
                        Car_1++;
                        text3[j] = Car.toString(); j++;
                        Log.d("Cartt", "汽车= " + characterAtIndex + "  and  第" + cart + "个标签");
                        break;
                    case "T":
                        cart = 4;
                        Truck_1++;
                        text3[j] = Truck.toString(); j++;
                        Log.d("Cart", "卡车 = " + characterAtIndex + "  and  第" + cart + "个标签");
                        break;
                }

            Log.d("车型数量", "摩托车 = " + Motorcycle_1 + " 个," + "自行车 = " + Bike_1 + " 个," + "汽车 = " + Car_1 + " 个," + "卡车 = " + Truck_1 + " 个,");
            text1 += result.toString() + "\n";
            text2 = Motorcycle.toString() + " = " + Motorcycle_1 + " 个 "   + Bike.toString() + " = " + Bike_1 + " 个 "  + Car.toString() + " = " + Car_1 + " 个 "
                    + Truck.toString() + " = " + Truck_1 + " 个 "  ;

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
