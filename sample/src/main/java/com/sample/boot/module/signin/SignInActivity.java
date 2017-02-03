package com.sample.boot.module.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.okandroid.boot.widget.ContentView;
import com.sample.boot.R;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ContentView(this));

        showSignInView();
    }

    private void showSignInView() {
        final String tag = "sign_in_view";
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignInFragment fragment = (SignInFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = SignInFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.okandroid_content, fragment, tag).commitNowAllowingStateLoss();
        }
    }

}
