package com.okandroid.boot.ext.loadingstatus;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.okandroid.boot.R;

/**
 * Created by idonans on 2017/7/10.
 */

public class LoadingStatusLoadSuccessSmall extends LoadingStatus {

    public LoadingStatusLoadSuccessSmall(Context context, @Nullable LayoutInflater inflater, @Nullable ViewGroup parent) {
        super(context, inflater, parent, R.layout.okandroid_ext_loadingstatus_load_success_small);
    }

}
