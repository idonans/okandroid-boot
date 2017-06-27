package com.okandroid.boot.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;

/**
 * Created by idonans on 2017/6/20.
 */

public class ContentFullView extends ContentView {

    public ContentFullView(Context context) {
        super(context);
    }

    public ContentFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContentFullView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.left = 0;
        insets.top = 0;
        insets.right = 0;
        insets.bottom = 0;
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        return insets.consumeSystemWindowInsets();
    }

}
