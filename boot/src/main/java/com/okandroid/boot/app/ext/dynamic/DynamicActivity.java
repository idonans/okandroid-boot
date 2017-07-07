package com.okandroid.boot.app.ext.dynamic;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.okandroid.boot.R;
import com.okandroid.boot.app.ext.backpressed.BackPressedActivity;
import com.okandroid.boot.widget.ContentView;

/**
 * Created by idonans on 2017/2/15.
 */
public abstract class DynamicActivity extends BackPressedActivity {

    protected static final String TAG_DEFAULT_CONTENT_FRAGMENT = "okandroid_default_content_fragment";
    protected static final int DEFAULT_CONTENT_VIEW_ID = R.id.okandroid_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isAvailable()) {
            return;
        }

        initContent();
    }

    protected void initContent() {
        setContentView(createDefaultContentView());

        FragmentManager fragmentManager = getSupportFragmentManager();
        DynamicFragment fragment = (DynamicFragment) fragmentManager.findFragmentByTag(TAG_DEFAULT_CONTENT_FRAGMENT);
        if (fragment == null) {
            fragment = createDynamicFragment();
            if (fragment != null) {
                fragmentManager.beginTransaction().add(DEFAULT_CONTENT_VIEW_ID, fragment, TAG_DEFAULT_CONTENT_FRAGMENT).commitNowAllowingStateLoss();
            }
        }
    }

    protected View createDefaultContentView() {
        return new ContentView(this);
    }

    protected abstract DynamicFragment createDynamicFragment();

}
