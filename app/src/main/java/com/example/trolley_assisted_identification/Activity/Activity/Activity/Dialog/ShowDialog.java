package com.example.trolley_assisted_identification.Activity.Activity.Activity.Dialog;
import android.content.Context;

import com.example.trolley_assisted_identification.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class ShowDialog {

    private CustomDialog customDialog;

    public void ShowDialog() {

    }

    public void show(final Context context, String title) {
        customDialog = new CustomDialog(context);
        customDialog.setTitle(title);
        customDialog.setMessage(readUpData(context));
        customDialog.setYesOnClickListener("确定", new CustomDialog.onYesOnClickListener() {
            @Override
            public void onYesClick() {
                customDialog.dismiss();
            }
        });

        customDialog.show();

    }

    /**
     * 读取更新内容
     * @return
     */
    private BufferedReader readUpData(Context context){
        BufferedReader br = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.updatafile);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(isr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return br;
    }

}