package com.okandroid.boot.lang;

import android.support.annotation.Nullable;

/**
 * log 处理
 * Created by idonans on 16-4-13.
 */
public class Log {

    private static String sLogTag = "okandroid";
    private static int sLogLevel = android.util.Log.DEBUG;

    private Log(){
    }

    public static void setLogLevel(int logLevel) {
        sLogLevel = logLevel;
    }

    public static void setLogTag(String logTag) {
        sLogTag = logTag;
    }

    public static void d(@Nullable Object message) {
        if (sLogLevel <= android.util.Log.DEBUG) {
            android.util.Log.d(sLogTag, String.valueOf(message));
        }
    }

    public static void e(@Nullable Object message) {
        if (sLogLevel <= android.util.Log.ERROR) {
            android.util.Log.e(sLogTag, String.valueOf(message));
        }
    }

}
