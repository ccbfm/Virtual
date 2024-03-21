package com.virtual.util.widget;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;

public final class VViewStorage {

    private final SparseArray<View> mViews;
    private final View mRootView;

    public VViewStorage(View rootView) {
        mRootView = rootView;
        mViews = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mRootView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
