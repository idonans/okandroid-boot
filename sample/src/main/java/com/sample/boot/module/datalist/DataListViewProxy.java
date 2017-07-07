package com.sample.boot.module.datalist;

import com.okandroid.boot.app.ext.page.PageViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.thread.Threads;
import com.okandroid.boot.util.NetUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by idonans on 2017/4/21.
 */

public class DataListViewProxy extends PageViewProxy<DataListView> {

    private static final String TAG = "DataListViewProxy";

    public DataListViewProxy(DataListView view) {
        super(view);
    }

    @Override
    protected Disposable createPageLoadingRequest(final int pageNo) {
        Log.d(TAG + " createPageLoadingRequest pageNo:" + pageNo);

        return Single.fromCallable(new Callable<Collection>() {
            @Override
            public Collection call() throws Exception {
                ArrayList items = new ArrayList(10);

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    int randomLine = ((int) (Math.random() * 10) % 10);

                    builder.setLength(0);
                    builder.append(pageNo + "#" + i + "#" + randomLine);

                    for (int j = 0; j < randomLine; j++) {
                        builder.append("\nrandom line text " + j);
                    }
                    items.add(Item.from(builder.toString()));
                }
                Threads.sleepQuietly(1000);

                if (!NetUtil.hasActiveNetwork()) {
                    throw new RuntimeException("network error");
                }

                if (Math.random() > 0.8) {
                    if (pageNo == 0) {
                        return Collections.EMPTY_LIST;
                    } else {
                        throw new RuntimeException("random error");
                    }
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

    public static class Item {
        public String content;
        public boolean _expand;

        public static Item from(String content) {
            Item item = new Item();
            item.content = content;
            return item;
        }
    }

}
