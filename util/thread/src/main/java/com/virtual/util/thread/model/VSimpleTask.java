package com.virtual.util.thread.model;

import android.util.Log;

public abstract class VSimpleTask<T> extends VTask<T> {

    @Override
    protected void onCancel() {
        Log.e("VTask", "onCancel: " + Thread.currentThread());
    }

    @Override
    protected void onFail(Throwable t) {
        Log.e("VTask", "onFail: ", t);
    }
}
