package com.okandroid.boot.widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.okandroid.boot.R;
import com.okandroid.boot.util.ViewUtil;

/**
 * Created by idonans on 2017/5/15.
 */

public class ContentLoadingDialog extends ContentDialog {

    private ProgressBar mProgressBar;

    public ContentLoadingDialog(@NonNull Activity activity) {
        super(activity);
    }

    public ContentLoadingDialog(@NonNull Activity activity, @NonNull ViewGroup contentParent) {
        super(activity, contentParent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setContentView(R.layout.okandroid_content_loading_dialog);

        mProgressBar = ViewUtil.findViewByID(getContentView(), R.id.progress_bar);

        setDimBackground(true);
        setDimAmount(0.5f);

        setAnimationStyle(R.style.OKAndroid_Animation_ContentLoadingTop);
    }

}
