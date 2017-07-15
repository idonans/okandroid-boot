package com.sample.boot.module.ext;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.okandroid.boot.widget.ContentDialog;
import com.sample.boot.R;

/**
 * Created by idonans on 2017/7/15.
 */

public class SampleInputDialog extends ContentDialog {

    public SampleInputDialog(@NonNull Activity activity) {
        super(activity);
    }

    public SampleInputDialog(@NonNull Activity activity, @NonNull ViewGroup contentParent) {
        super(activity, contentParent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setContentView(R.layout.sample_sample_input_dialog);
        setDimAmount(0.5f);
        setDimBackground(true);
    }

}
