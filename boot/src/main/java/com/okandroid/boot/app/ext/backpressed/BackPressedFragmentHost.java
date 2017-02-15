package com.okandroid.boot.app.ext.backpressed;

/**
 * Created by idonans on 2017/2/15.
 */

public interface BackPressedFragmentHost {

    boolean onBackPressed();

    boolean callActivityBackPressed();

}
