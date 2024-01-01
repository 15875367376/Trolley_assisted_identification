package com.example.trolley_assisted_identification.Activity.Activity.Activity.color.CarNum;


import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.PlateInfo;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.ToolCv;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.Shape.shape_cv;
//import com.example.trolley_assisted_identification.Activity.Activity.Activity.PlateInfo;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.PlateRecognition;



public class CarNumAll {

   static int [] angle = {-30,-15,0,15,30};


          public static String  str ;

        public static void CarNumRun(String color,Bitmap bitmap)
        {

                    str = CarHandle(bitmap,color);

            //    MainActivity.text1.setText(str);
                if (str != null) {
                    MainActivity.Connect_Transport.SendCarNum(str);
                }
                    else {
                    MainActivity.Connect_Transport.SendNull((byte) 0x03);
                }
        }

        public static String CarHandle(Bitmap bitmap,String color)
        {

            String  res = null ;
            Mat mat = new Mat();
            mat = shape_cv.BitmapToMat(ToolCv.getTFT(bitmap,"111"));

            Point point = new Point(mat.width()/2,mat.height()/2);
            Mat mat2 = new Mat();
            for (double i = -45 ; i <45;i++) {
                mat2 = Imgproc.getRotationMatrix2D(point, i, 1.0);//倾斜画面
               Log.e("angle","度数"+i);
                Mat mat3 = new Mat();
                Imgproc.warpAffine(mat, mat3, mat2, new Size(mat.width(), mat.height()));
//                MainActivity.img3.setImageBitmap(shape_cv.MatToBitmap(mat3));
                 res = null;
                res = SimpleRecog(shape_cv.MatToBitmap(mat3), 10,color);
                Log.e("angleRes","结果？"+res);
                if (res != null) {
                    return res;
                }
            }
            return null;

        }



    public static String SimpleRecog(Bitmap bmp,int dp,String color)
    {
       String str = null;


       if (color=="G") {
           Mat mat = new Mat();
           mat = shape_cv.BitmapToMat(bmp);
           Mat mat1 = new Mat();
           Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_BGR2GRAY);//灰度
           bmp = ToolCv.invertBitmap(shape_cv.MatToBitmap(mat1));
           Log.e("C3","灰度");
       }
        float dp_asp  = dp/10.f;
//        imgv.setImageBitmap(bmp);
        Mat mat_src = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC4);


        float new_w = (float) (bmp.getWidth()*dp_asp*1.125);
//
//        if (color=="B") {
//            new_w*=1.2;
//            Log.e("C3","更宽");
//        }
        float new_h = (float) (bmp.getHeight()*dp_asp);
        Size sz = new Size(new_w,new_h);
        Utils.bitmapToMat(bmp, mat_src);
        Imgproc.resize(mat_src,mat_src,sz);
     //   MainActivity.img3.setImageBitmap(bmp);
        MainActivity.img2.setImageBitmap(shape_cv.MatToBitmap(mat_src));



        long currentTime1 = System.currentTimeMillis();

        PlateInfo plateInfo = PlateRecognition.PlateInfoRecognization(mat_src.getNativeObjAddr(),MainActivity.handle);
       MainActivity.img3.setImageBitmap(plateInfo.bitmap);
        if (plateInfo.plateName!=null) {
            Log.e("C2",plateInfo.plateName);
            String result = GetTarget(plateInfo.plateName);
            if (result!=null) {

                str = result;
                MainActivity.text1.setText(str);
            }
            else {
                MainActivity.text1.setText("错误车牌 ： " + plateInfo.plateName);
            }
        }else {
            MainActivity.text1.setText("识别结果不到车牌 ");
        }
        long diff = System.currentTimeMillis() - currentTime1;

        return str;

    }





    public static StringBuffer  strbuf ;
    public static String GetTarget(String str)
    {

        strbuf = new StringBuffer(str);
        Log.e("C1",strbuf.length()+"");
        if (strbuf.length()>=6) {
            for (int i = 0; i <= strbuf.length() - 6; i++) {
                if (getSTR(strbuf.charAt(i))) {
                    Log.e("C1","0位");
                    if (strbuf.charAt(i + 1) == 'I') {
                        strbuf.setCharAt(i + 1, '1');
                    }
                    if (strbuf.charAt(i + 1) == 'Z') {
                        strbuf.setCharAt(i + 1, '2');
                    }
                    if (strbuf.charAt(i + 1) == 'A') {
                        strbuf.setCharAt(i + 1, '4');
                    }
                    if (strbuf.charAt(i + 1) == 'Q') {
                        strbuf.setCharAt(i + 1, '0');
                    }
                    if (strbuf.charAt(i + 1) == 'B') {
                        strbuf.setCharAt(i + 1, '8');
                    }
                    if (strbuf.charAt(i + 1) == 'D') {
                        strbuf.setCharAt(i + 1, '0');
                    }
                    if (strbuf.charAt(i + 1) == 'G') {
                        strbuf.setCharAt(i + 1, '6');
                    }
                    if (strbuf.charAt(i + 1) == 'S') {
                        strbuf.setCharAt(i + 1, '5');
                    }
                    if (getNUM(strbuf.charAt(i + 1))) {
                        Log.e("C1","1位");
                        if (strbuf.charAt(i + 2) == 'I') {
                            strbuf.setCharAt(i + 2, '1');
                        }
                        if (strbuf.charAt(i + 2) == 'A') {
                            strbuf.setCharAt(i + 2, '4');
                        }

                        if (strbuf.charAt(i + 2) == 'Z') {
                            strbuf.setCharAt(i + 2, '2');
                        }
                        if (strbuf.charAt(i + 2) == 'Q') {
                            strbuf.setCharAt(i + 2, '0');
                        }
                        if (strbuf.charAt(i + 2) == 'B') {
                            strbuf.setCharAt(i + 2, '8');
                        }
                        if (strbuf.charAt(i + 2) == 'D') {
                            strbuf.setCharAt(i + 2, '0');
                        }
                        if (strbuf.charAt(i + 2) == 'G') {
                            strbuf.setCharAt(i + 2, '6');
                        }
                        if (strbuf.charAt(i + 2) == 'S') {
                            strbuf.setCharAt(i + 2, '5');
                        }

                        if (getNUM(strbuf.charAt(i + 2))) {
                            Log.e("C1","2位");
                            if (strbuf.charAt(i + 3) == 'I') {
                                strbuf.setCharAt(i + 3, '1');
                            }
                            if (strbuf.charAt(i + 3) == 'A') {
                                strbuf.setCharAt(i + 3, '4');
                            }

                            if (strbuf.charAt(i + 3) == 'Z') {
                                strbuf.setCharAt(i + 3, '2');
                            }
                            if (strbuf.charAt(i + 3) == 'Q') {
                                strbuf.setCharAt(i + 3, '0');
                            }
                            if (strbuf.charAt(i + 3) == 'B') {
                                strbuf.setCharAt(i + 3, '8');
                            }
                            if (strbuf.charAt(i + 3) == 'D') {
                                strbuf.setCharAt(i + 3, '0');
                            }
                            if (strbuf.charAt(i + 3) == 'G') {
                                strbuf.setCharAt(i + 3, '6');
                            }
                            if (strbuf.charAt(i + 3) == 'S') {
                                strbuf.setCharAt(i + 3, '5');
                            }
                            if (getNUM(strbuf.charAt(i + 3))) {
                                Log.e("C1","3位");
                                if (strbuf.charAt(i + 4) == '4') {
                                    strbuf.setCharAt(i + 4, 'A');
                                }

                                if (strbuf.charAt(i + 4) == '2') {
                                    strbuf.setCharAt(i + 4, 'Z');
                                }
                                if (strbuf.charAt(i + 4) == '1') {
                                    strbuf.setCharAt(i + 4, 'I');
                                }
                                if (strbuf.charAt(i + 4) == '8') {
                                    strbuf.setCharAt(i + 4, 'B');
                                }
                                if (strbuf.charAt(i + 4) == '0') {
                                    strbuf.setCharAt(i + 4, 'Q');
                                }
                                if (strbuf.charAt(i + 4) == '6') {
                                    strbuf.setCharAt(i + 4, 'G');
                                }
                                if (strbuf.charAt(i + 4) == '5') {
                                    strbuf.setCharAt(i + 4, 'S');
                                }
                                if (getSTR(strbuf.charAt(i + 4))) {
                                    Log.e("C1","4位");
                                    if (strbuf.charAt(i + 5) == 'A') {
                                        strbuf.setCharAt(i + 5, '4');
                                    }

                                    if (strbuf.charAt(i + 5) == 'Z') {
                                        strbuf.setCharAt(i + 5, '2');
                                    }
                                    if (strbuf.charAt(i + 5) == 'Q') {
                                        strbuf.setCharAt(i + 5, '0');
                                    }
                                    if (strbuf.charAt(i + 5) == 'I') {
                                        strbuf.setCharAt(i + 5, '1');
                                    }
                                    if (strbuf.charAt(i + 5) == 'B') {
                                        strbuf.setCharAt(i + 5, '8');
                                    }
                                    if (strbuf.charAt(i + 5) == 'D') {
                                        strbuf.setCharAt(i + 5, '0');
                                    }
                                    if (strbuf.charAt(i + 5) == 'G') {
                                        strbuf.setCharAt(i + 5, '6');
                                    }
                                    if (strbuf.charAt(i + 5) == 'S') {
                                        strbuf.setCharAt(i + 5, '5');
                                    }
                                    if (getNUM(strbuf.charAt(i + 5)))  {
                                        Log.e("C1","5位");
                                        return str = strbuf.substring(i, i + 6);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return null ;
    }


    public static Boolean getSTR(char s)
    {
        if(s>='A'&&s<='Z')
        {return true ;}
        else return false ;
    }
    public static Boolean getNUM(char s)
    {
        if(s>='0'&&s<='9')
        {return true ;}
        else return false ;
    }


}
