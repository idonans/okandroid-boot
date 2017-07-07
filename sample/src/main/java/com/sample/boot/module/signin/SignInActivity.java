package com.sample.boot.module.signin;

import android.content.Context;
import android.content.Intent;

import com.okandroid.boot.app.ext.dynamic.DynamicFragment;
import com.sample.boot.app.BaseActivity;

/**
 * Created by idonans on 2017/2/3.
 */

public class SignInActivity extends BaseActivity {

    public static Intent startIntent(Context context) {
        Intent starter = new Intent(context, SignInActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return starter;
    }

    @Override
    protected DynamicFragment createDynamicFragment() {
        return SignInFragment.newInstance();
    }

}
