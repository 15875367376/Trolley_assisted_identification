package com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class ToolCv {

    //膨胀腐蚀后的RGB
    public static double[][] HSV_VALUE_LOW = {
            {50,0,40},
            {80,170,80}
    };

    public static double[][] HSV_VALUE_HIGH = {
            {95,60,95},
            {150,255,200}
    };

    public static Bitmap getTFT(Bitmap bmp,String str) {

        Mat mRgba = shape_cv.BitmapToMat(bmp);

        Mat gray = new Mat();
        Imgproc.cvtColor(mRgba, gray, Imgproc.COLOR_BGR2GRAY);//灰度化

        Mat binary = new Mat();
        Imgproc.Canny(gray, binary,     50, 100);//二值化  边缘检测

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));//  指定腐蚀膨胀核
        Imgproc.dilate(binary, binary, kernel);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binary, contours, hierarchy,
                Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓
          //   MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(binary));
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
            }
        }
        Mat result = null;
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            double area = Imgproc.contourArea(contour);
            if (area > 0.01 * maxArea) {

                Log.e("T1","1");
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
                // 求取中心点
                Moments mm = Imgproc.moments(contour);
                int center_x = (int) (mm.get_m10() / (mm.get_m00()));
                int center_y = (int) (mm.get_m01() / (mm.get_m00()));
                Point center = new Point(center_x, center_y);

                //最小外接矩形
                Rect rect = Imgproc.boundingRect(new_contour);
                double rectarea = rect.area();//最小外接矩形面积
                //轮廓的面积/最小外接矩形面积(一个圆和一个圆的外接矩形)  一定小于1 一般为0.1 0.2

                Log.e("T1","value1: "+(Math.abs((new_area / rectarea) - 1)));
                if (Math.abs((new_area / rectarea) - 1) < 0.2) {

                    Log.e("T1","2");
                    double wh = rect.size().width / rect.size().height;//宽高比值

                    if (Math.abs(wh - 1.7) < 0.7 && rect.width > 250) {


                        Log.e("T1","3");
                        Mat imgSource = mRgba.clone();
                        // 绘制外接矩形
                        Imgproc.rectangle(imgSource, rect.tl(), rect.br(),
                                new Scalar(0, 0, 255), 2);
                        //*****图片裁剪***可以封装成函数*****************
                        rect.x += 5;
                        //rect.width -= 25;
                        rect.y += 2;// 10
                        rect.height -= 3; //12
                        result = new Mat(imgSource, rect);

                   //     MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(result));


                        Mat black_while = result.clone();//剪切后的图片复制一份
                        Mat black_while_gray = new Mat();//存储剪切后的图片灰度化
                        Imgproc.cvtColor(black_while, black_while_gray, Imgproc.COLOR_BGR2GRAY);//灰度化图片

                        Mat hsv_gray_mask = new Mat();//存储二值化后的图片
                        Imgproc.threshold(black_while_gray, hsv_gray_mask, 50, 255, Imgproc.THRESH_BINARY);

                        Imgproc.resize(hsv_gray_mask, hsv_gray_mask, new Size(303, 183));//放大规定的大小，

                        //*****图片裁剪***可以封装成函数*****************
                        Imgproc.pyrUp(result, result);//向上采样,放大图片
                    }
                }
            }
        }


        if (result != null) {
            if (str != "a") {
                return shape_cv.MatToBitmap(result);
            }
            else return shape_cv.MatToBitmap8888(result);//ARGB8888 交通标志接口规定
        }
        return null;
    }







    // 把坐标降低到4分之一
     static MatOfPoint  ChangeSize(MatOfPoint contour) {
        for (int i = 0; i < contour.height(); i++) {
            double[] p = contour.get(i, 0);
            p[0] = p[0] / 4;
            p[1] = p[1] / 4;
            contour.put(i, 0, p);
        }
        return contour;
    }

        //选择不同色二维码
    public static Bitmap ColorSlecet(Bitmap bitmap)
    {

        Mat target =shape_cv.BitmapToMat(bitmap);
        //蓝色4阈值分割
        Mat  hsv_img = target.clone();
        Log.e("C3","test1");
        Imgproc.cvtColor(hsv_img,hsv_img,Imgproc.COLOR_BGR2HSV);//Hsv颜色空间转换
        Mat kernel2 =Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(2,2));//  指定腐蚀膨胀核
       // MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(hsv_img));
        Mat blue = new Mat();
        Mat erode1 = new Mat();
        Imgproc.dilate(hsv_img,erode1,kernel2);
        Log.e("C3","test2");
    //    MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(erode1));
        //
        //img2 的RGB值填入Scalar的参数
        //
        Core.inRange(erode1,new Scalar(HSV_VALUE_LOW[0]),new Scalar(HSV_VALUE_HIGH[0]),blue);
        Mat kernel =Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(4,4));//  指定腐蚀膨胀核
        Mat mat3 = new Mat();
        Imgproc.dilate(blue,mat3,kernel);
        MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(mat3));

  //      Imgproc.erode(blue,blue,kernel);
//        Imgproc.erode(blue,blue,kernel);
//        Imgproc.dilate(blue,blue,kernel2);
       // MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(blue));
        List<MatOfPoint> contourstest = new ArrayList<MatOfPoint>();
        Mat hierarchytest=new Mat();
        Imgproc.findContours(mat3, contourstest, hierarchytest,
        Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓

        Imgproc.drawContours(mat3,contourstest,
                -1,new Scalar(255,0,0),3);
    //    MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(blue));
        Iterator<MatOfPoint> eachs = contourstest.iterator();


        for (MatOfPoint c : contourstest) {
            int area = 0;
            area = (int) Imgproc.contourArea(c);
            Log.i("ShapeDetect", "识别面积：" + area);
            //300、800、500  if (area > 500&&800<area)
            //识别面积 消除误检
            if (area > 1000&&area < 150000) {
            Mat mat = new Mat();

            mat  = sideNum(target,new MatOfPoint2f(c.toArray()));
            if (mat!=null)
             //  MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(mat));
               return shape_cv.MatToBitmap(mat);

         //   return null;
            }

        }
            return null;


//        while(eachs.hasNext()) {
//
//
//            Rect rects = Imgproc.boundingRect(eachs.next());
//            Log.e("QRtest","轮廓大小"+rects.area()+"   ");
//            if (rects.area()>=2000) {
//                Mat bules = new Mat(target, rects);
//                return shape_cv.MatToBitmap(bules);
//            }
//        } return null;
    }


//    //选择指定颜色的车牌
//    public static Bitmap ColorSlecet(Bitmap bitmap,int i)
//    {
//        Mat target =shape_cv.BitmapToMat(bitmap);
//        //蓝色4阈值分割
//        Mat  hsv_img = target.clone();
//
//        Imgproc.cvtColor(hsv_img,hsv_img,Imgproc.COLOR_BGR2HSV);//Hsv颜色空间转换
//        Mat blue = new Mat();
//        Core.inRange(hsv_img,new Scalar(HSV_VALUE_LOW[0]),new Scalar(HSV_VALUE_HIGH[0]),blue);
//        Mat kernel=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));//  指定腐蚀膨胀核
//        Imgproc.erode(blue,blue,kernel);
//        Imgproc.erode(blue,blue,kernel);
//
//        List<MatOfPoint> contourstest = new ArrayList<MatOfPoint>();
//        Mat hierarchytest=new Mat();
//        Imgproc.findContours(blue, contourstest, hierarchytest,
//                Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);//查找轮廓
//
//        Iterator<MatOfPoint> eachs = contourstest.iterator();
//
//        Rect rects = Imgproc.boundingRect(eachs.next());
//        Mat bules = new Mat(target,rects);
//        MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(bules));
//
//        return shape_cv.MatToBitmap(bules);
//
//    }


    public static Mat sideNum(Mat canny_new_img,MatOfPoint2f c1) {


        double area = 0;
        area = Imgproc.contourArea(c1);
        Moments moments = Imgproc.moments(c1);
        //计算轮廓中心
        int cx = (int) (moments.m10 / moments.m00);
        int cy = (int) (moments.m01 / moments.m00);

      //  Imgproc.circle(canny_new_img, new Point(cx, cy), 5, new Scalar(255, 0, 0), -1);


        //       MainActivity.img2.setImageBitmap(MatToBitmap(canny_new_img));

        //MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(canny_new_img));
        //计算轮廓的周长
        double peri = Imgproc.arcLength(c1, true);
        //double side_lenght=peri/4;//计算出菱形或者正方形的边长，用于判断菱形与正方形和矩形的区别
        MatOfPoint2f approx = new MatOfPoint2f();
        //得到大概值
        Imgproc.approxPolyDP(c1, approx, 0.028 * peri, true);
        Log.e("side",approx.toList().size()+"边");
        //如果是三角形形状，则有三个顶点
        if (approx.toList().size() >=4&&approx.toList().size()<=8) {
            Rect rects = Imgproc.boundingRect(new MatOfPoint(c1.toArray()));
            Rect rect = new Rect(rects.x-10,rects.y-10,rects.width+20,rects.height+20);
            Mat bules = new Mat(canny_new_img,rect);
          //  MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(bules));
            return bules;

        }
        return null;
    }



    //反色
    public static Bitmap invertBitmap(Bitmap bitmap) {
            int    sWidth = bitmap.getWidth();
        int  sHeight = bitmap.getHeight();
        int [] sPixels = new int[sWidth * sHeight];
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);

      int   sIndex = 0;
        for (int sRow = 0; sRow < sHeight; sRow++) {
            sIndex = sRow * sWidth;
            for (int sCol = 0; sCol < sWidth; sCol++) {
              int   sPixel = sPixels[sIndex];
              int sA = (sPixel >> 24) & 0xff;
               int  sR = (sPixel >> 16) & 0xff;
               int  sG = (sPixel >> 8) & 0xff;
               int  sB = sPixel & 0xff;

                sR = 255 - sR;
                sG = 255 - sG;
                sB = 255 - sB;

                sPixel = ((sA & 0xff) << 24 | (sR & 0xff) << 16 | (sG & 0xff) << 8 | sB & 0xff);

                sPixels[sIndex] = sPixel;

                sIndex++;
            }
        }
        bitmap.setPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);
        MainActivity.img2.setImageBitmap(bitmap);
        return bitmap;
    }



    //保存bitmap到相册
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
