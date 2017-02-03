package com.sample.boot.data;

import com.okandroid.boot.data.StorageManager;

/**
 * Created by idonans on 2017/2/3.
 */
public class SessionManager {

    private static class InstanceHolder {
        private static final SessionManager sInstance = new SessionManager();
    }

    public static SessionManager getInstance() {
        return InstanceHolder.sInstance;
    }

    private static final String TAG = "SessionManager";
    private final StorageManager mStorageManager;

    private SessionManager() {
        mStorageManager = StorageManager.getInstance();
    }

}