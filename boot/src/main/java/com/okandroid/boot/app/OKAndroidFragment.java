package com.okandroid.boot.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.okandroid.boot.App;
import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Log;

/**
 * 基类 Fragment
 * Created by idonans on 16-4-13.
 */
public class OKAndroidFragment extends Fragment implements Available {

    private boolean mAvailable;
    private boolean mResumed;

    private final String DEBUG_TAG = getClass().getName();

    private boolean isDebug() {
        return App.getBuildConfigAdapter().isDebug();
    }

    @Override
    public void onStart() {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onStart");
        }

        super.onStart();
    }

    @Override
    public void onPause() {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onPause");
        }

        super.onPause();
        mResumed = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onCreate");
        }

        super.onCreate(savedInstanceState);
        mAvailable = true;
    }

    @Override
    public void onDestroy() {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onDestroy");
        }

        super.onDestroy();
        mAvailable = false;
    }

    @Override
    public boolean isAvailable() {
        return mAvailable;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStop() {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onStop");
        }

        super.onStop();
    }

    @Override
    public void onResume() {
        if (isDebug()) {
            Log.d(DEBUG_TAG + " onResume");
        }

        super.onResume();
        mResumed = true;
    }

    public boolean isAppCompatResumed() {
        return mResumed;
    }

}
