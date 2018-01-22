package com.usiellau.conferenceol.util;

import android.content.res.Resources;
import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by UsielLau on 2018/1/21 0021 23:30.
 */

public class Utils {
    public static boolean isMobiPhoneNum(String telNum){
        String regex = "^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(telNum);
        return m.matches();
    }


}
