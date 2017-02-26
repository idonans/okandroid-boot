package com.okandroid.boot.widget;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by idonans on 2017/2/26.
 */

public class ContentLoadingWindow {

    private final ViewGroup mParent;
    private ContentLoadingView mContentView;
    private PopupWindow mPopupWindow;

    public ContentLoadingWindow(ViewGroup parent) {
        mParent = parent;
    }

    public void show() {
        dismiss();

        mContentView = new ContentLoadingView(mParent.getContext());
        mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(true);
        mPopupWindow.showAtLocation(mParent, Gravity.LEFT | Gravity.TOP, 0, 0);
        mContentView.showLoading();
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mContentView.hideLoading();
            mPopupWindow.dismiss();
            mContentView = null;
            mPopupWindow = null;
        }
    }

}
