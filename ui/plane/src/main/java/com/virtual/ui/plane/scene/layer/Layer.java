package com.virtual.ui.plane.scene.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;

public abstract class Layer<D> implements ILayer {
    protected D mLayerData;
    private Region mRegion;
    private View mView;
    protected boolean mIsInner = false;

    public Layer(D layerData) {
        mLayerData = layerData;
    }

    @CallSuper
    public void update(D layerData) {
        mLayerData = layerData;
    }

    public D getLayerData() {
        return mLayerData;
    }

    @Override
    public void attached(View view) {
        mView = view;
        mRegion = initRegion();
    }

    @Override
    public void draw(Canvas canvas) {
        onDraw(canvas);
    }

    public abstract void onDraw(Canvas canvas);

    @Override
    public boolean isInner(float x, float y) {
        if (mRegion != null) {
            return mRegion.contains((int) x, (int) y);
        }
        return false;
    }

    protected Region initRegion() {
        return null;
    }

    protected void refreshRegion(Region region) {
        mRegion = region;
    }

    @Override
    public void onTouchInner(boolean inner) {
        if (mIsInner != inner) {
            mIsInner = inner;
            mView.invalidate();
        }
    }

    public void invalidate() {
        mView.invalidate();
    }

    public void postInvalidate() {
        mView.postInvalidate();
    }

    @Override
    public void onClick() {
        Log.d("BaseLayer", "onClick: " + this);
    }

    protected Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    protected void setPaintStroke(Paint paint, float width) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
    }
}
