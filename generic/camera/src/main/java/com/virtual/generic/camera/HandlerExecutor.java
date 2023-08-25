package com.virtual.generic.camera;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public final class HandlerExecutor implements Executor {

    private final Handler mHandler;

    public HandlerExecutor(@NonNull Handler handler) {
        mHandler = handler;
    }

    @Override
    public void execute(Runnable command) {
        if (!mHandler.post(command)) {
            throw new RejectedExecutionException(mHandler + " is shutting down");
        }
    }
}
