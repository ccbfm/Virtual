package com.virtual.evolute.ui.scene;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.virtual.evolute.ui.IDataChange;
import com.virtual.evolute.ui.scene.layer.ILayer;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("ViewConstructor")
public abstract class BaseScene<Data> extends View implements IDataChange<Data> {

    protected final int mWidth, mHeight;
    protected final List<ILayer> mLayers = new LinkedList<>();

    public BaseScene(@NonNull Context context, int width, int height) {
        super(context);
        mWidth = width;
        mHeight = height;
        init();
        initLayers(mLayers);
    }

    protected void init() {

    }

    protected void destroy() {

    }

    protected void focusChanged(boolean focus) {
        List<ILayer> layers = mLayers;
        if (layers != null) {
            for (ILayer layer : layers) {
                layer.focusChanged(focus);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        List<ILayer> layers = mLayers;
        if (layers != null) {
            for (ILayer layer : layers) {
                layer.attached(this);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        focusChanged(hasWindowFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destroy();
    }

    protected abstract void initLayers(List<ILayer> layers);

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        List<ILayer> layers = mLayers;
        if (layers != null) {
            for (ILayer layer : layers) {
                layer.draw(canvas);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    private ILayer mDownLayer;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            List<ILayer> layers = mLayers;
            if (layers != null) {
                for (ILayer layer : layers) {
                    if (layer.isInner(x, y)) {
                        mDownLayer = layer;
                        layer.onTouchInner(true);
                        break;
                    }
                }
            }
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            if (mDownLayer != null) {
                if (mDownLayer.isInner(x, y)) {
                    mDownLayer.onClick();
                }
                mDownLayer.onTouchInner(false);
                mDownLayer = null;
            }
        }
        return true;
    }

}
