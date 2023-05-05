package com.virtual.ui.plane.feel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.virtual.ui.plane.IDataChange;

@SuppressLint("ViewConstructor")
public abstract class BaseFeel<T extends View, Data> implements IDataChange<Data> {
    protected final Context mContext;
    protected final int mWidth, mHeight;
    protected final T mFeelView;

    public BaseFeel(@NonNull Context context, int width, int height) {
        mContext = context;
        mWidth = width;
        mHeight = height;
        mFeelView = createFeel(context);
    }

    protected abstract T createFeel(@NonNull Context context);

    public <V extends ViewGroup, LP extends ViewGroup.LayoutParams> void attached(V parent, LP lp) {
        parent.addView(mFeelView, lp);
    }
}
