package com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest.QR;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;


import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;


public class QRtest {

    private static String str = null;
    static int[] angle = {0, 15, 30, 45, 60};

    public static String decodeQR(Bitmap srcBitmap) {
        // com.google.zxing.Result[] result = null;
        Result[] result = null;
        String s = "未识别到二维码";
        int len = 0;
        Mat mat = new Mat();
        mat = shape_cv.BitmapToMat(srcBitmap);
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
            Bitmap bitmap = shape_cv.MatToBitmap(mat1);
            MainActivity.img2.setImageBitmap(bitmap);

            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                // 新建一个RGBLuminanceSource对象
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                // 将图片转换成二进制图片
                BinaryBitmap binaryBitmap = new BinaryBitmap(new
                        GlobalHistogramBinarizer(source));
                QRCodeMultiReader reader = new QRCodeMultiReader();// 初始化解析对象
                try {
                    result = reader.decodeMultiple(binaryBitmap);
                } catch (Resources.NotFoundException | NotFoundException e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    len = result.length;
                    s = "";


                    Log.e("QRsize", "" + len);
                    for (int i = 0; i < len; i++) {

//                        if (result[i].getText().equals('A'))

                        s += result[i].getText() + "!";
                    }

                }
            }

//
//        QRCodeMultiReader qrCodeMultiReader = new QRCodeMultiReader();
//        int[] data = new int[srcBitmap.getWidth() * srcBitmap.getHeight()];
//        srcBitmap.getPixels(data, 0, srcBitmap.getWidth(), 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
//        RGBLuminanceSource source = new RGBLuminanceSource(srcBitmap.getWidth(), srcBitmap.getHeight(), data);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
//        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
//        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
//        try {
//            result =  qrCodeMultiReader.decode(binaryBitmap, hints).getText();
//            if (result != null) {
//
//                s = "";
//
//
//                Log.e("QRsize", "" + result);
//                for (int i = 0; i < len; i++) {
//
////                        if (result[i].getText().equals('A'))
//
//                    s = result;
//                }
//            }
//
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        } catch (ChecksumException e) {
//            e.printStackTrace();
//        } catch (com.google.zxing.FormatException e) {
//            e.printStackTrace();
//        }
            //     return null;


            //}
            return s;
        }
        return null ;
    }

    public static void QrRun(Bitmap bitmap)
    {
        //   Bitmap bitmapTarget = ToolCv.getTFT(MainActivity.bitmap,"asd");
        //ToolCv.ColorSlecet(MainActivity.bitmap);


        if (bitmap!=null) {

            //   Bitmap bitmaps = ToolCv.ColorSlecet(MainActivity.bitmap);
            if (bitmap != null) {
                Bitmap preprocessedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(preprocessedBitmap);
                Paint paint = new Paint();


                // 设置抗锯齿
                paint.setAntiAlias(true);

//        // 设置图像的色彩饱和度
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0.3f);
                ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);paint.setColorFilter(colorFilter);
                // 去除模糊
                paint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));

                // 绘制调整后的图像
                canvas.drawBitmap(bitmap, 0, 0, paint);
                str = QRtest.decodeQR(preprocessedBitmap);



                MainActivity.text1.setText(str);
                //  MainActivity.Connect_Transport.voiceController(str);
                MainActivity.Connect_Transport.sendQR(str);

            }
        }
    }
}