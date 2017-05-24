package com.sample.boot.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.okandroid.boot.util.DimenUtil;
import com.okandroid.boot.util.ViewUtil;
import com.sample.boot.R;

/**
 * Created by idonans on 2017/5/18.
 */

public class CircleTextView extends View {

    public CircleTextView(Context context) {
        super(context);
        init();
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint mPaint;
    private String mText;
    private int mGravity = Gravity.CENTER;
    private RectF mArea;

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.okandroid_color_md_red_500));
        mPaint.setTextSize(DimenUtil.sp2px(20));

        mArea = new RectF();
    }

    public void setText(String text) {
        mText = text;
        postInvalidate();
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mText)) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        mArea.set(0, 0, width, height);
        ViewUtil.drawText(canvas, mText, mPaint, mArea, mGravity);
    }

}
