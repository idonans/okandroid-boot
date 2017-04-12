package com.okandroid.boot.widget.ptr;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by idonans on 2017/4/12.
 */

public class PtrHeader extends FrameLayout implements PtrLayout.HeaderView {

    public PtrHeader(@NonNull Context context) {
        super(context);
        init();
    }

    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PtrHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Context context = getContext();
        TextView textView = new TextView(context);
        textView.setText("ptr header");
        textView.setTextColor(Color.RED);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        addView(textView);
    }

    @Override
    public void setOffsetYDiff(int yDiff, View target) {
        int height = getHeight();
        if (height <= 0) {
            return;
        }

        int translationY = (int) getTranslationY();
        translationY += yDiff;
        if (translationY < 0) {
            translationY = 0;
        }
        if (translationY > height) {
            translationY = height;
        }
        setTranslationY(translationY);

        target.setTranslationY(translationY);
    }

    @Override
    public void finishOffsetY(boolean cancel, View target) {
        // TODO
    }

}
