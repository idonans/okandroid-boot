package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.preload.PreloadFragment;
import com.sample.boot.app.BaseActivity;

/**
 * Created by idonans on 2017/4/21.
 */

public class DataListActivity extends BaseActivity {

    @Override
    protected PreloadFragment createPreloadFragment() {
        return new DataListFragment();
    }

}
