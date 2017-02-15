package com.okandroid.boot.app.ext.preload;

import android.os.Bundle;

import com.okandroid.boot.app.OKAndroidActivity;

/**
 * 当前页面不会保存内部 fragment 的状态
 * Created by idonans on 2017/2/15.
 */

public class PreloadActivity extends OKAndroidActivity {

    private static final String TAG_DEFAULT_CONTENT_FRAGMENT = "preload_default_content_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isAvailable()) {
            return;
        }

        initPreload();
    }

    protected void initPreload() {

    }

}
