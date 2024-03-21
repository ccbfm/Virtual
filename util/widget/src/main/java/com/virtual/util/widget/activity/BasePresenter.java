package com.virtual.util.widget.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

public abstract class BasePresenter<Data extends BasePackData> {

    private Data mEmptyData;
    private Handler mMainHandler;

    protected Data emptyData() {
        if (mEmptyData == null) {
            mEmptyData = createEmptyPackData();
        }
        return mEmptyData;
    }

    protected abstract @NonNull Data createEmptyPackData();

    protected Handler mainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    protected final BaseView<Data> mView;

    public BasePresenter(BaseView<Data> view) {
        mView = view;
    }

    public void destroy() {

    }

    protected Context context() {
        return mView.context();
    }

    protected void presenterCallback(int action) {
        presenterCallback(action, emptyData());
    }

    protected void presenterCallback(int action, @NonNull Data data) {
        mView.presenterCallback(action, data);
    }
}
