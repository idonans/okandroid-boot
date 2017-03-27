package com.okandroid.boot.data;

import android.text.TextUtils;

import com.okandroid.boot.AppContext;
import com.okandroid.boot.lang.Log;

/**
 * 记录进程信息，在 app 中可能存在多个进程，在处理如缓存路径时进程之间的应当不同，否则可能出现读写冲突。
 * Created by idonans on 16-4-12.
 */
public class ProcessManager {

    private static class InstanceHolder {

        private static final ProcessManager sInstance = new ProcessManager();

    }

    private static boolean sInit;

    public static ProcessManager getInstance() {
        ProcessManager instance = InstanceHolder.sInstance;
        sInit = true;
        return instance;
    }

    public static boolean isInit() {
        return sInit;
    }

    private int mProcessId;
    private String mProcessName;
    private String mProcessTag;
    private static final String PROCESS_TAG_MAIN = "main";

    private ProcessManager() {
        mProcessId = android.os.Process.myPid();
        mProcessName = AppContext.getContext().getApplicationInfo().processName;

        String processName = mProcessName;
        int index = processName.lastIndexOf(':');
        String processSuffix = null;
        if (index >= 0) {
            if (index == 0 || index == processName.length() - 1) {
                throw new IllegalArgumentException("invalid process name " + processName);
            }
            processSuffix = processName.substring(index + 1);
        }

        if (TextUtils.isEmpty(processSuffix)) {
            mProcessTag = PROCESS_TAG_MAIN;
        } else {
            mProcessTag = "sub_" + processSuffix;
        }

        Log.d("process tag:" + mProcessTag + ", id:" + mProcessId + ", name:" + mProcessName);
    }

    public int getProcessId() {
        return mProcessId;
    }

    /**
     * 获取当前进程名称
     */
    public String getProcessName() {
        return mProcessName;
    }

    /**
     * 获取当前进程的标识，可以用于文件名
     */
    public String getProcessTag() {
        return mProcessTag;
    }

    /**
     * 判断当前进程是否为主进程， 主进程的进程名等于包名
     */
    public boolean isMainProcess() {
        return PROCESS_TAG_MAIN.equals(mProcessTag);
    }

}
