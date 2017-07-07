package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.dynamic.DynamicFragment;
import com.sample.boot.app.BaseActivity;

/**
 * Created by idonans on 2017/4/21.
 */

public class DataListActivity extends BaseActivity {

    @Override
    protected DynamicFragment createDynamicFragment() {
        return new DataListFragment();
    }

}
