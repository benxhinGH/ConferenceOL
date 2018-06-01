package com.usiellau.conferenceol.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.ConfSvMethods;
import com.usiellau.conferenceol.network.HttpResult;
import com.usiellau.conferenceol.network.entity.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by UsielLau on 2018/1/21 0021 23:30.
 */

public class Utils {

    // 保存最后一次登录账号信息
    public static void saveLastLogined(Context context,User user) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt("uid",user.getId())
                .putString("username", user.getPhonenumber())
                .putString("password", user.getPassword())
                .putString("nickname",user.getNickname())
                .putString("imagePath",user.getHeadImageUrl())
                .apply();
    }

    public static void updateLocalUserInfo(final Context context){
        String username=PreferenceManager.getDefaultSharedPreferences(context).getString("username","");
        ConfSvMethods.getInstance().queryUserInfo(new Observer<HttpResult<User>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HttpResult<User> userHttpResult) {
                saveLastLogined(context,userHttpResult.getResult());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },username);
    }

    public static String getDefaultFileSavePath(Context context){
        return context.getExternalFilesDir(null).getPath();
    }

    public static boolean copySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isMobiPhoneNum(String telNum){
        String regex = "^((13[0-9])|(15[0-9])|(18[0-9])|(16[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(telNum);
        return m.matches();
    }

    public static boolean fileExistExternalDir(Context context,String fileName){
        File[] files=context.getExternalFilesDir(null).listFiles();
        for(File file:files){
            if(file.getName().equals(fileName)){
                return true;
            }
        }
        return false;
    }


    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSystemUI(Activity activity, boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (show) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }
    }

    public static void setActivityFullScreen(Activity activity, boolean fullScreen) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        if (fullScreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(attrs);
        } else {
            attrs.flags &= ~(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(attrs);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    public static int[] getAndroiodScreenProperty(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;
        int[] res=new int[2];
        res[0]=width;
        res[1]=height;
        return res;
    }

    public static String longTime2String(long time){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(time);
        return sf.format(d);
    }

    public static String longPeriod2String(long time){
        time=time/1000;
        int second=(int)(time%60);
        int minutes=(int)(time-second)/60;
        int minute=minutes%60;
        int hour=minutes/60;
        String secondStr=turnTimeItemToString(second);
        String minuteStr=turnTimeItemToString(minute);
        String hourStr=turnTimeItemToString(hour);
        return hourStr+":"+minuteStr+":"+secondStr;
    }

    private static String turnTimeItemToString(int i){
        String res="";
        if(i>=10){
            res+=i;
        }else{
            res="0"+i;
        }
        return res;
    }

    public static String getUUID(){
        String uuid= UUID.randomUUID().toString();
        String r=uuid.replaceAll("-","");
        return r;
    }


}
