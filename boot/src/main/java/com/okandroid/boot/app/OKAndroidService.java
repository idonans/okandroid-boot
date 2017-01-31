package com.okandroid.boot.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * 基类 Service.<br>
 * 应用中常见的 Service 用处之一是作为进程重启的触发器， 后台任务使用线程实现更方便。
 * Created by idonans on 16-4-14.
 */
public class OKAndroidService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START_STICKY 如果 service 被 kill， 则系统会稍后尝试重启该服务，但是不会保存 intent 对象。
        return START_STICKY;
    }

}
