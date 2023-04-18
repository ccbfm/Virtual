package com.virtual.util.thread.model;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;

import com.virtual.util.thread.VThread;
import com.virtual.util.thread.pool.VThreadPoolConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VTask<T> implements Runnable {

    private final AtomicInteger mState = new AtomicInteger(VTaskState.NEW);
    /**
     * 延迟
     */
    private long mDelay = 0L;
    /**
     * 循环
     */
    private long mPeriod = 0L;

    private volatile Thread mRunner;

    private Handler mResultHandler;

    private long mTimeoutMillis = 0L;
    private OnTimeoutListener mTimeoutListener;

    public void setDelay(long delay) {
        mDelay = delay;
    }

    public void setPeriod(long period) {
        mPeriod = period;
    }

    public VTask<T> setResultHandler(Handler resultHandler) {
        mResultHandler = resultHandler;
        return this;
    }

    public VTask<T> setMainResultHandler() {
        mResultHandler = VThread.getMainHandler();
        return this;
    }

    public VTask<T> setTimeout(final long timeoutMillis, final OnTimeoutListener listener) {
        mTimeoutMillis = timeoutMillis;
        mTimeoutListener = listener;
        return this;
    }

    protected abstract T doTask() throws Throwable;

    protected abstract void onSuccess(T result);

    protected abstract void onCancel();

    protected abstract void onFail(Throwable t);

    @Override
    public void run() {
        try {
            if (mRunner == null) {
                if (!mState.compareAndSet(VTaskState.NEW, VTaskState.RUNNING)) {
                    return;
                }
                mRunner = Thread.currentThread();
                if (mPeriod > 0 && mTimeoutListener != null) {
                    Log.w("VTask", "Scheduled task doesn't support timeout.");
                } else {
                    VThread.getThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runTimeout();
                        }
                    }, mTimeoutMillis);
                }
            } else {
                if (mState.get() != VTaskState.RUNNING) {
                    return;
                }
            }

            if (mDelay > 0) {
                synchronized (this) {
                    wait(mDelay);
                }
                if (mState.get() != VTaskState.RUNNING) {
                    return;
                }
            }

            boolean done = mPeriod <= 0;
            if (done) {
                final T result = doTask();
                if (!mState.compareAndSet(VTaskState.RUNNING, VTaskState.COMPLETING)) {
                    return;
                }
                runSuccess(result, true);
            } else {
                while (mState.get() == VTaskState.RUNNING) {
                    final T result = doTask();
                    runSuccess(result, false);
                    synchronized (this) {
                        wait(mPeriod);
                    }
                }
            }
        } catch (InterruptedException ignore) {
            mState.compareAndSet(VTaskState.CANCELLED, VTaskState.INTERRUPTED);
        } catch (final Throwable throwable) {
            if (!mState.compareAndSet(VTaskState.RUNNING, VTaskState.EXCEPTIONAL)) {
                return;
            }
            runFail(throwable);
            //Log.e("VTask", "run-Throwable: ", e);
        }
    }

    private void runSuccess(final T result, boolean done) throws Throwable {
        if (mResultHandler == null) {
            onSuccess(result);
            if (done) {
                onDone();
            }
        } else {
            mResultHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(result);
                    if (done) {
                        onDone();
                    }
                }
            });
        }
    }

    private void runFail(Throwable throwable) {
        if (mResultHandler == null) {
            onFail(throwable);
            onDone();
        } else {
            mResultHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFail(throwable);
                    onDone();
                }
            });
        }
    }

    private void runCancel() {
        if (mResultHandler == null) {
            onCancel();
            onDone();
        } else {
            mResultHandler.post(new Runnable() {
                @Override
                public void run() {
                    onCancel();
                    onDone();
                }
            });
        }
    }

    private void runTimeout() {
        if (!isDone() && mTimeoutListener != null) {
            timeout();
            if (mResultHandler == null) {
                mTimeoutListener.onTimeout();
                onDone();
            } else {
                mResultHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTimeoutListener.onTimeout();
                        onDone();
                    }
                });
            }
        }
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean mayInterruptIfRunning) {
        synchronized (mState) {
            if (mState.get() > VTaskState.RUNNING) return;
            mState.set(VTaskState.CANCELLED);
        }

        if (mayInterruptIfRunning) {
            if (mRunner != null) {
                mRunner.interrupt();
            }
        }

        runCancel();
    }

    @CallSuper
    protected void onDone() {
        VThreadPoolConfig.instance().getTaskPoolMap().remove(this);
    }

    private void timeout() {
        synchronized (mState) {
            if (mState.get() > VTaskState.RUNNING) return;
            mState.set(VTaskState.TIMEOUT);
        }
        if (mRunner != null) {
            mRunner.interrupt();
        }
    }

    public boolean isCanceled() {
        return mState.get() >= VTaskState.CANCELLED;
    }

    public boolean isDone() {
        return mState.get() > VTaskState.RUNNING;
    }

    @IntDef({
            VTaskState.NEW,
            VTaskState.RUNNING,
            VTaskState.EXCEPTIONAL,
            VTaskState.COMPLETING,
            VTaskState.CANCELLED,
            VTaskState.INTERRUPTED,
            VTaskState.TIMEOUT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VTaskState {
        int NEW = 0;
        int RUNNING = 1;
        int EXCEPTIONAL = 2;
        int COMPLETING = 3;
        int CANCELLED = 4;
        int INTERRUPTED = 5;
        int TIMEOUT = 6;
    }

    public interface OnTimeoutListener {
        void onTimeout();
    }
}
