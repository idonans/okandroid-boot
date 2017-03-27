package com.okandroid.boot.util;

import android.content.pm.PackageManager;

/**
 * Created by idonans on 2017/3/27.
 */

public class GrantResultUtil {

    private GrantResultUtil() {
    }

    /**
     * 如果授权通过返回 true, 否则返回 false.
     */
    public static boolean isGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 如果授权都通过返回 true, 否则返回 false.
     */
    public static boolean isAllGranted(int[] grantResults) {
        if (grantResults == null) {
            return true;
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

}
