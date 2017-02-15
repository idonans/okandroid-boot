package com.sample.boot.data;

/**
 * Created by idonans on 2017/2/15.
 */
public class SessionManager {

    private static class InstanceHolder {
        private static final SessionManager sInstance = new SessionManager();
    }

    private static boolean sInit;

    public static SessionManager getInstance() {
        SessionManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean hasInit() {
        return sInit;
    }

    private SessionManager() {
    }

}