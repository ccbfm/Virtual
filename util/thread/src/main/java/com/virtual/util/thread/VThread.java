package com.virtual.util.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.virtual.util.thread.model.VTask;
import com.virtual.util.thread.pool.VThreadPoolConfig;
import com.virtual.util.thread.pool.VThreadPoolExecutor;
import com.virtual.util.thread.pool.VThreadType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class VThread {

    private static Handler sMainHandler;
    private static Handler sThreadHandler;

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private static void checkMainHandler() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    private static void checkThreadHandler() {
        if (sThreadHandler == null) {
            HandlerThread handlerThread = new HandlerThread("thread-handler");
            handlerThread.start();
            sThreadHandler = new Handler(handlerThread.getLooper());
        }
    }

    public static Handler getMainHandler() {
        checkMainHandler();
        return sMainHandler;
    }

    public static Handler getThreadHandler() {
        checkThreadHandler();
        return sThreadHandler;
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            getMainHandler().post(runnable);
        }
    }

    public static void runOnUiThreadDelayed(final Runnable runnable, long delayMillis) {
        getMainHandler().postDelayed(runnable, delayMillis);
    }


    /**
     * 固定数量线程池
     *
     * @param size 大小
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getFixedPool(@IntRange(from = 1) final int size) {
        return VThreadPoolConfig.instance().getPoolByType(size);
    }

    public static VThreadPoolExecutor getFixedPool(@IntRange(from = 1) final int size, final boolean cached) {
        return VThreadPoolConfig.instance().getPoolByType(size, cached);
    }

    /**
     * 固定数量线程池
     *
     * @param size     大小
     * @param priority 线程优先级
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getFixedPool(@IntRange(from = 1) final int size,
                                                   @IntRange(from = 1, to = 10) final int priority) {
        return VThreadPoolConfig.instance().getPoolByTypeAndPriority(size, priority);
    }

    /**
     * 单个工作线程操作的线程池
     *
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getSinglePool() {
        return VThreadPoolConfig.instance().getPoolByType(VThreadType.SINGLE);
    }

    /**
     * 单个工作线程操作的线程池
     *
     * @param priority 线程优先级
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getSinglePool(@IntRange(from = 1, to = 10) final int priority) {
        return VThreadPoolConfig.instance().getPoolByTypeAndPriority(VThreadType.SINGLE, priority);
    }

    /**
     * 线程池根据需要创建新线程，可重用以前构造的线程
     * 对大小为128的队列进行操作。
     *
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getCachedPool() {
        return VThreadPoolConfig.instance().getPoolByType(VThreadType.CACHED);
    }

    /**
     * 线程池根据需要创建新线程，可重用以前构造的线程
     * 对大小为128的队列进行操作。
     *
     * @param priority 线程优先级
     * @return VThreadPoolExecutor
     */
    public static VThreadPoolExecutor getCachedPool(@IntRange(from = 1, to = 10) final int priority) {
        return VThreadPoolConfig.instance().getPoolByTypeAndPriority(VThreadType.CACHED, priority);
    }

    /**
     * 创建（2*CPU_COUNT+1）个线程的线程池
     *
     * @return IO线程池
     */
    public static VThreadPoolExecutor getIoPool() {
        return VThreadPoolConfig.instance().getPoolByType(VThreadType.IO);
    }

    /**
     * 创建（2*CPU_COUNT+1）个线程的线程池
     *
     * @param priority 线程的优先级
     * @return IO线程池
     */
    public static VThreadPoolExecutor getIoPool(@IntRange(from = 1, to = 10) final int priority) {
        return VThreadPoolConfig.instance().getPoolByTypeAndPriority(VThreadType.IO, priority);
    }

    /**
     * 创建（CPU_COUNT+1）线程的线程池
     * 线程数等于（2*CPU_COUNT+1）。
     *
     * @return cpu线程池
     */
    public static VThreadPoolExecutor getCpuPool() {
        return VThreadPoolConfig.instance().getPoolByType(VThreadType.CPU);
    }

    /**
     * 创建（CPU_COUNT+1）线程的线程池
     * 线程数等于（2*CPU_COUNT+1）。
     *
     * @param priority 线程的优先级
     * @return cpu线程池
     */
    public static VThreadPoolExecutor getCpuPool(@IntRange(from = 1, to = 10) final int priority) {
        return VThreadPoolConfig.instance().getPoolByTypeAndPriority(VThreadType.CPU, priority);
    }


    public static <T> void execute(final VThreadPoolExecutor executor, final VTask<T> task) {
        VThreadPoolConfig.instance().execute(executor, task);
    }


    public static <T> void executeWithDelay(final VThreadPoolExecutor executor,
                                            final VTask<T> task,
                                            final long delay,
                                            final TimeUnit unit) {
        VThreadPoolConfig.instance().executeWithDelay(executor, task, delay, unit);
    }

    public static <T> void executeAtFixRate(final VThreadPoolExecutor executor,
                                            final VTask<T> task,
                                            final long period,
                                            final TimeUnit unit) {
        VThreadPoolConfig.instance().executeAtFixedRate(executor, task, 0, period, unit);
    }


    public static <T> void executeAtFixRate(final VThreadPoolExecutor executor,
                                            final VTask<T> task,
                                            long initialDelay,
                                            final long period,
                                            final TimeUnit unit) {
        VThreadPoolConfig.instance().executeAtFixedRate(executor, task, initialDelay, period, unit);
    }

    public static void cancel(final VTask<?> task) {
        if (task == null) {
            return;
        }
        task.cancel();
    }

    public static void cancel(final VTask<?>... tasks) {
        if (tasks == null || tasks.length == 0) return;
        for (VTask<?> task : tasks) {
            if (task == null) {
                continue;
            }
            task.cancel();
        }
    }

    public static void cancel(final List<VTask<?>> tasks) {
        if (tasks == null || tasks.size() == 0) return;
        for (VTask<?> task : tasks) {
            if (task == null) {
                continue;
            }
            task.cancel();
        }
    }

    public static void cancel(@NonNull VThreadPoolExecutor executorService) {
        Map<VTask<?>, VThreadPoolExecutor> taskPoolMap = VThreadPoolConfig.instance().getTaskPoolMap();
        for (Map.Entry<VTask<?>, VThreadPoolExecutor> taskTaskInfoEntry : taskPoolMap.entrySet()) {
            if (taskTaskInfoEntry.getValue() == executorService) {
                cancel(taskTaskInfoEntry.getKey());
            }
        }
    }
}
