package com.virtual.ui.plane.scene.layer;


import android.graphics.Canvas;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

public abstract class VGroupLayer extends VLayer {
    protected final List<VLayer> mChildLayers = new LinkedList<>();
    private VLayer mTouchChildLayer;

    public VGroupLayer(int width, int height) {
        super(width, height);
    }

    @Override
    public void attached(View view) {
        super.attached(view);
        initChildLayers(mChildLayers);
        for (VLayer layer : mChildLayers) {
            layer.attached(view);
        }
    }

    protected abstract void initChildLayers(List<VLayer> layers);

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (VLayer layer : mChildLayers) {
            layer.draw(canvas);
        }
    }

    @Override
    public boolean isInner(float x, float y) {
        if (super.isInner(x, y)) {
            if (mTouchChildLayer == null) {
                for (VLayer layer : mChildLayers) {
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