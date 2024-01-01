package com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5;

import android.Manifest;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.Classifier;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.DetectorFactory;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.yolov5.tflite.YoloV5Classifier;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.io.IOException;
import java.util.List;


/*===============================     Yolov5训练的  “车牌目标检测”  识别模型    ===============================*/
public class yolov5demo_5 {
    private  final AssetManager a;
    private MainActivity mainActivity;
    private YoloV5Classifier detector;
    public static final String TAG = "yolov5demo_5";
    private int plate_1=0;
    private int plate=0;
    String text3[] = new String[50];
    String PlateString55[] = new String[50];

    private String resultText;
    public static   String[] str1 = new String[5];

    private Bitmap croppedBitmap[] = new Bitmap[5];

    private String std;

    private String strs1 ;

    private String result58;
    // 在类的成员变量中声明一个计数器
    private int completedTasks = 0;

    private int i = 0,z = 0,x = 0, num = 0 , y = 0;
    public yolov5demo_5(AssetManager a){
        this.a = a;
    }
    public void start(Bitmap bitmap){
        z = 0;num = 0;x=0;
        Bitmap[] bitmaps = new Bitmap[5];
        for (i = 0;i<5;i++){
            bitmaps[i] = bitmap;
        }
        //   模型初始化
        try {
            detector = DetectorFactory.getDetector(this.a,"yolov5_5/test.tflite");
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
        String Plate = "车牌";
        int j = 0;

        for (Classifier.Recognition result : results){

                String characterAtIndex = result.toString().substring(4, 5);
                Log.d("String","characterAtIndex = "+characterAtIndex);
                switch (characterAtIndex) {
                    case "P":
                        plate = 1;
                        plate_1++;
                        text3[j] = Plate.toString(); j++;
                        Log.d("Cart", "车牌 = " + characterAtIndex + "  and  第" + plate + "个标签");
                        break;
                }
            Log.d("车牌数量", "车牌 = " + plate_1 + " 个");
            text1 += result.toString() + "\n";
            text2 = Plate.toString() + "=" + plate_1 + "个";
            RectF location = result.getLocation();
            // 获取目标框的坐标信息（left, top, right, bottom）
            float left = location.left;
            float top = location.top;
            float right = location.right;
            float bottom = location.bottom;
            Log.d("坐标"," L = " + left + " T = " + top + " R = " + right + " B = " + bottom);
            // 计算截取区域的宽度和高度
            int width = (int) (right - left);
            int height = (int) (bottom - top );
            // 创建一个新的 Bitmap，宽度和高度分别为 width 和 height
            croppedBitmap[x] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            // 创建一个 Canvas 对象，并将其与新的 Bitmap 关联
            Canvas canvas = new Canvas(croppedBitmap[x]);

            Paint paint = new Paint();// 设置抗锯齿

            paint.setAntiAlias(true);

            // 设置图像的色彩饱和度
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.2f);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);paint.setColorFilter(colorFilter);

            // 使用 Canvas 的 drawBitmap 方法将指定区域的图像截取到新的 Bitmap 中
            Rect srcRect = new Rect((int) left, (int) top, (int) right, (int) bottom );
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

            MainActivity.img2.setImageBitmap(croppedBitmap[x]);

            x++;

        }

        for (y = 0; y < croppedBitmap.length;y++){
            if (croppedBitmap[y] != null){
                // When using Latin script library
                TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//            TextRecognizer recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

                InputImage image = InputImage.fromBitmap(croppedBitmap[y],0);

                Task<Text> result1 = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        resultText += text.getText() + ",";
                        result58 = resultText.replaceAll("[^a-zA-Z0-9]", ""); // 使用正则表达式删除非字母和数字的字符
                        std = result58.toString();
                        str1[z] = result58.toString().substring(4,10);
                        Log.e(TAG,resultText);
                        Log.e(TAG,result58);
                        z++;
                        if (z == plate_1) {
                            // 所有任务都已完成，可以处理数据了
                            String std1 = std.toString().substring(4);
                            MainActivity.text1.setText(std1);
                            Log.e(TAG, std + "显示");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"车牌文本识别错误");

                    }
                });
                String lk = result1.toString();
                Log.e(TAG,lk);
            }

        }

//        MainActivity.text1.setText(strs1);


//        return croppedBitmap;
    }

      public String[] PlateString(){
        for (int i = 0;i < 20;i++){
            PlateString55[i] = text3[i];
        }
          return PlateString55;
      }
    public void requestPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int requestCode = 1;
        ActivityCompat.requestPermissions(mainActivity, permissions, requestCode);
    }


}
