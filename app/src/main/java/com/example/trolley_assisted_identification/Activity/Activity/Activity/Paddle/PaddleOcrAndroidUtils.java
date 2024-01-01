package com.example.trolley_assisted_identification.Activity.Activity.Activity.Paddle;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;
import com.litongjava.android.paddle.ocr.OCRPredictorNative;
import com.litongjava.android.paddle.ocr.Predictor;
import com.litongjava.android.paddle.ocr.OcrResultModel;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/*
Paddle——Ocr识别工具类

public static String ocr(Bitmap image);      =========识别函数

 */
public class PaddleOcrAndroidUtils {

    private static Predictor predictor = new Predictor();

    private static final String TAG = "PaddleOcrAndroidUtils";

    public Bitmap bitmap1;

    static {
        // 加载类库和模型
        OCRPredictorNative.loadLibrary();
    }

    public static boolean init(Context context){
        return predictor.init(context);
    }

    public static void releaseModel(){
        predictor.releaseModel();
    }

    /*
    *  识别图片，返回数据
    *
    * @param image
    * @return
     */
    public static String ocr(Bitmap image){
        if (predictor.isLoaded()){
            predictor.setInputImage(image);
        }else{
            return null;
        }
        if (predictor.runModel()){
            String result = predictor.outputResult();
            Log.e(TAG,"识别结果:"+result);
            Bitmap bitmap = predictor.outputImage();
            if (bitmap != null){
//                Mat mat = new Mat();
//                //   将Bitmap格式转成Mat
//                Utils.bitmapToMat(bitmap,mat);
//
//                Mat mat1 = new Mat();
//                Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_BGR2GRAY);//灰度
//                Core.bitwise_not(mat1, mat1);
//                Mat edges = new Mat();
//                Imgproc.Canny(mat1, edges, 50, 150);
//                Log.d(TAG, "Canny边缘检测处理");
//                List<MatOfPoint> contours = new ArrayList<>();
//                Mat hierarchy = new Mat();
//                Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//                Mat result1 = Mat.zeros(edges.size(), CvType.CV_8UC3); // 创建一个空白的彩色图像
//
//                for (int i = 0; i < contours.size(); i++) {
//                    Scalar color = new Scalar(255, 255, 255); // 填充颜色为蓝色
//                    Imgproc.drawContours(result1, contours, i, color, -1); // 将边缘填充为指定颜色
//                }
//                bitmap = Bitmap.createBitmap(result1.width(), result1.height(), Bitmap.Config.ARGB_8888);
//                //   将Mat格式转成Bitmap
//                Utils.matToBitmap(result1, bitmap);
                MainActivity.img3.setImageBitmap(bitmap);
                Log.e(TAG,"OCR识别结果的图片显示");
            }

            return result;
        }
        return null;
    }

    public static Bitmap ocr1(Bitmap image){
        if (predictor.isLoaded()){
            predictor.setInputImage(image);
        }else{
            return null;
        }
        if (predictor.runModel()){
            String result = predictor.outputResult();
            Log.e(TAG,"识别结果:"+result);
            Bitmap bitmap = predictor.outputImage();
            if (bitmap != null){
                MainActivity.img3.setImageBitmap(bitmap);
                Log.e(TAG,"OCR识别结果的图片显示");
            }

            return bitmap;
        }
        return null;
    }

    public void setBitmap(Bitmap bitmap){
        bitmap1 = bitmap;
    }

    public Bitmap getBitmap1(){
        return bitmap1;
    }

}
