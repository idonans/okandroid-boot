package com.okandroid.boot.util;

import android.app.Activity;
import android.view.View;

/**
 * View 相关辅助类
 * Created by idonans on 16-4-18.
 */
public class ViewUtil {

    public static <T> T findViewByID(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static <T> T findViewByID(View view, int id) {
        return (T) view.findViewById(id);
    }

}
