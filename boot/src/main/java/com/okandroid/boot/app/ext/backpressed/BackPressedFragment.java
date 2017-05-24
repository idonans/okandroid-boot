package com.okandroid.boot.app.ext.backpressed;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.okandroid.boot.app.OKAndroidFragment;

/**
 * Created by idonans on 2017/2/15.
 */

public class BackPressedFragment extends OKAndroidFragment implements BackPressedComponent {

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean requestBackPressed() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity instanceof BackPressedHost) {
            ((BackPressedHost) activity).addBackPressedComponent(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Activity activity = getActivity();
        if (activity instanceof BackPressedHost) {
            ((BackPressedHost) activity).removeBackPressedComponent(this);
        }
    }

}
