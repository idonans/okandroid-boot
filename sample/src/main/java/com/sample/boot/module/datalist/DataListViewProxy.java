package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.thread.Threads;

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
                        for (int i = 0; i < 10; i++) {
                            items.add(pageNo + "#" + i);
                        }
                        Threads.sleepQuietly(3000);
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
                        notifyPageLoadingEnd(pageNo, null, null);
                    }

                    @Override
                    public void onNext(Collection collection) {
                        ExtraPageMessage extraPageMessage = new ExtraPageMessage();
                        extraPageMessage.totalPage = 20;
                        notifyPageLoadingEnd(pageNo, collection, extraPageMessage);
                    }
                });
    }

}
