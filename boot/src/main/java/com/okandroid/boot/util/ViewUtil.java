package com.okandroid.boot.util;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
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

    /**
     * 将文本绘制在指定区域内(仅支持单行的形式), 可指定对齐方式
     */
    public static void drawText(Canvas canvas, String text, Paint paint, RectF area, int gravity) {
        float textWith = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        Rect areaIn = new Rect();
        area.round(areaIn);
        Rect areaOut = new Rect();

        GravityCompat.apply(gravity, (int) Math.ceil(textWith), (int) Math.ceil(textHeight), areaIn, areaOut, ViewCompat.LAYOUT_DIRECTION_LTR);

        canvas.drawText(text, areaOut.left, areaOut.top - fontMetrics.ascent, paint);
    }

}
