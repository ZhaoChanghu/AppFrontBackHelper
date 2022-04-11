package com.example.appfrontbackhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.appfrontbackhelperlibrary.AppFrontBackHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //开启APP后台弹窗
        AppFrontBackHelper.startToast();
    }


    @Override
    public void onBackPressed() {
        //双击退出APP
        AppFrontBackHelper.setAPPFinish(2000,"再次点击退出APP");
    }
}