package com.okandroid.boot.app.ext.pageloading;

import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.widget.PageDataAdapter;

import java.io.IOException;
import java.util.Collection;

import rx.Subscription;

/**
 * Created by idonans on 2017/4/20.
 */

public abstract class PageLoadingViewProxy<T extends PageLoadingView> extends PreloadViewProxy<T> {

    private static final String TAG = "PageLoadingViewProxy";

    public PageLoadingViewProxy(T view) {
        super(view);
    }

    @Override
    protected void onPreDataLoadBackground() {
    }

    @Override
    public void onPrepared() {
        super.onPrepared();

        loadFirstPage();
    }

    // 上一次成功加载的数据页
    private int mLastLoadSuccessPageNo = -1;
    // 当前正在加载的数据页
    private int mCurrentLoadingPageNo = -1;
    // 第一页从 0 开始
    private final int mFirstPageNo = 0;

    // 总页数
    private int mTotalPage = -1;

    public void loadFirstPage() {
        tryLoadPage(mFirstPageNo);
    }

    public void loadNextPage() {
        tryLoadPage(mLastLoadSuccessPageNo + 1);
    }

    protected void tryLoadPage(int pageNo) {
        if (pageNo < mFirstPageNo) {
            Log.e(TAG + " tryLoadPage with invalid page no " + pageNo);
            return;
        }

        if (!isPrepared()) {
            return;
        }

        PageLoadingView view = getView();
        if (view == null) {
            return;
        }

        if (mCurrentLoadingPageNo == pageNo) {
            Log.d(TAG + " tryLoadPage ignored, equals with current loading page no " + pageNo);
            return;
        }

        mCurrentLoadingPageNo = pageNo;

        replaceDefaultSubscription(null);

        boolean firstPage = pageNo == mFirstPageNo;
        view.showPageLoadingStatus(new PageDataAdapter.PageLoadingStatus.Builder()
                .setFirstPage(firstPage)
                .setLoading(true)
                .setLoadSuccess(false)
                .setLoadFail(false)
                .build());

        replaceDefaultSubscription(createPageLoadingSubscription(pageNo));
    }

    @Override
    public void close() throws IOException {
        super.close();

        mCurrentLoadingPageNo = -1;
    }

    /**
     * 显示时，可以根据当前页和总页数确定是否加载到了最后一页，注意：仅当 pageNo >= 0 时有效. 否则总是认为还有下一页.
     * 由于总页数是动态更新的，因此不能确定 pageNo 总是小于 totalPage
     */
    public static class ExtraPageMessage {
        /**
         * 当前页, 当不小于 0 时有效
         */
        public int pageNo = -1;
        /**
         * 总页数, 当不小于 0 时有效
         */
        public int totalPage = -1;

        /**
         * 当发生分页数据加载失败时，标识当前是否发生了网络错误
         */
        public boolean networkError;

        /**
         * 当发生分页数据加载失败时，标识当前是否发生了服务器错误
         */
        public boolean serverError;

        /**
         * 其他辅助消息，如发生错误时的详细提示(用户名或密码错误), 或者成功时的消息(成功加载 10 条数据)
         */
        public CharSequence detailMessage;
    }

    protected abstract Subscription createPageLoadingSubscription(final int pageNo);

    /**
     * @param data    如果加载失败, 值为 null.
     * @param message
     */
    public void notifyPageLoadingEnd(int pageNo, Collection data, ExtraPageMessage message) {
        if (!isPrepared()) {
            return;
        }

        PageLoadingView view = getView();
        if (view == null) {
            return;
        }

        if (mCurrentLoadingPageNo != pageNo) {
            return;
        }

        // 如果在本次分页请求中有明确指定总页数，则更新全局总页数的值
        if (message != null && message.totalPage >= 0) {
            mTotalPage = message.totalPage;
        }

        boolean success = data != null;
        mCurrentLoadingPageNo = -1;
        if (success) {
            mLastLoadSuccessPageNo = pageNo;
        }

        if (mTotalPage < 0) {
            // 如果没有指定总页数，根据 data 的内容猜测一下总页数(认为到某一个 size 为 0 的 data 时, 是最后一页)
            if (success && data.isEmpty()) {
                mTotalPage = pageNo + 1;
            }
        }

        if (message == null) {
            message = new ExtraPageMessage();
        }

        // 使用更新后的分页信息填充 message
        message.pageNo = pageNo;
        message.totalPage = mTotalPage;

        boolean firstPage = pageNo == mFirstPageNo;

        view.showPageLoadingStatus(new PageDataAdapter.PageLoadingStatus.Builder()
                .setFirstPage(firstPage)
                .setExtraMessage(message)
                .setLoading(false)
                .setLoadSuccess(success)
                .setLoadFail(!success)
                .build());

        if (success) {
            view.showPageContent(firstPage, data);
        }
    }

}
