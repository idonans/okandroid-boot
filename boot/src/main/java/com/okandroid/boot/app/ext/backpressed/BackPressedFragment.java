package com.okandroid.boot.app.ext.backpressed;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.okandroid.boot.app.OKAndroidFragment;
import com.okandroid.boot.util.SystemUtil;

/**
 * Created by idonans on 2017/2/15.
 */

public class BackPressedFragment extends OKAndroidFragment implements BackPressedFragmentHost {

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean callActivityBackPressed() {
        Activity activity = SystemUtil.getActivityFromFragment(this);
        if (activity != null) {
            activity.onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = SystemUtil.getActivityFromFragment(this);
        if (activity instanceof BackPressedActivityHost) {
            ((BackPressedActivityHost) activity).addBackPressedFragmentHost(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Activity activity = SystemUtil.getActivityFromFragment(this);
        if (activity instanceof BackPressedActivityHost) {
            ((BackPressedActivityHost) activity).removeBackPressedFragmentHost(this);
        }
    }

}
