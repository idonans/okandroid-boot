package com.okandroid.boot.app.ext.preload;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.backpressed.BackPressedActivity;
import com.okandroid.boot.widget.ContentView;

/**
 * Created by idonans on 2017/2/15.
 */
public abstract class PreloadActivity extends BackPressedActivity {

    protected static final String TAG_DEFAULT_CONTENT_FRAGMENT = "okandroid_default_content_fragment";
    protected static final int DEFAULT_CONTENT_VIEW_ID = R.id.okandroid_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        removeDirtyFragment();

        if (!isAvailable()) {
            return;
        }

        initContent();
    }

    protected void removeDirtyFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PreloadFragment fragment = (PreloadFragment) fragmentManager.findFragmentByTag(TAG_DEFAULT_CONTENT_FRAGMENT);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss();
        }
    }

    protected void initContent() {
        setContentView(new ContentView(this));

        FragmentManager fragmentManager = getSupportFragmentManager();
        PreloadFragment fragment = (PreloadFragment) fragmentManager.findFragmentByTag(TAG_DEFAULT_CONTENT_FRAGMENT);
        if (fragment == null) {
            fragment = createPreloadFragment();
            if (fragment != null) {
                fragmentManager.beginTransaction().add(DEFAULT_CONTENT_VIEW_ID, fragment, TAG_DEFAULT_CONTENT_FRAGMENT).commitNowAllowingStateLoss();
            }
        }
    }

    protected abstract PreloadFragment createPreloadFragment();

}
