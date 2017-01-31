package com.okandroid.boot.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.okandroid.boot.AppContext;

/**
 * dp,sp to px
 * Created by idonans on 16-4-13.
 */
public class DimenUtil {

    public static int dp2px(float dp) {
        DisplayMetrics metrics = AppContext.getContext().getResources().getDisplayMetrics();
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                metrics);
        final int res = (int) (v + 0.5f);
        if (res != 0) return res;
        if (dp == 0) return 0;
        if (dp > 0) return 1;
        return -1;
    }

    public static int sp2px(float sp) {
        DisplayMetrics metrics = AppContext.getContext().getResources().getDisplayMetrics();
        float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                metrics);
        final int res = (int) (v + 0.5f);
        if (res != 0) return res;
        if (sp == 0) return 0;
        if (sp > 0) return 1;
        return -1;
    }

}
