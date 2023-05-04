package com.virtual.ui.plane.scene.layer;


import android.graphics.Canvas;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public abstract class GroupLayer<T extends Layer<?>, D> extends Layer<D> {
    protected final List<T> mChildLayers = new LinkedList<>();
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
        for (Layer<?> layer : mChildLayers) {
            layer.draw(canvas);
        }
    }

    @Override
    public boolean isInner(float x, float y) {
        if (super.isInner(x, y)) {
            if (mTouchChildLayer == null) {
                for (Layer<?> layer : mChildLayers) {
                    if (layer.isInner(x, y)) {
                        mTouchChildLayer = layer;
                        return true;
                    }
                }
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
}