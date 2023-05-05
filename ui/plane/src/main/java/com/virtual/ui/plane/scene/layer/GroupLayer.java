package com.virtual.ui.plane.scene.layer;


import android.graphics.Canvas;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GroupLayer<T extends Layer<?>, D> extends Layer<D> {
    protected final List<T> mChildLayers = new LinkedList<>();
    protected final Lock mChildLock = new ReentrantLock();
    private Layer<?> mTouchChildLayer;

    public GroupLayer(D layerData) {
        super(layerData);
    }

    @Override
    public void attached(View view) {
        super.attached(view);
        initChildLayers(mChildLayers);
        for (Layer<?> layer : mChildLayers) {
            layer.attached(view);
        }
    }

    protected abstract void initChildLayers(List<T> layers);

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        try {
            mChildLock.lock();
            for (Layer<?> layer : mChildLayers) {
                layer.draw(canvas);
            }
        } finally {
            mChildLock.unlock();
        }

    }

    @Override
    public boolean isInner(float x, float y) {
        if (super.isInner(x, y)) {
            if (mTouchChildLayer == null) {
                try {
                    mChildLock.lock();
                    for (Layer<?> layer : mChildLayers) {
                        if (layer.isInner(x, y)) {
                            mTouchChildLayer = layer;
                            return true;
                        }
                    }
                } finally {
                    mChildLock.unlock();
                }
            } else {
                return mTouchChildLayer.isInner(x, y);
            }
        }
        return false;
    }

    @Override
    public void onTouchInner(boolean inner) {
        if (mTouchChildLayer != null) {
            mTouchChildLayer.onTouchInner(inner);
            if (!inner) {
                mTouchChildLayer = null;
            }
        } else {
            super.onTouchInner(inner);
        }
    }

    @Override
    public void onClick() {
        if (mTouchChildLayer != null) {
            mTouchChildLayer.onClick();
        } else {
            super.onClick();
        }
    }

    public void addLayer(T layer) {
        layer.attached(mView);
        try {
            mChildLock.lock();
            mChildLayers.add(layer);
        } finally {
            mChildLock.unlock();
        }

        postInvalidate();
    }

    public void removeLayer(T layer) {
        if (mTouchChildLayer == layer) {
            mTouchChildLayer = null;
        }
        try {
            mChildLock.lock();
            mChildLayers.remove(layer);
        } finally {
            mChildLock.unlock();
        }

        postInvalidate();
    }
}