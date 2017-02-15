package com.okandroid.boot.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.okandroid.boot.R;

/**
 * Created by idonans on 2016/7/21.
 */
public class ContentView extends FrameLayout {

    public ContentView(Context context) {
        super(context);
        init();
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (getId() != NO_ID && getId() != R.id.okandroid_content) {
            throw new IllegalArgumentException("id should set with R.id.okandroid_content");
        }
        setId(R.id.okandroid_content);
        setFitsSystemWindows(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean fitSystemWindows(Rect insets) {
        insets.top = 0;
        return super.fitSystemWindows(insets);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        insets = insets.replaceSystemWindowInsets(
                insets.getSystemWindowInsetLeft(),
                0,
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom());
        return super.dispatchApplyWindowInsets(insets);
    }

}
