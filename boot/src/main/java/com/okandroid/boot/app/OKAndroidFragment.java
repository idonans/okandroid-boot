package com.okandroid.boot.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Log;

/**
 * 基类 Fragment
 * Created by idonans on 16-4-13.
 */
public class OKAndroidFragment extends Fragment implements Available {

    private boolean mAvailable;
    private boolean mResumed;

    private final String CLASS_NAME = getClass().getName() + "@" + hashCode();

    @Override
    public void onStart() {
        Log.v(CLASS_NAME, "onStart");

        super.onStart();
    }

    @Override
    public void onPause() {
        Log.v(CLASS_NAME, "onPause");

        super.onPause();
        mResumed = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(CLASS_NAME, "onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.v(CLASS_NAME, "onViewStateRestored");

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.v(CLASS_NAME, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        Log.v(CLASS_NAME, "onAttach");

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.v(CLASS_NAME, "onDetach");

        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.v(CLASS_NAME, "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(CLASS_NAME, "onCreate");

        super.onCreate(savedInstanceState);
        mAvailable = true;
    }

    @Override
    public void onDestroy() {
        Log.v(CLASS_NAME, "onDestroy");

        super.onDestroy();
        mAvailable = false;
    }

    @Override
    public boolean isAvailable() {
        return mAvailable;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.v(CLASS_NAME, "onHiddenChanged", hidden);

        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(CLASS_NAME, "setUserVisibleHint", isVisibleToUser);

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStop() {
        Log.v(CLASS_NAME, "onStop");

        super.onStop();
    }

    @Override
    public void onResume() {
        Log.v(CLASS_NAME, "onResume");

        super.onResume();
        mResumed = true;
    }

    public boolean isAppCompatResumed() {
        return mResumed;
    }

}
