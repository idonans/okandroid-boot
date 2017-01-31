package com.okandroid.boot.thread;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 任务队列，所有任务都在共享的线程池中执行
 * Created by idonans on 16-4-13.
 */
public class TaskQueue {

    private final Object mLock = new Object();
    /**
     * 同时执行的最大任务数量
     */
    private int mMaxCount;
    /**
     * 当前正在执行的任务数量
     */
    private int mCurrentCount;
    /**
     * 当前正在等待的任务数量
     */
    private int mWaitCount;
    private Deque<Task> mQueue = new LinkedList<>();

    public TaskQueue(int maxCount) {
        if (maxCount <= 0) {
            throw new IllegalArgumentException("max count must > 0, max count:"
                    + maxCount);
        }
        this.mMaxCount = maxCount;
    }

    public int getWaitCount() {
        return this.mWaitCount;
    }

    public void setMaxCount(int maxCount) {
        if (maxCount <= 0) {
            return;
        }
        synchronized (mLock) {
            if (this.mMaxCount == maxCount) {
                return;
            }
            this.mMaxCount = maxCount;
        }
        recheckQueue();
    }

    public int getMaxCount() {
        return this.mMaxCount;
    }

    public int getCurrentCount() {
        return this.mCurrentCount;
    }

    public void enqueue(Runnable runnable) {
        Task task = new Task(runnable) {
            @Override
            protected void onTargetFinished() {
                super.onTargetFinished();
                synchronized (mLock) {
                    mCurrentCount--;
                }
                recheckQueue();
            }
        };
        boolean offeredToQueue;
        synchronized (this.mLock) {
            if (this.mCurrentCount < this.mMaxCount) {
                this.mCurrentCount++;
                offeredToQueue = false;
            } else {
                this.mWaitCount++;
                this.mQueue.offerLast(task);
                offeredToQueue = true;
            }
        }
        if (!offeredToQueue) {
            ThreadPool.getInstance().post(task);
        }
    }

    private void recheckQueue() {
        ThreadPool.getInstance().post(new Runnable() {
            @Override
            public void run() {
                for (; recheckQueueInternal(); ) ;
            }
        });
    }

    private boolean recheckQueueInternal() {
        Task postToRun;
        synchronized (this.mLock) {
            if (this.mCurrentCount < this.mMaxCount) {
                postToRun = this.mQueue.pollFirst();
                if (postToRun != null) {
                    this.mWaitCount--;
                    this.mCurrentCount++;
                }
            } else {
                postToRun = null;
            }
        }
        if (postToRun != null) {
            ThreadPool.getInstance().post(postToRun);
        }
        return postToRun != null;
    }

    public void printDetail(StringBuilder builder) {
        String tag = "TaskQueue@" + hashCode();
        builder.append("--" + (tag) + "--\n");
        builder.append("--max count:" + getMaxCount() + "--\n");
        builder.append("--current count:" + getCurrentCount() + "--\n");
        builder.append("--wait count:" + getWaitCount() + "--\n");
        builder.append("--" + (tag) + "--end\n");
    }

    private static class Task implements Runnable {

        private final Runnable target;

        public Task(Runnable target) {
            this.target = target;
        }

        @Override
        public void run() {
            try {
                this.target.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            onTargetFinished();
        }

        protected void onTargetFinished() {
        }

    }

}
