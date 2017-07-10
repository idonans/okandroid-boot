package com.okandroid.boot.ext.loadingstatus;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okandroid.boot.R;
import com.okandroid.boot.util.ViewUtil;

/**
 * 显示视图的加载状态, 如: 加载中, 加载成功, 网络错误, 服务器忙 等.
 * Created by idonans on 2017/7/10.
 */

public class LoadingStatus {

    public final int layoutResId;
    public final Context context;
    public final LayoutInflater inflater;
    public final View view;

    @Nullable
    public TextView itemMessage;
    @Nullable
    public View itemRetry;

    public LoadingStatus(Context context, @Nullable LayoutInflater inflater, @Nullable ViewGroup parent, int layoutResId) {
        this.layoutResId = layoutResId;
        this.context = context;

        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        this.inflater = inflater;

        this.view = this.inflater.inflate(this.layoutResId, parent, false);

        this.itemMessage = ViewUtil.findViewByID(this.view, R.id.item_msg);
        this.itemRetry = ViewUtil.findViewByID(this.view, R.id.item_retry);
    }

}
