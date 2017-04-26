package com.okandroid.boot.app.ext.pageloading;

import com.okandroid.boot.app.ext.preload.PreloadViewProxy;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.widget.PageDataAdapter;

import java.io.IOException;
import java.util.Collection;

import io.reactivex.disposables.Disposable;

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

    /**
     * 第一页的数据是否总是可以发起请求，即使当前总页数是 0.
     */
    protected boolean alwaysAllowLoadFirstPage() {
        return true;
    }

    protected void tryLoadPage(int pageNo) {
        if (pageNo < mFirstPageNo) {
            Log.d(TAG + " tryLoadPage with invalid page [" + pageNo + "/" + mTotalPage + "]");
            return;
        }

        if (alwaysAllowLoadFirstPage()
                && mFirstPageNo == pageNo) {
            // 总是允许加载第一页
        } else if (mTotalPage >= 0
                && pageNo >= mTotalPage) {
            Log.d(TAG + " tryLoadPage with no more page [" + pageNo + "/" + mTotalPage + "]");
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

        replaceDefaultRequestHolder(null);

        boolean firstPage = pageNo == mFirstPageNo;
        view.showPageLoadingStatus(new PageDataAdapter.PageLoadingStatus.Builder()
                .setFirstPage(firstPage)
                .setLoading(true)
                .setLoadSuccess(false)
                .setLoadFail(false)
                .build());

        replaceDefaultRequestHolder(createPageLoadingRequest(pageNo));
    }

    @Override
    public void close() throws IOException {
        super.close();

        mCurrentLoadingPageNo = -1;
    }

    /**
     * 当前页加载时的额外的信息
     */
    public static class ExtraPageMessage implements PageDataAdapter.PageLoadingStatus.PageLoadingContentValidator {
        /**
         * 当前页, 当不小于 0 时有效
         */
        public int pageNo = -1;

        /**
         * 总页数, 当不小于 0 时有效
         */
        public int totalPage = -1;

        /**
         * 当前页加载的数据是否是空. 某些情况下, 如果第一页是空, 可能需要使用特殊的样式显示.
         */
        public boolean emptyContent;

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

        /**
         * 是否是最后一页
         *
         * @return
         */
        public boolean isLastPage() {
            return this.pageNo >= 0
                    && this.totalPage >= 0
                    && this.pageNo >= this.totalPage - 1;
        }

        @Override
        public boolean isPageLoadingContentEmpty() {
            return emptyContent;
        }

    }

    protected abstract Disposable createPageLoadingRequest(final int pageNo);

    /**
     * @param data    如果加载失败, 值为 null.
     * @param message
     */
    public void notifyPageLoadingEnd(int pageNo, Collection data, ExtraPageMessage message) {
        if (mCurrentLoadingPageNo != pageNo) {
            return;
        }

        mCurrentLoadingPageNo = -1;

        if (!isPrepared()) {
            return;
        }

        PageLoadingView view = getView();
        if (view == null) {
            return;
        }

        // 如果在本次分页请求中有明确指定总页数，则更新全局总页数的值
        if (message != null && message.totalPage >= 0) {
            mTotalPage = message.totalPage;
        }

        final boolean success = data != null;
        if (success) {
            mLastLoadSuccessPageNo = pageNo;
        }

        final boolean emptyContent = data == null || data.isEmpty();

        if (mTotalPage < 0) {
            // 如果没有指定总页数，根据 data 的内容猜测一下总页数(认为加载到空数据时, 是最后一页)
            if (success && emptyContent) {
                mTotalPage = pageNo;
            }
        }

        if (message == null) {
            message = new ExtraPageMessage();
        }

        // 使用更新后的分页信息填充 message
        message.pageNo = pageNo;
        message.totalPage = mTotalPage;
        message.emptyContent = emptyContent;

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
