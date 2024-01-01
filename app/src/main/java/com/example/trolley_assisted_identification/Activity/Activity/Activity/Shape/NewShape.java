package com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class NewShape {


    //膨胀腐蚀后的RGB
    public static double[][] HSV_VALUE_LOW = {
            {80,210,240},
            {100,240,240},//深蓝
            {50,200,200},//绿

    };

    public static double[][] HSV_VALUE_HIGH = {
            {100,250,255},
            {120,255,255},
            {70,255,255},
    };



    public static String getShapeNum(Bitmap bmp) {
        //  Bitmap bitmap = ToolCv.getTFT(bmp,"1231a");
        Bitmap bitmap = bmp;


        Mat target = shape_cv.BitmapToMat(bitmap);
        Mat hsv_img = target.clone();
        //Imgproc.cvtColor(hsv_img,hsv_img,Imgproc.COLOR_RGB2BGR);//Hsv颜色空间转换
        Imgproc.cvtColor(hsv_img, hsv_img, Imgproc.COLOR_RGB2HSV);//Hsv颜色空间转换
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));//  指定腐蚀膨胀核
        Mat blue = new Mat();
        Mat erode1 = new Mat();
        Imgproc.dilate(hsv_img, erode1, kernel1);

        MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(erode1));
        //
        //img2 的RGB值填入Scalar的参数
        //
        Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));//  指定腐蚀膨胀核

        Core.inRange(erode1, new Scalar(HSV_VALUE_LOW[2]), new Scalar(HSV_VALUE_HIGH[2]), blue);
        Imgproc.morphologyEx(blue, blue, Imgproc.MORPH_ERODE, kernel2, new Point(-1, -1), 5);

        MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(blue));

        List<MatOfPoint> contourstest = new ArrayList<MatOfPoint>();
        Mat hierarchytest = new Mat();
        Imgproc.findContours(blue, contourstest, hierarchytest,
                Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓

        Iterator<MatOfPoint> eachs = contourstest.iterator();


        for (MatOfPoint c : contourstest) {
            int area = 0;
            area = (int) Imgproc.contourArea(c);
            Log.i("ShapeDetect", "识别面积：" + area);


            return null;
        }
        return null;
    }
}
