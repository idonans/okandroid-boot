package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.NetUtil;

import java.util.ArrayList;
import java.util.Collection;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by idonans on 2017/4/21.
 */

public class DataListViewProxy extends PageLoadingViewProxy<DataListView> {

    public DataListViewProxy(DataListView view) {
        super(view);
    }

    @Override
    protected Subscription createPageLoadingSubscription(final int pageNo) {
        return Observable.just("1")
                .map(new Func1<String, Collection>() {
                    @Override
                    public Collection call(String s) {
                        ArrayList items = new ArrayList(10);
                        for (int i = 0; i < 20; i++) {
                            items.add(pageNo + "#" + i);
                        }
                        Threads.sleepQuietly(1000);

                        if (Math.random() > 0.5) {
                            throw new RuntimeException("random error");
                        }

                        return items;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Collection>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExtraPageMessage extraPageMessage = new ExtraPageMessage();

                        if (!NetUtil.hasActiveNetwork()) {
                            // 模拟网络错误
                            extraPageMessage.networkError = true;
                            extraPageMessage.detailMessage = "模拟测试网络错误";
                        } else {
                            // 模拟服务器错误
                            extraPageMessage.serverError = true;
                            extraPageMessage.detailMessage = "模拟测试服务器错误";
                        }

                        notifyPageLoadingEnd(pageNo, null, extraPageMessage);
                    }

                    @Override
                    public void onNext(Collection collection) {
                        ExtraPageMessage extraPageMessage = new ExtraPageMessage();
                        extraPageMessage.totalPage = 5;
                        notifyPageLoadingEnd(pageNo, collection, extraPageMessage);
                    }
                });
    }

}
