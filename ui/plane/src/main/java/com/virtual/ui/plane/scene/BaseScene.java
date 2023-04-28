package com.virtual.ui.plane.scene;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.virtual.ui.plane.scene.layer.IVLayer;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("ViewConstructor")
public abstract class BaseScene extends View {

    protected final int mWidth, mHeight;
    protected final List<IVLayer> mLayers = new LinkedList<>();

    public BaseScene(@NonNull Context context, int width, int height) {
        super(context);
        mWidth = width;
        mHeight = height;
        initLayers(mLayers);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        List<IVLayer> layers = mLayers;
        if (layers != null) {
            for (IVLayer layer : layers) {
                layer.attached(this);
            }
        }
    }

    protected abstract void initLayers(List<IVLayer> layers);

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        List<IVLayer> layers = mLayers;
        if (layers != null) {
            for (IVLayer layer : layers) {
                layer.draw(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    private IVLayer mDownLayer;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            List<IVLayer> layers = mLayers;
            if (layers != null) {
                for (IVLayer layer : layers) {
                    if (layer.isInner(x, y)) {
                        mDownLayer = layer;
                        layer.onTouchInner(true);
                        break;
                    }
                }
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (mDownLayer != null && mDownLayer.isInner(x, y)) {
                mDownLayer.onClick();
                mDownLayer.onTouchInner(false);
            }
            mDownLayer = null;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mDownLayer != null) {
                mDownLayer.onTouchInner(mDownLayer.isInner(x, y));
            }
        }
        return true;
    }

}
