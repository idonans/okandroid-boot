package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.pageloading.PageLoadingViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.NetUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by idonans on 2017/4/21.
 */

public class DataListViewProxy extends PageLoadingViewProxy<DataListView> {

    private static final String TAG = "DataListViewProxy";

    public DataListViewProxy(DataListView view) {
        super(view);
    }

    @Override
    protected Disposable createPageLoadingRequest(final int pageNo) {
        Log.d(TAG + " createPageLoadingRequest pageNo:" + pageNo);

        return Single.just("1")
                .map(new Function<String, Collection>() {
                    @Override
                    public Collection apply(@NonNull String s) throws Exception {
                        ArrayList items = new ArrayList(10);
                        for (int i = 0; i < 20; i++) {
                            items.add(pageNo + "#" + i + "\n line text\n line text\n line text\n line text\n line text\n line text");
                        }
                        Threads.sleepQuietly(1000);

                        if (!NetUtil.hasActiveNetwork()) {
                            throw new RuntimeException("network error");
                        }

                        if (Math.random() > 0.5 && pageNo == 0) {
                            return Collections.EMPTY_LIST;
                        } else if (Math.random() > 0.8) {
                            throw new RuntimeException("random error");
                        }

                        return items;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Collection>() {
                    @Override
                    public void accept(@NonNull Collection collection) throws Exception {
                        ExtraPageMessage extraPageMessage = new ExtraPageMessage();
                        extraPageMessage.totalPage = 5;
                        notifyPageLoadingEnd(pageNo, collection, extraPageMessage);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
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
                });
    }

}
