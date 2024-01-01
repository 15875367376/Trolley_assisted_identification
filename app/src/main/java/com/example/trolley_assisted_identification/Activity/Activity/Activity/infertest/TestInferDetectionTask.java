package com.example.trolley_assisted_identification.Activity.Activity.Activity.infertest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.IInterface;
import android.widget.TextView;




import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import com.baidu.ai.edge.core.detect.DetectionResultModel;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;

import  com.baidu.ai.edge.core.infer.InferConfig;
import com.baidu.ai.edge.core.infer.InferManager;


/**
 * 通用arm 物体检测
 */
public class TestInferDetectionTask extends BaseTestTask<Void, CharSequence, CharSequence> {
    private static final int NUM_OF_RUNS = 1;
    private static final int NUM_OF_API_CALLS = 1;
    private static final float CONFIDENCE = 0.f;
    private static Bitmap bitmaps = null;
    public TestInferDetectionTask(Context context, TextView tv, String serialNum,Bitmap bitmap1) {
        super(context, tv, serialNum);
        bitmaps = bitmap1;
    }

    @SuppressLint("WrongThread")
    @Override
    protected CharSequence doInBackground(Void... voids) {
        publishProgress("\n\nARM Detection\n");

        Rect rect2;
        Mat mat2 = null;
        Point P2 ,P1;
        try {
            for (int i = 0; i < NUM_OF_RUNS; i++) {
                /* 以下逻辑请放在同一个线程里执行，比如使用ThreadHandler */

                publishProgress("\nStart running: " + i + "\n");

                /* 1. 准备配置类，初始化Manager类。可以在onCreate或onResume中触发，请在非UI线程里调用 */
                InferConfig config = new InferConfig(context.getAssets(), "com/baidu/ai/edge/core/infer");
                InferManager manager = new InferManager(context, config, serialNum);

                /* 2.1 准备图片，作为Bitmap输入 */
             //   InputStream is = context.getAssets().open("test.jpg");\
              //  MainActivity.img2.setImageBitmap(bitmaps);
                Bitmap image = bitmaps;
                mat2 = shape_cv.BitmapToMat(image);
             //   MainActivity.img2.setImageBitmap(image);
               // is.close();
                pLog("Image size: " + image.getWidth() + "*" + image.getHeight());

                /* 2.2 推理图片及解析结果 */
                List<DetectionResultModel> results = null;
                String resStr;
                for (int j = 0; j < NUM_OF_API_CALLS; j++) {
                    // 在模型销毁前可以不断调用。但是不支持多线程。
                    results = manager.detect(image, CONFIDENCE);

                    // 解析结果
                    if (results != null) {
                        resStr = "{size:" + results.size() + ", firstRes:{";
                        if (results.size() > 0) {
//                            resStr += "labelName:" + results.get(0).getLabel() + ", "
//                                    + "confidence:" + results.get(0).getConfidence() + ", "
//                                    + "bounds:" + results.get(0).getBounds();
                            rect2 = results.get(0).getBounds();
                            P1 = new Point(rect2.left,rect2.top);  //左上
                            P2 = new Point(rect2.right,rect2.bottom);//右下
                            Imgproc.rectangle(mat2,P1,P2,new Scalar(255,0,0),5);
                            MainActivity.confidence = results.get(0).getConfidence();
                            MainActivity.carflag = results.get(0).getLabel()+"亲和度"+"\n" +results.get(0).getConfidence();
                        }



                        resStr += "}}";
                    } else {
                        resStr = "{}";
                    }

                }

                /* 3. 销毁模型。可以在onDestroy或onPause中触发，请在非UI线程里调用 */
                manager.destroy();
                publishProgress("Finish running\n");
            }
            if (mat2!=null) {
                MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(mat2));

            }
//
            return RESULT_FIN;
        } catch (Exception e) {
            pError(e);
            return genErrStr("ERROR: " + e.getMessage());
        }
    }
}
