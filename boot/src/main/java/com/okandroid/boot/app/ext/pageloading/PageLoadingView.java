package com.okandroid.boot.app.ext.pageloading;

import com.okandroid.boot.app.ext.preload.PreloadView;
import com.okandroid.boot.widget.PageDataAdapter;

import java.util.Collection;

/**
 * Created by idonans on 2017/4/20.
 */

public interface PageLoadingView extends PreloadView {

    void showPageLoadingStatus(PageDataAdapter.PageLoadingStatus pageLoadingStatus);

    void showPageContent(boolean firstPage, Collection data);

}
