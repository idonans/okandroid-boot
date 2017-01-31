package com.okandroid.boot;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 设置 Context
 * Created by idonans on 16-4-12.
 */
public class AppContext {

    private static Context sContext;

    @NonNull
    public static Context getContext() {
        return sContext;
    }

    public static void setContext(@NonNull Context context) {
        sContext = context.getApplicationContext();
    }

}
