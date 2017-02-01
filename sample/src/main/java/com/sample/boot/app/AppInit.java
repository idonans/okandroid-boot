package com.sample.boot.app;

import android.content.Context;
import android.util.Log;

import com.okandroid.boot.App;
import com.sample.boot.BuildConfig;

/**
 * Created by idonans on 2017/2/1.
 */

public class AppInit {

    private static boolean sInit;

    private AppInit() {
    }

    public static synchronized void init(Context context) {
        if (sInit) {
            return;
        }
        context = context.getApplicationContext();
        new App.Config.Builder()
                .setContext(context)
                .setBuildConfigAdapter(new BuildConfigAdapterImpl())
                .build()
                .init();
        sInit = true;
    }

    public static class BuildConfigAdapterImpl implements App.BuildConfigAdapter {

        @Override
        public int getVersionCode() {
            return BuildConfig.VERSION_CODE;
        }

        @Override
        public String getVersionName() {
            return BuildConfig.VERSION_NAME;
        }

        @Override
        public String getLogTag() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getPublicSubDirName() {
            return BuildConfig.APPLICATION_ID;
        }

        @Override
        public String getChannel() {
            return "default";
        }

        @Override
        public int getLogLevel() {
            return Log.DEBUG;
        }

        @Override
        public boolean isDebug() {
            return BuildConfig.DEBUG;
        }
    }

}
