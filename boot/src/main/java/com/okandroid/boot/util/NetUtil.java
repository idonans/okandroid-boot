package com.okandroid.boot.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.net.ConnectivityManagerCompat;

import com.okandroid.boot.AppContext;

/**
 * 网络相关
 * Created by idonans on 16-5-3.
 */
public class NetUtil {

    /**
     * 是否联网
     */
    public static boolean hasActiveNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    /**
     * 当前活动网络是否是计量网络(计量网络通常会产生流量费用)
     */
    public static boolean isActiveNetworkMetered() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return ConnectivityManagerCompat.isActiveNetworkMetered(connectivityManager);
    }

}
