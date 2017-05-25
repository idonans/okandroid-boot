package com.sample.boot.module.splash;

import android.view.Window;

import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.okandroid.boot.util.SystemUtil;
import com.sample.boot.app.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void updateSystemUi() {
        Window window = getWindow();
        SystemUtil.setStatusBarTransparent(window);
        SystemUtil.setNavigationBarTransparent(window);
        SystemUtil.setSystemUi(window.getDecorView(), true, true, true, true, false);
    }

    @Override
    protected PreloadFragment createPreloadFragment() {
        return SplashFragment.newInstance();
    }

}