package com.example.trolley_assisted_identification.Activity.Activity.Activity.Util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/*
   图像处理工具类
*/
public class OpenCV_ocr {

    private static final String TAG = "OpenCV_ocr_Util";
    private Bitmap bitmap1;
    private Bitmap bitmap2;

    public static double[][] HSV_VALUE_LOW = {
            {10,163,147},//浅蓝0
            {77, 163, 147},//黄色1
            {146, 212, 140},//品红2
            {126,155, 160},//浅红色3
            {0, 204, 178},//蓝色4
            {35, 163, 147},//青色5
            {110,155,160},// 深红色6
            {0,0,0},//黑色7
            {0,0,192},//标准蓝8
            {0,190,190},//车牌蓝底9      暗的TFT：0,190,190   亮的：0,180,190
            {22,195,158}//车牌绿底10    暗的TFT H:21 S要调高一点:210  V:211  亮的TFT S值要调底一点：110    10,100,148
    };

    public static double[][] HSV_VALUE_HIGH = {
            {47,255,255},//浅蓝0
            {111, 255,255},//黄色1
            {241, 255, 255.0},//品红2
            {150,255, 255},//浅红色3
            {21, 255, 255},//蓝色4
            {75, 255.0, 255},//青色5
            {150,255,255},// 深红色6
            {180,255,120},//黑色7
            {45,238,255},//标准蓝8
            {28,255,255},//车牌蓝底9   亮暗一样
            {73,255,255}//车牌绿底10   暗H:66     亮H:83
    };



    //  OpenCV图像处理        bitmap传入bitmap图像         temp  传入识别处理模式 1 是车牌，2是//  2 是TFT   Yolov5模型
    public void setBitmap1(Bitmap bitmap,int temp) {

        Bitmap bmp = bitmap;
        Mat mat = new Mat();
        //   将Bitmap格式转成Mat
        Utils.bitmapToMat(bmp, mat);

        Mat gray = new Mat();
        // 灰度化处理
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        Log.d(TAG, "灰度化处理");

//        Mat blurredMat = new Mat();
//        Imgproc.bilateralFilter(gray,blurredMat,9,75,75);
//        Log.d(TAG,"双边降噪滤波处理");
//
//        // 使用Canny边缘检测
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);
        Log.d(TAG, "Canny边缘检测处理");
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));//  指定腐蚀膨胀核
        Imgproc.dilate(edges, edges, kernel);
        Log.d(TAG, "指定腐蚀膨胀核处理");
//
//        // 寻找轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);



//        // 对轮廓进行聚类处理
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
            }
        }
        Log.e(TAG, "对轮廓进行聚类处理 :" + maxArea);
//
        Mat result = null;
        each = contours.iterator();
//
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double area = Imgproc.contourArea(contour);
            Log.e(TAG, "area :" + area);
            if (area > 0.01 * maxArea) {
                // 多边形逼近 会使原图放大4倍
                Core.multiply(contour, new Scalar(4, 4), contour);
                MatOfPoint2f newcoutour = new MatOfPoint2f(contour.toArray());
                MatOfPoint2f resultcoutour = new MatOfPoint2f();
                double length = Imgproc.arcLength(newcoutour, true);
                Double epsilon = 0.01 * length;
                Imgproc.approxPolyDP(newcoutour, resultcoutour, epsilon, true);
                contour = new MatOfPoint(resultcoutour.toArray());
                // 进行修正，缩小4倍改变联通区域大小
                MatOfPoint new_contour = new MatOfPoint();
                new_contour = ChangeSize(contour);
                double new_area = Imgproc.contourArea(new_contour);//轮廓的面积
                Log.e(TAG, "轮廓的面积 " + new_area);
                // 求取中心点
                Moments mm = Imgproc.moments(contour);
                int center_x = (int) (mm.get_m10() / (mm.get_m00()));
                int center_y = (int) (mm.get_m01() / (mm.get_m00()));
                Log.e(TAG, "中心点: " + center_x + "+" + center_y);
                Point center = new Point(center_x, center_y);

                //最小外接矩形
                Rect rect = Imgproc.boundingRect(new_contour);
                double rectarea = rect.area();//最小外接矩形面积
                Log.e(TAG, "最小外接矩形面积" + rectarea);
                if (Math.abs((new_area / rectarea) - 1) < 0.2) {
                    double wh = rect.size().width / rect.size().height;//宽高比值

                    if (Math.abs(wh - 1.7) < 0.7 && rect.width > 250) {
                        // 绘图///
                        Mat imgSource = mat.clone();
                        // 绘制外接矩形
                        Imgproc.rectangle(imgSource, rect.tl(), rect.br(),
                                new Scalar(0, 0, 255), 2);
                        //*****图片裁剪***可以封装成函数*****************
                        rect.x += 5;
                        rect.width -= 25;
                        rect.y += 2;
                        rect.height -= 3;
                        result = new Mat(imgSource, rect);

                        //*****图片裁剪***可以封装成函数*****************
                        Imgproc.pyrUp(result, result);//向上采样,放大图片
                    }
                }
            }
        }


        if (result != null) {
            Log.e(TAG, "TFT屏幕裁剪成功: ");
            if(temp == 1){         //  1 是车牌
//                Mat edges1 = new Mat();
//                Imgproc.cvtColor(result, edges1, Imgproc.COLOR_BGR2GRAY);//灰度
//                Core.bitwise_not(edges1, edges1);
//                Mat thresholdImage = new Mat();
//                Imgproc.threshold(edges1, thresholdImage, 95, 200, Imgproc.THRESH_BINARY); // 使用阈值处理
                bmp = Bitmap.createBitmap(result.width(),result.height(),Bitmap.Config.ARGB_8888);
                //   将Mat格式转成Bitmap
                Utils.matToBitmap(result,bmp);
//                // 创建一个Canvas对象，用于在新的Bitmap上绘制图像
//                Canvas canvas = new Canvas(bmp);
//                // 创建一个Paint对象
//                Paint paint = new Paint();
//
//                // 应用颜色矩阵来增强图像的对比度和清晰度
//                ColorMatrix colorMatrix = new ColorMatrix(new float[]{
//                        1.5f, 0, 0, 0, -20,
//                        0, 1.5f, 0, 0, -20,
//                        0, 0, 1.5f, 0, -20,
//                        0, 0, 0, 1.0f, 0
//                });
//                paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//
//                // 在Canvas上绘制处理后的图像
//                canvas.drawBitmap(bmp, 0, 0, paint);
                bitmap1 = bmp;
            }
            if(temp == 2){              //  2 是TFT   Yolov5模型
                bmp = Bitmap.createBitmap(result.width(),result.height(),Bitmap.Config.ARGB_8888);
                //   将Mat格式转成Bitmap
                Utils.matToBitmap(result,bmp);
                bitmap1 = bmp;
            }
        }else {
            if(temp == 1){                  //  1 是车牌
                bitmap1 = bitmap;
            }
            if(temp == 2){                  //  2 是TFT   Yolov5模型
                bitmap1 = bitmap;
            }

        }



        //   截取车牌的代码
        //  如果能截取到画面
//        if (result != null) {
//            Log.e(TAG, "TFT屏幕裁剪不为空");
//            Log.d("裁剪TFT后","能截取到画面");
//            Mat black_while = result.clone();//剪切后的图片复制一份
//            Mat black_while_gray = new Mat();//存储剪切后的图片灰度化
//            Imgproc.cvtColor(black_while, black_while_gray, Imgproc.COLOR_BGR2GRAY);//灰度化图片
//            Log.d("裁剪TFT后","灰度化图片");
//            Imgproc.GaussianBlur(black_while_gray, black_while_gray,new Size(5, 5), 0.5, 0.5);//高斯模糊
//            Log.d("裁剪TFT后","高斯模糊");
//            Mat imgsobel = new Mat();
//            Imgproc.Sobel(black_while_gray, imgsobel, CvType.CV_8U, 1, 0, 3);//利用sobel滤波，对x进行求导，就是强调Y方向
//            Log.d("裁剪TFT后","利用sobel滤波");
//            Imgproc.threshold(imgsobel, imgsobel, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);// 二值化
//            Log.d("裁剪TFT后","二值化");
//            // 闭操作
//            Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(21, 5));
//            Imgproc.morphologyEx(imgsobel, imgsobel, Imgproc.MORPH_CLOSE, element);
//            Log.d("裁剪TFT后","闭操作");
//            // 寻找轮廓
//            List<MatOfPoint> contours1 = new ArrayList<>();
//            Mat hierarchy1 = new Mat();
//            Imgproc.findContours(imgsobel, contours1, hierarchy1, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//            Log.d("裁剪TFT后","寻找轮廓");
//            // 用来存放旋转矩形的容器
//            List<RotatedRect> rotatedRects = new ArrayList<>();
//
//            for (int i = 0; i < contours1.size(); i++) {
//                Point[] vertices = new Point[4];
//                // 寻找最小矩形
//                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(contours1.get(i).toArray()));
//                // 判断是不是要找的区域，如果是画线
//                if (VerifySize(rotatedRect) == 1) {
//                    rotatedRect.points(vertices);
//                    //在源图上画四点的线
//                    for (int j = 0; j < 4; j++) {
//                        Imgproc.line(black_while,vertices[j], vertices[(j + 1) % 4],new Scalar(0, 0, 255), 3);
//                    }
//                    //将符合的矩形存放到容器里
//                    rotatedRects.add(rotatedRect);
//                }
//            }
//            Log.d("裁剪TFT后","寻找最小矩形");
//            // 用于存放识别到的图像
//            List<Mat> output = new ArrayList<>();
//            for (int i = 0; i < rotatedRects.size(); i++) {
//                Mat dst_warp = new Mat();
//                Mat dst_warp_rotate = new Mat();
//                Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
//                dst_warp = Mat.zeros(black_while.size(), black_while.type());
//                float r = (float) rotatedRects.get(i).size.width / (float) rotatedRects.get(i).size.height;
//                float angle = (float)rotatedRects.get(i).angle;
//                if (r < 1)
//                    angle = angle + 0;
//
//                // 其中的angle参数，正值表示逆时针旋转，关于旋转矩形的角度，以为哪个是长哪个是宽，在下面会说到
//                rotMat = Imgproc.getRotationMatrix2D(rotatedRects.get(i).center, angle, 1);
//                // 将矩形通过仿射变换修正回来
//                Imgproc.warpAffine(black_while, dst_warp_rotate, rotMat, dst_warp.size());
//                Log.d("裁剪TFT后","将矩形通过仿射变换修正回来");
//                Size rect_size = rotatedRects.get(i).size;
//                if (r < 1){
//                    // 交换rect_size的宽和高
//                    double temp = rect_size.width;
//                    rect_size.width = rect_size.height;
//                    rect_size.height = temp;
//                }
//                // 定义输出的图像
//                Mat dst = new Mat(rotatedRects.get(i).size, CvType.CV_8U);
//                // 裁剪矩形，下面的函数只支持CV_8U或者CV_32F格式的图像输入输出。
//                // 所以要先转换图像将RGBA改为RGB
//                Imgproc.cvtColor(dst_warp_rotate, dst_warp_rotate, Imgproc.COLOR_RGBA2RGB);
//                // 裁剪矩形
//                Imgproc.getRectSubPix(dst_warp_rotate, rect_size, rotatedRects.get(i).center, dst);
//                Log.d("裁剪TFT后","裁剪矩形车牌");
//                // 将裁减到的矩形设置为相同大小，并且提高对比度
//                Mat resultResized = new Mat(33, 144, CvType.CV_8UC3);
//                Imgproc.resize(dst, resultResized, resultResized.size(), 0, 0, Imgproc.INTER_CUBIC);
//                Mat grayResult = new Mat();
//                Imgproc.cvtColor(resultResized, grayResult, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.blur(grayResult, grayResult, new Size(3, 3));
//                // 均值化提高对比度
//                Imgproc.equalizeHist(grayResult, grayResult);
//                Log.d("裁剪TFT后","均值化提高对比度");
//                // 最终生成的矩形存放进ArrayList中
//                output.add(grayResult);
//                Bitmap bmp5 = Bitmap.createBitmap(grayResult.width(), grayResult.height(), Bitmap.Config.ARGB_8888);
//                //   将Mat格式转成Bitmap
//                Utils.matToBitmap(grayResult, bmp5);
//                MainActivity.img3.setImageBitmap(bmp5);
//            }
////            bitmap1 = bmp;
//
//        }


    }


    // 把坐标降低到4分之一
    MatOfPoint ChangeSize(MatOfPoint contour) {
        for (int i = 0; i < contour.height(); i++) {
            double[] p = contour.get(i, 0);
            p[0] = p[0] / 4;
            p[1] = p[1] / 4;
            contour.put(i, 0, p);
        }
        return contour;
    }

    public int VerifySize(RotatedRect candidate) {
        float error = 0.2f; //20%的误差范围
        float aspect = 4.7272f;//宽高比例
        int min = (int)(15 * aspect * 15); //最小像素为15
        int max = (int)(125 * aspect * 125);//最大像素为125
        float rmin = aspect - aspect * error;//最小误差
        float rmax = aspect + aspect * error;//最大误差
        int area = (int)(candidate.size.height * candidate.size.width);//求面积
        float r = (float) candidate.size.width / (float) candidate.size.height;//长宽比
        if (r < 1) r = 1 / r;
        if (area < min || area > max || r < rmin || r > rmax
                || Math.abs(candidate.angle) > 10 || candidate.size.width < candidate.size.height) {
            return 0;
        } else {
            return 1;
        }
    }

    public Bitmap getBitmap1(){
        Log.d("OpenCv_ocr","返回车牌图片");
        return bitmap1;
    }


    static int[] angle = {0, 15, 30, 45, 60};
    public void  setBitmap2(Bitmap bitmap){
        Bitmap bmp = bitmap;
        Mat mat = new Mat();
        //   将Bitmap格式转成Mat
        Utils.bitmapToMat(bmp,mat);

        Mat mat1 = new Mat();
        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_BGR2GRAY);//灰度

        Mat mat2 = new Mat();
        Imgproc.threshold(mat1, mat1, 100, 200, Imgproc.THRESH_BINARY);
        Mat mat3 = new Mat();

        Point point = new Point(mat.width() / 2, mat.height() / 2);
        for (int x = 0; x < angle.length; x++) {

            mat2 = Imgproc.getRotationMatrix2D(point, angle[x], 0.8);//倾斜画面
            Log.e("angle", "度数" + angle[x]);
            Imgproc.warpAffine(mat1, mat3, mat2, new Size(mat1.width(), mat1.height()));

            bmp = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
            //   将Mat格式转成Bitmap
            Utils.matToBitmap(mat1, bmp);
        }
        bitmap2 = bmp;
    }

    public Bitmap getBitmap2(){
        Log.d("OpenCv_ocr","返回文字识别图片");
        return bitmap2;
    }


}
