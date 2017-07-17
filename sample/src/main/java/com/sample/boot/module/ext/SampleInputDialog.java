package com.sample.boot.module.ext;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.okandroid.boot.app.ext.dialog.OKAndroidDialog;
import com.sample.boot.R;

/**
 * Created by idonans on 2017/7/15.
 */

public class SampleInputDialog extends OKAndroidDialog {

    @Override
    protected View onCreateContentView(@NonNull Activity activity, @NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.sample_sample_input_dialog, container, false);

        setBackgroundColor(0x1f000000);
        setEnterAnimation(R.anim.okandroid_fade_in_from_top);
        setOuterAnimation(R.anim.okandroid_fade_out_from_top);
        return view;
    }

}
