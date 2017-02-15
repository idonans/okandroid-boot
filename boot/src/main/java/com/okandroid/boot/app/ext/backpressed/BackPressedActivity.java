package com.okandroid.boot.app.ext.backpressed;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.lang.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by idonans on 2017/2/15.
 */

public class BackPressedActivity extends OKAndroidActivity implements BackPressedActivityHost {

    private static final String TAG = "BackPressedActivity";

    @Override
    public void onBackPressed() {
        ArrayList<BackPressedFragmentHost> hosts = getBackPressedFragmentHosts();
        int size = hosts.size();
        for (int i = size - 1; i >= 0; i--) {
            if (hosts.get(i).onBackPressed()) {
                return;
            }
        }
        callSuperOnBackPressed();
    }

    @Override
    public boolean callSuperOnBackPressed() {
        super.onBackPressed();
        return true;
    }

    private final ArrayList<BackPressedFragmentHost> mBackPressedFragmentHosts = new ArrayList<>();

    public ArrayList<BackPressedFragmentHost> getBackPressedFragmentHosts() {
        synchronized (mBackPressedFragmentHosts) {
            return new ArrayList<>(mBackPressedFragmentHosts);
        }
    }

    @Override
    public void addBackPressedFragmentHost(BackPressedFragmentHost fragmentHost) {
        synchronized (mBackPressedFragmentHosts) {
            if (mBackPressedFragmentHosts.contains(fragmentHost)) {
                new IllegalAccessError("already add " + fragmentHost).printStackTrace();
            } else {
                mBackPressedFragmentHosts.add(fragmentHost);
            }
        }
    }

    @Override
    public void removeBackPressedFragmentHost(BackPressedFragmentHost fragmentHost) {
        synchronized (mBackPressedFragmentHosts) {
            if (!mBackPressedFragmentHosts.contains(fragmentHost)) {
                new IllegalAccessError("not add " + fragmentHost).printStackTrace();
            } else {
                mBackPressedFragmentHosts.remove(fragmentHost);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mBackPressedFragmentHosts.isEmpty()) {
            new IllegalAccessError("mBackPressedFragmentHosts is not empty").printStackTrace();
            Log.e(TAG + " need remove: " + Arrays.deepToString(mBackPressedFragmentHosts.toArray()));
        }
    }

}
