package com.sample.boot.module.splash;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.okandroid.boot.widget.ContentView;
import com.sample.boot.R;
import com.sample.boot.app.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ContentView(this));

        showSplashView();
    }

    private void showSplashView() {
        final String tag = "splash_view";
        FragmentManager fragmentManager = getSupportFragmentManager();
        SplashFragment fragment = (SplashFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = SplashFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.okandroid_content, fragment, tag).commitNowAllowingStateLoss();
        }
    }

}