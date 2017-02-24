package com.sample.boot;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by idonans on 2017/2/1.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        AppInit.init(this);
    }

}
