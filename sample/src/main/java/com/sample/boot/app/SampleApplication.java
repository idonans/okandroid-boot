package com.sample.boot.app;

import android.app.Application;

/**
 * Created by idonans on 2017/2/1.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this);
    }

}
