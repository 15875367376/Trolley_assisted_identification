package com.example.trolley_assisted_identification.Activity.Activity.Activity.color;


import android.graphics.Bitmap;
import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.MainActivity;


public class Car_color_test {
    int Rnum = 0;
    int Gnum = 0;

    int Ynum = 0;
    private int blackMax = 255; //黑色最大RGB值和
    private int RGBMax = 800;   //红绿蓝最大RGB值和
    private int  Who = 0;

    public Car_color_test()
    {

 
    }









    public void ColorRun()
    {  Bitmap bitmap2 = null;
//
//    MainActivity.camerastate_control(6);
//
//        try {
//            Thread.sleep(3000);
//
//                Thread.sleep(500);

                bitmap2 = new color_cv().color_A(MainActivity.bitmap);

                if (bitmap2 != null) {
                    Who = convertToBlackTest(bitmap2);
                    Log.i("color", ""+Who );

                    //  MainActivity.img2.setImageBitmap(bitmap2);
                    if (Who!=0){
                 //   MainActivity.text1.setText("颜色" + Who);

                    MainActivity.Connect_Transport.colorRGB(Who);
                    }else
                    {
                        MainActivity.Connect_Transport.colorRGB(1);

                    }
                }


       //         MainActivity.camerastate_control(4);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }


    public void ColorRun(Bitmap bitmap)
    {
        Who = convertToBlackTest(bitmap);

        MainActivity.text1.setText("Color"+Who); ;

        MainActivity.Connect_Transport.colorRGB(Who);

    }


//   /* public int convertToBlack(Bitmap bip) {
//
//        Ynum = 0 ;
//        Rnum = 0 ;
//        Gnum = 0 ;
//        int width = bip.getWidth();
//        int height = bip.getHeight();
//        int[] pixels = new int[width * height];
//        bip.getPixels(pixels, 0, width, 0, 0, width, height);       // 把二维图片的每一行像素颜色值读取到一个一维数组中
//        int[] pl = new int[bip.getWidth() * bip.getHeight()];
//    //   MainActivity.toastUtil.ShowToast("子函数");
//        for (int y = 0; y < height; y++) {
//            int offset = y * width;
//            for (int x = 0; x < width; x++) {
//                int pixel = pixels[offset + x];
//                int r = (pixel >> 16) & 0xff;
//                int g = (pixel >> 8) & 0xff;
//                int b = pixel & 0xff;
//                int rgb = r + g + b;
//                    if (rgb < RGBMax) {        // 红绿蓝
//                    if (r>230&&g>230){
//                    Ynum++;
//                    }           //黄色
//                else if (r>200&&g<140&&b<170&&b>70){
//                    Rnum++;
//                    }         //红色//
//                else  if (r<100&&g>230&&GreenCompare(b,r)&&r>30){
//                    Gnum++;
//                    }          //绿色
//                }
//            }
//        }
//        if(Rnum>Ynum&&Rnum>Gnum){return 1;}
//        if (Gnum>Ynum&&Gnum>Rnum){return 2;}
//        if (Ynum>Gnum&&Ynum>Rnum){return 3;}
//        return 0;
//    }*/






    public int convertToBlackTest(Bitmap bip) {
     //  MainActivity.img2.setImageBitmap(bip);
        Ynum = 0 ;
        Rnum = 0 ;
        Gnum = 0 ;
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);       // 把二维图片的每一行像素颜色值读取到一个一维数组中
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
       // MainActivity.toastUtil.ShowToast("子函数");
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int rgb = r + g + b;
                if (rgb < RGBMax) {        // 红绿蓝
                    if (r>240&&g>200){
                        Ynum++;
                    }           //黄色
                    else if  (r>200&&g<140&&b<170&&b>70){
                        Rnum++;
                    }         //红色//
                    else  if (r<180&&g>150&&GreenCompare(g,b)){
                        Gnum++;
                    }          //绿色
                }
            }
        }
        MainActivity.text1.setText("R:  "+Rnum+"    G: "+ Gnum+"     Y:"+Ynum);
        Log.e("RGBColor","R:  "+Rnum+"    G: "+ Gnum+"     Y:"+Ynum);
        if(Rnum>Ynum&&Rnum>Gnum||Rnum>=200&&Rnum>Gnum){return 1;}
        if (Gnum>Ynum&&Gnum>Rnum||Gnum>50&&Gnum>Rnum){return 2;}
        if (Ynum>Gnum&&Ynum>Rnum){return 3;}
        return 0;
    }


//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void Car_colortest1(WIFIflagtest wifIflagtest)
//    {
//        if(wifIflagtest.getFlag()==1)
//        {
//            MainActivity.toastUtil.ShowToast("收到信息");
//            color_test();
//        }
//    }



    public boolean RedCompare(int g , int b)
    {
        int num = g-b;
        if(num<140)return true;
        return false;
    }



    public boolean GreenCompare(int b , int r)
    {
        int num = b-r;
        if(num<100&&num>40)return true;
        return false;
    }
}


