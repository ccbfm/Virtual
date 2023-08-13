package com.virtual.util.socket.net.work;

import android.util.Log;

import androidx.annotation.CallSuper;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VWork implements Runnable {
    private final AtomicInteger mState = new AtomicInteger(VState.NEW);
    protected Thread mRunner;

    @Override
    public void run() {
        try {
            if (mRunner == null) {
                if (!mState.compareAndSet(VState.NEW, VState.RUNNING)) {
                    return;
                }
                mRunner = Thread.currentThread();
            } else {
                if (mState.get() != VState.RUNNING) {
                    return;
                }
            }

            doWork();
        } catch (IOException exception) {
            Log.e("VWork", "run IOException: ", exception);
            doThrowable();
        } catch (Throwable throwable) {
            Log.e("VWork", "run Throwable: ", throwable);
            doThrowable();
        }
    }

    protected abstract void doWork() throws Throwable;

    protected abstract void doThrowable();

    public abstract void start();

    protected boolean isRunning() {
        return mState.get() == VState.RUNNING;
    }

    @CallSuper
    public void close() {
        synchronized (mState) {
            if (mState.get() > VState.RUNNING) return;
            mState.set(VState.STOP);
        }
        if (mRunner != null) {
            mRunner.interrupt();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface VState {
        int NEW = 0;
        int RUNNING = 1;
        int STOP = 2;
    }


}
