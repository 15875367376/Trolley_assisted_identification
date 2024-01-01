package com.example.trolley_assisted_identification.Activity.Activity.Activity.color;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;


public class color_cv {
    int minArea = 1000;
    int maxArea = 20000;


    public static double[][] HSV_VALUE_LOW = {
            {40,45,130},
            {40,10,160}
    };

    public static double[][] HSV_VALUE_HIGH = {
            {90,100,210},
            {100,50,210}
    };

    public Bitmap color_A(Bitmap bitmap,Mat flag)
    {
        int tempW,tempH,resW,resH;
  
        Rect rect;

        Mat mat  = shape_cv.BitmapToMat(bitmap);//获取图像
        Log.e("color","Target: 高"+mat.height()+"  长 "+mat.width());
        Mat Kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(6,6));// 卷积盒
//        Mat Kernel = new Mat(new Size(3,3),CvType.CV_8UC1,new Scalar(255));


        Mat getFuncAfter  = new Mat();
        Imgproc.morphologyEx(mat,getFuncAfter,Imgproc.MORPH_GRADIENT,Kernel);
        //  MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(getFuncAfter));//输出图像




        Mat target  = flag;
        Log.e("color","Flag  高"+target.height()+"  长 "+target.width());
        tempH =target.height();
        tempW = target.width();
        resW = mat.rows()-target.rows()-1;
        resH = mat.cols()-target.cols()-1;
        Mat result = new Mat(resH,resW, CvType.CV_32FC1);

        Imgproc.matchTemplate(getFuncAfter,target,result,Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr;
        mmr =  Core.minMaxLoc(result);
        Mat mat2 = new Mat();
        mat.copyTo(mat2);

        Log.e("color","color亲和率"+mmr.maxVal);
        Point P1 = new Point(mmr.maxLoc.x,mmr.maxLoc.y);  //左上
        Point P2 = new Point(mmr.maxLoc.x+tempW,mmr.maxLoc.y+(tempH*1.5));//右下
        //画板
        Imgproc.rectangle(mat2,P1,P2,new Scalar(255,0,0),5);
        // MainActivity.img3.setImageBitmap(bitmap);//原图
        MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(mat2));//画板图
//
        int LeftX = (int) (mmr.maxLoc.x-((5+(mmr.maxVal*5))*tempW));
        int RightX = (int) mmr.maxLoc.x;
        // 裁剪部分

        rect = new Rect(0,(int) mmr.maxLoc.y-(tempH/4), mat.width()-(mat.width()-(int)mmr.maxLoc.x),(int)(tempH*1.5));


        if (rect!=null) {
            Mat results = new Mat(mat, rect);
            return shape_cv.MatToBitmap(results);
        }
        return null;
        //    return bitmap;
    }





    public Bitmap color_A(Bitmap bitmap)
    {

        Mat mat  =  shape_cv.BitmapToMat(bitmap);//获取图像
        Log.e("color","bitmap: 高"+mat.height()+"  长 "+mat.width());
        Mat Kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(2,2));// 卷积盒
//        Mat Kernel = new Mat(new Size(3,3),CvType.CV_8UC1,new Scalar(255));


        Mat getFuncAfter  = new Mat();
        Imgproc.morphologyEx(mat,getFuncAfter,Imgproc.MORPH_GRADIENT,Kernel);
  //  MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(getFuncAfter));//输出图像

        Mat  hsv_img = getFuncAfter.clone();
        Log.e("C3","test1");
        Imgproc.cvtColor(hsv_img,hsv_img,Imgproc.COLOR_RGB2HSV);//Hsv颜色空间转换
        Mat kernel2 =Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(6,6));//  指定腐蚀膨胀核
    //     MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(hsv_img));
        Mat blue = new Mat();
        Mat erode1 = new Mat();
//        Imgproc.dilate(hsv_img,erode1,kernel2);
        Log.e("C3","test2");
        Core.inRange(hsv_img,new Scalar(HSV_VALUE_LOW[0]),new Scalar(HSV_VALUE_HIGH[0]),blue);
        Imgproc.dilate(blue,erode1,kernel2);
        Imgproc.morphologyEx(erode1,erode1,Imgproc.MORPH_CLOSE,kernel2);
        Imgproc.dilate(erode1,erode1,kernel2);
        Imgproc.morphologyEx(erode1,erode1,Imgproc.MORPH_CLOSE,kernel2);
        Imgproc.dilate(erode1,erode1,kernel2);
        Imgproc.morphologyEx(erode1,erode1,Imgproc.MORPH_CLOSE,kernel2);
        Mat Addarea = new Mat();
        Imgproc.dilate(erode1,Addarea,kernel2);

        MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(Addarea));

        List<MatOfPoint> contourstest = new ArrayList<MatOfPoint>();
        Mat hierarchytest=new Mat();
        Imgproc.findContours(Addarea, contourstest, hierarchytest,
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓

        Imgproc.drawContours(Addarea,contourstest,
                -1,new Scalar(255,0,0),3);


        for (MatOfPoint c : contourstest) {
            int area = 0;
            area = (int) Imgproc.contourArea(c);
            Log.i("ShapeDetect", "识别面积：" + area);
            if (area > 600&&area < 1300) {
                Rect rects = Imgproc.boundingRect(new MatOfPoint(c.toArray()));

                Log.i("tagerdata", "x" +rects.x+"  y"+rects.y+"   width"+rects.width+"   height"+rects.height );
                if (rects.x>380) {

                    int x = rects.x - rects.width * 9;
                    if (x<0)x=0;

                    Rect rect = new Rect(220, (rects.y - 20), 230, rects.height * 2);
                    Mat res = new Mat(mat, rect);
               MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(res));
                    return shape_cv.MatToBitmap(res);


                }
            }
        }

        return null;
    }



    public static void color_Trun_On(Mat mat) {


    }
}
