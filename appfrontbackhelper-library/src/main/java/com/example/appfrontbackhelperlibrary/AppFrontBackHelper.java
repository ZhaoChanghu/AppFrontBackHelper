package com.example.appfrontbackhelperlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者:zch
 * 时间:2022/4/11 8:50
 * 描述:监听APP进入前台，退出后台,双击退出APP，获取当前已注册的Activity
 * 使用：
 * 1.application中注册
 * 2.启动Activity中声明弹窗
 */

public class AppFrontBackHelper {

    private OnAppStatusListener mOnAppStatusListener;

    private static boolean isShow = false;

    public String startToast = null;

    public String stopToast = null;

    public int toastTime = 0;

    private static ArrayList<Activity> mActivityList =  null;

    //自定义方法，重写后复用
    public AppFrontBackHelper() {

    }
    //设置APP进入启动页时的内容,设置之后默认打开弹窗
    public AppFrontBackHelper setStartToast(String startToastText){
        this.startToast = startToastText;
        return this;
    }
    //设置APP进入后台时的内容，需要在启动页打开之后才能弹窗
    public AppFrontBackHelper setStopToast(String stopToastText){
        this.stopToast = stopToastText;
        return this;
    }
    //设置弹窗时间，默认 short
    public AppFrontBackHelper setToastTime(int toastTime){
        this.toastTime = toastTime;
        return this;
    }
    //注册ActivityLifecycleCallbacks
    public AppFrontBackHelper register(){
        isShow = true;
        if (getCurApplication() != null) {
            getCurApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
        return this;
    }
    //自定义注册ActivityLifecycleCallbacks
    public AppFrontBackHelper register(OnAppStatusListener listener){
        isShow = true;
        mOnAppStatusListener = listener;
        if (getCurApplication() != null) {
            getCurApplication().registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
        return this;
    }
    //开启APP进入后台弹窗
    public static void startToast(){
        isShow = true;
    }
    //关闭APP进入后台弹窗
    public static void stopToast(){
        isShow = false;
    }
    //取消注册，取消之后APP彻底杀死之后(未在后台存活)才能正常重新使用
    public AppFrontBackHelper unRegister(){
        if (getCurApplication() != null) {
            getCurApplication().unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        }
        isShow = false;
        return this;
    }
    //ActivityLifecycleCallbacks方法监听
    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        //打开的Activity数量统计
        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mActivityList == null){
                mActivityList = new ArrayList<>();
            }
            if (activity != null){
                mActivityList.add(activity);
            }

            activityStartCount++;
            //数值从0变到1说明是从后台切到前台
            if (activityStartCount == 1){
                //从后台切到前台
                if(mOnAppStatusListener != null){
                    mOnAppStatusListener.onFront();
                }
                if (startToast != null){
                    showToast(startToast,toastTime);
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityStartCount--;
            //数值从1到0说明是从前台切到后台
            if (activityStartCount == 0 && isShow){
                //从前台切到后台
                if(mOnAppStatusListener != null){
                    mOnAppStatusListener.onBack();
                }
                if (stopToast != null){
                    showToast(stopToast,toastTime);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
    //自定义回调接口
    public interface OnAppStatusListener{
        void onFront();
        void onBack();
    }

    /**
     *注释：
     * 获取当前所有Activity,返回一个集合
     */
    public static ArrayList<Activity> getALLActivity(){
        return mActivityList;
    }

    /**
     *注释：
     * 双击退出APP
     */
    private static long finishTime = 0;
    public static void setAPPFinish(int time,String s){
        if (System.currentTimeMillis() - finishTime > time) {
            showFinishToast(s,time);
            finishTime = System.currentTimeMillis();
        } else {
            isShow = false;
            if (mActivityList.size() > 0){
                for (Activity a : mActivityList){
                    a.finish();
                }
            }

        }
    }

    /**
     *注释：
     * 自定义弹窗实时间
     */
    private void showToast(String s, final int cnt) {
        Toast toast = Toast.makeText(getCurApplication(), s, Toast.LENGTH_SHORT);
        if (cnt == 0){
            toast.show();
        }else {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.show();
                }
            }, 0, 3000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                    timer.cancel();
                }
            }, cnt );
        }
    }
    private static void showFinishToast(String s, final int cnt) {
        Toast toast = Toast.makeText(getCurApplication(), s, Toast.LENGTH_SHORT);
        if (cnt == 0){
            toast.show();
        }else {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.show();
                }
            }, 0, 3000);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                    timer.cancel();
                }
            }, cnt );
        }
    }
    /**
     * 获取当前应用的Application
     * 先使用ActivityThread里获取Application的方法，如果没有获取到，
     * 再使用AppGlobals里面的获取Application的方法
     * @return
     */
    private  static Application getCurApplication(){
        Application application = null;
        try{
            @SuppressLint("PrivateApi") Class atClass = Class.forName("android.app.ActivityThread");
            @SuppressLint("DiscouragedPrivateApi") Method currentApplicationMethod = atClass.getDeclaredMethod("currentApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
            Log.d("fw_create","curApp class1:"+application);
        }catch (Exception e){
            Log.d("fw_create","e:"+e.toString());
        }

        if(application != null)
            return application;

        try{
            @SuppressLint("PrivateApi") Class atClass = Class.forName("android.app.AppGlobals");
            @SuppressLint("DiscouragedPrivateApi") Method currentApplicationMethod = atClass.getDeclaredMethod("getInitialApplication");
            currentApplicationMethod.setAccessible(true);
            application = (Application) currentApplicationMethod.invoke(null);
            Log.d("fw_create","curApp class2:"+application);
        }catch (Exception e){
            Log.d("fw_create","e:"+e.toString());
        }

        return application;
    }

}
