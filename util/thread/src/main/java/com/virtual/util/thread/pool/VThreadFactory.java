package com.virtual.util.thread.pool;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class VThreadFactory extends AtomicLong implements ThreadFactory {

    private static final long serialVersionUID = -2773621533590558580L;
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    private final String mNamePrefix;
    private final int mPriority;
    private final boolean mIsDaemon;

    VThreadFactory(String prefix, int priority) {
        this(prefix, priority, false);
    }

    VThreadFactory(String prefix, int priority, boolean isDaemon) {
        mNamePrefix = prefix + "-pool-" +
                POOL_NUMBER.getAndIncrement() +
                "-thread-";
        this.mPriority = priority;
        this.mIsDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, mNamePrefix + getAndIncrement()) {
            @Override
            public void run() {
                try {
                    super.run();
                } catch (Throwable t) {
                    Log.e("VThreadFactory", "Request threw uncaught throwable", t);
                }
            }
        };
        t.setDaemon(mIsDaemon);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                System.out.println("Throwable:" + e);
            }
        });
        t.setPriority(mPriority);
        return t;
    }
}
