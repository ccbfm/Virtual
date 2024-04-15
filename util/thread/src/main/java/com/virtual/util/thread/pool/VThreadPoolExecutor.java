package com.virtual.util.thread.pool;

import android.util.Log;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class VThreadPoolExecutor extends ThreadPoolExecutor {

    private final AtomicInteger mSubmittedCount = new AtomicInteger();

    private final VLinkedBlockingQueue mWorkQueue;
    private final VThreadFactory mVThreadFactory;

    public VThreadPoolExecutor(int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               VLinkedBlockingQueue workQueue,
                               VThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        mVThreadFactory = threadFactory;
        workQueue.mPool = this;
        mWorkQueue = workQueue;
    }

    public VThreadFactory getVThreadFactory() {
        return mVThreadFactory;
    }

    public int getSubmittedCount() {
        return mSubmittedCount.get();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        mSubmittedCount.decrementAndGet();
        super.afterExecute(r, t);
    }

    @Override
    public void execute(Runnable command) {
        if (this.isShutdown()) {
            return;
        }
        mSubmittedCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException ignore) {
            Log.e("VThreadPoolExecutor", "This will not happen!");
            mWorkQueue.offer(command);
        } catch (Throwable t) {
            mSubmittedCount.decrementAndGet();
        }
    }
}
