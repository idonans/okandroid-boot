package com.sample.boot.module.splash;

import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.sample.boot.app.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected PreloadFragment createPreloadFragment() {
        return SplashFragment.newInstance();
    }

}