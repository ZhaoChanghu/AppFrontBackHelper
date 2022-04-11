package com.example.appfrontbackhelper;

import android.widget.Toast;

import com.example.appfrontbackhelperlibrary.AppFrontBackHelper;

/**
 * 作者:zch
 * 时间:2022/4/11 13:27
 * 描述:Application
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //注册AppFrontBackHelper
        //方式1
        new AppFrontBackHelper()
                .setToastTime(500)
                .setStopToast("APP已进入后台运行")
                .register();
        //方式2
//        AppFrontBackHelper helper = new AppFrontBackHelper();
//        helper.register(new AppFrontBackHelper.OnAppStatusListener() {
//            @Override
//            public void onFront() {
//                //应用切到前台处理
//                Toast.makeText(getApplicationContext(),"我进入到前台了",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onBack() {
//                //应用切到后台处理
//                Toast.makeText(getApplicationContext(),"我进入到后台了",Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
