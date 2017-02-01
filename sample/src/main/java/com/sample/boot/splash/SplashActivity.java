package com.sample.boot.splash;

import android.os.Bundle;

import com.sample.boot.R;
import com.sample.boot.app.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_splash_activity);
    }
}
