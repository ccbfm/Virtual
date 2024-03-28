package com.virtual.util.widget.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public abstract class VBasePresenter<Data extends VBasePackData> {

    private Handler mMainHandler;

    protected Handler mainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    protected VBaseView<Data> mView;

    public VBasePresenter() {

    }

    public void setView(VBaseView<Data> view) {
        mView = view;
    }

    public void destroy() {

    }

    protected Context context() {
        if (mView == null) {
            throw new NullPointerException("mView is null");
        }
        return mView.context();
    }

    protected void presenterCallback(int action, Data data) {
        if (mView == null) {
            throw new NullPointerException("mView is null");
        }
        mView.presenterCallback(action, data);
    }
}
