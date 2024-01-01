package com.example.trolley_assisted_identification.Activity.Activity.Activity.Util;

import android.util.Log;

import com.example.trolley_assisted_identification.Activity.Activity.Activity.ConnectTransport;
import com.example.trolley_assisted_identification.Activity.Activity.Activity.StmToAndroid;

public class Android_Contor_Car_Util {

    public int flag ;  //  接收主车反馈信息标志位

    public static ConnectTransport connectTransport; //   通讯类

    private StmToAndroid STA ;




    /*
              重置接收主车标志位
       */
    public void setFlag_Andoird_Contor_Car(int flag){
        flag = flag;
    }
    /*
               给Android返回主车标志位
        */
    public int getFlag_Andoird_Contor_Car(){
        return flag;
    }




    /*
                     安卓控制主车启动,执行是靠安卓发指令执行，安卓发什么，主车做什么
     */
    public void Android_Contor_Car(int num){
        STA = new StmToAndroid();
        connectTransport = new ConnectTransport();
        connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 1)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 2)connectTransport.Android_Contor_Car_Go(0x03);     //  主车右转
        Log.d("Android控制主车执行任务"," 主车右转");
        if(num == 3)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 4)connectTransport.Android_Contor_Car_Go(0x03);     //  主车右转
        Log.d("Android控制主车执行任务"," 主车右转");
        if(num == 5)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 6)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 7)connectTransport.Android_Contor_Car_Go(0x02);     //  主车左转
        Log.d("Android控制主车执行任务"," 主车左转");
        if(num == 8)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 9)connectTransport.Android_Contor_Car_Go(0x02);     //  主车左转
        Log.d("Android控制主车执行任务"," 主车左转");
        if(num == 10)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        if(num == 11)connectTransport.Android_Contor_Car_Go(0x01);     //  主车向前
        Log.d("Android控制主车执行任务"," 主车向前");
        num = 0;
    }
}
