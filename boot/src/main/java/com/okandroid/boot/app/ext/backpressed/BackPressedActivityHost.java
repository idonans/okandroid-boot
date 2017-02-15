package com.okandroid.boot.app.ext.backpressed;

/**
 * Created by idonans on 2017/2/15.
 */

public interface BackPressedActivityHost {

    boolean callSuperOnBackPressed();

    void addBackPressedFragmentHost(BackPressedFragmentHost fragmentHost);

    void removeBackPressedFragmentHost(BackPressedFragmentHost fragmentHost);

}
