package com.okandroid.boot.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.SystemUtil;

/**
 * 基类 Activity
 * Created by idonans on 16-4-13.
 */
public class OKAndroidActivity extends AppCompatActivity implements Available {

    private boolean mAvailable;
    private boolean mResumed;
    private boolean mTransparentStatusBar = true;

    private final String CLASS_NAME = getClass().getName() + "@" + hashCode();

    /**
     * the best way u should always check available status,
     * if false, means this activity will be finished soon.
     * <pre>
     *     protected void onCreate() {
     *         super.onCreate();
     *
     *         if (!isAvailable()) {
     *             return;
     *         }
     *
     *         setContentView();
     *     }
     * </pre>
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(CLASS_NAME, "onCreate");

        if (mTransparentStatusBar) {
            SystemUtil.setStatusBarTransparent(getWindow());
        }
        super.onCreate(savedInstanceState);

        {
            // 修复根 Activity 重复启动的 bug
            if (!isTaskRoot()) {
                Intent intent = getIntent();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                    Log.e("close this launcher instance " + getClass().getName() + "@" + hashCode());
                    finish();
                    return;
                }
            }
        }

        mAvailable = true;
    }

    /**
     * default is true, u can change to false but must call before onCreate()
     * <pre>
     *     protected void onCreate() {
     *         setTransparentStatusBar(false);
     *         super.onCreate();
     *
     *         if (!isAvailable()) {
     *             return;
     *         }
     *
     *         setContentView();
     *     }
     * </pre>
     *
     * @param transparentStatusBar
     */
    protected void setTransparentStatusBar(boolean transparentStatusBar) {
        mTransparentStatusBar = transparentStatusBar;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(CLASS_NAME, "onNewIntent");

        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        Log.v(CLASS_NAME, "onRestart");

        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.v(CLASS_NAME, "onStart");

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(CLASS_NAME, "onStop");

        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.v(CLASS_NAME, "onPause");

        super.onPause();
        mResumed = false;
    }

    @Override
    protected void onResume() {
        Log.v(CLASS_NAME, "onResume");

        super.onResume();
        mResumed = true;
    }

    public boolean isAppCompatResumed() {
        return mResumed;
    }

    @Override
    public void onBackPressed() {
        if (isAppCompatResumed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        Log.v(CLASS_NAME, "onTrimMemory", level);

        super.onTrimMemory(level);
    }

    @Override
    protected void onDestroy() {
        Log.v(CLASS_NAME, "onDestroy");

        super.onDestroy();
        mAvailable = false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(CLASS_NAME, "onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(CLASS_NAME, "onRestoreInstanceState");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean isAvailable() {
        return mAvailable && !isFinishing();
    }

}