package com.okandroid.boot.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Window;

import com.okandroid.boot.R;
import com.okandroid.boot.util.SystemUtil;

/**
 * Created by idonans on 2017/5/15.
 */

public class ContentDialog extends Dialog {

    public ContentDialog(@NonNull Context context) {
        this(context, R.style.OKAndroid_ContentDialog);
    }

    public ContentDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateSystemUi();

        super.onCreate(savedInstanceState);
    }

    protected void updateSystemUi() {
        Window window = getWindow();
        SystemUtil.setStatusBarTransparent(window);
        SystemUtil.setSystemUi(window.getDecorView(), false, false);
    }

}
