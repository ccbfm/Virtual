package com.virtual.util.widget.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public abstract class BasePresenter<Data extends BasePackData> {

    private Handler mMainHandler;

    protected Handler mainHandler() {
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mMainHandler;
    }

    protected BaseView<Data> mView;

    public BasePresenter() {

    }

    public void setView(BaseView<Data> view) {
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
