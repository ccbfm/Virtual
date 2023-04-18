package com.virtual.util.thread.pool;

import android.util.Log;

import com.virtual.util.thread.model.VTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class VThreadPoolConfig {
    private final int mCpuCount;
    private final Map<Integer, Map<Integer, VThreadPoolExecutor>> mTypePriorityPools = new HashMap<>();
    private final Map<VTask<?>, VThreadPoolExecutor> mTaskPoolMap = new ConcurrentHashMap<>();

    private VThreadPoolConfig() {
        mCpuCount = Runtime.getRuntime().availableProcessors();
    }

    private static final class Singleton {
        private static final VThreadPoolConfig INSTANCE = new VThreadPoolConfig();
    }

    public static VThreadPoolConfig instance() {
        return Singleton.INSTANCE;
    }

    public Map<VTask<?>, VThreadPoolExecutor> getTaskPoolMap() {
        return mTaskPoolMap;
    }

    private VThreadPoolExecutor createPool(@VThreadType final int type, final int priority) {
        switch (type) {
            case VThreadType.SINGLE:
                return new VThreadPoolExecutor(1, 1,
                        0L, TimeUnit.MILLISECONDS,
                        new VLinkedBlockingQueue(),
                        new VThreadFactory("single", priority)
                );
            case VThreadType.CACHED:
                return new VThreadPoolExecutor(0, 128,
                        60L, TimeUnit.SECONDS,
                        new VLinkedBlockingQueue(true),
                        new VThreadFactory("cached", priority)
                );
            case VThreadType.IO:
                return new VThreadPoolExecutor(2 * mCpuCount + 1, 2 * mCpuCount + 1,
                        30, TimeUnit.SECONDS,
                        new VLinkedBlockingQueue(),
                        new VThreadFactory("io", priority)
                );
            case VThreadType.CPU:
                return new VThreadPoolExecutor(mCpuCount + 1, 2 * mCpuCount + 1,
                        30, TimeUnit.SECONDS,
                        new VLinkedBlockingQueue(true),
                        new VThreadFactory("cpu", priority)
                );
            default:
                return new VThreadPoolExecutor(type, type,
                        0L, TimeUnit.MILLISECONDS,
                        new VLinkedBlockingQueue(),
                        new VThreadFactory("fixed(" + type + ")", priority)
                );
        }
    }

    public VThreadPoolExecutor getPoolByType(final int type) {
        return getPoolByType(type, true);
    }

    /**
     * @param type   线程池类型
     * @param cached 是否使用缓存
     * @return VThreadPoolExecutor
     */
    public VThreadPoolExecutor getPoolByType(final int type, final boolean cached) {
        return getPoolByTypeAndPriority(type, Thread.NORM_PRIORITY, cached);
    }

    public VThreadPoolExecutor getPoolByTypeAndPriority(final int type, final int priority) {
        return getPoolByTypeAndPriority(type, priority, true);
    }

    public VThreadPoolExecutor getPoolByTypeAndPriority(final int type, final int priority, final boolean cached) {
        synchronized (mTypePriorityPools) {
            VThreadPoolExecutor pool;
            Map<Integer, VThreadPoolExecutor> priorityPools = null;
            if (cached) {
                priorityPools = mTypePriorityPools.get(type);
            }
            if (priorityPools == null) {
                pool = createPool(type, priority);
                if (cached) {
                    priorityPools = new ConcurrentHashMap<>();
                    priorityPools.put(priority, pool);
                    mTypePriorityPools.put(type, priorityPools);
                }
            } else {
                pool = priorityPools.get(priority);
                if (pool == null) {
                    pool = createPool(type, priority);
                    priorityPools.put(priority, pool);
                }
            }
            return pool;
        }
    }


    public <T> void execute(final VThreadPoolExecutor pool, final VTask<T> task) {
        execute(pool, task, 0, 0, null);
    }

    public <T> void executeWithDelay(final VThreadPoolExecutor pool,
                                     final VTask<T> task,
                                     final long delay,
                                     final TimeUnit unit) {
        execute(pool, task, delay, 0, unit);
    }

    public <T> void executeAtFixedRate(final VThreadPoolExecutor pool,
                                       final VTask<T> task,
                                       long delay,
                                       final long period,
                                       final TimeUnit unit) {
        execute(pool, task, delay, period, unit);
    }

    private <T> void execute(final VThreadPoolExecutor pool, final VTask<T> task,
                             long delay, final long period, final TimeUnit unit) {
        synchronized (mTaskPoolMap) {
            if (mTaskPoolMap.get(task) != null) {
                Log.e("VThreadPoolConfig", "Task can only be executed once.");
                return;
            }
            mTaskPoolMap.put(task, pool);
        }
        if (delay != 0) {
            task.setDelay(unit.toMillis(delay));
        }
        if (period != 0) {
            task.setPeriod(unit.toMillis(period));
        }
        pool.execute(task);
    }
}
