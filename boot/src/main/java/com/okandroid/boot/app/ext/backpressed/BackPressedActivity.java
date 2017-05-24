package com.okandroid.boot.app.ext.backpressed;

import com.okandroid.boot.app.OKAndroidActivity;
import com.okandroid.boot.lang.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by idonans on 2017/2/15.
 */

public class BackPressedActivity extends OKAndroidActivity implements BackPressedHost {

    private static final String TAG = "BackPressedActivity";

    @Override
    public void onBackPressed() {
        ArrayList<BackPressedComponent> components = getBackPressedComponents();
        int size = components.size();
        for (int i = size - 1; i >= 0; i--) {
            if (components.get(i).onBackPressed()) {
                return;
            }
        }
        callSuperOnBackPressed();
    }

    public boolean callSuperOnBackPressed() {
        super.onBackPressed();
        return true;
    }

    private final ArrayList<BackPressedComponent> mBackPressedComponents = new ArrayList<>();

    public ArrayList<BackPressedComponent> getBackPressedComponents() {
        synchronized (mBackPressedComponents) {
            return new ArrayList<>(mBackPressedComponents);
        }
    }

    @Override
    public void addBackPressedComponent(BackPressedComponent backPressedComponent) {
        synchronized (mBackPressedComponents) {
            if (mBackPressedComponents.contains(backPressedComponent)) {
                new IllegalAccessError("already add " + backPressedComponent).printStackTrace();
            } else {
                mBackPressedComponents.add(backPressedComponent);
            }
        }
    }

    @Override
    public void removeBackPressedComponent(BackPressedComponent backPressedComponent) {
        synchronized (mBackPressedComponents) {
            if (!mBackPressedComponents.contains(backPressedComponent)) {
                new IllegalAccessError("not add " + backPressedComponent).printStackTrace();
            } else {
                mBackPressedComponents.remove(backPressedComponent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mBackPressedComponents.isEmpty()) {
            new IllegalAccessError("mBackPressedComponents not empty").printStackTrace();
            Log.e(TAG + " need remove: " + Arrays.deepToString(mBackPressedComponents.toArray()));
        }
    }

}
