package com.okandroid.boot.app.ext.page;

import com.okandroid.boot.app.ext.dynamic.DynamicView;
import com.okandroid.boot.widget.PageDataAdapter;

import java.util.Collection;

/**
 * Created by idonans on 2017/4/20.
 */

public interface PageView extends DynamicView {

    void showPageLoadingStatus(PageDataAdapter.PageLoadingStatus pageLoadingStatus);

    void showPageContent(boolean firstPage, Collection data);

}
