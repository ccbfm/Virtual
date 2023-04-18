package com.virtual.util.thread.pool;

import java.util.concurrent.LinkedBlockingQueue;

public final class VLinkedBlockingQueue extends LinkedBlockingQueue<Runnable> {

    private static final long serialVersionUID = -4879119049358294386L;
    /*public*/ volatile VThreadPoolExecutor mPool;

    private int mCapacity = Integer.MAX_VALUE;

    public VLinkedBlockingQueue() {
    }

    public VLinkedBlockingQueue(boolean isAddSubThreadFirstThenAddQueue) {
        super();
        if (isAddSubThreadFirstThenAddQueue) {
            mCapacity = 0;
        }
    }

    public VLinkedBlockingQueue(int capacity) {
        super();
        mCapacity = capacity;
    }

    @Override
    public boolean offer(Runnable runnable) {
        if (mCapacity <= size() &&
                mPool != null && mPool.getPoolSize() < mPool.getMaximumPoolSize()) {
            // create a non-core thread
            return false;
        }
        return super.offer(runnable);
    }

}
