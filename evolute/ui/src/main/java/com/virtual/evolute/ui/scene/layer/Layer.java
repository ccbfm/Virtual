package com.virtual.evolute.ui.scene.layer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.util.Log;
import android.view.View;

import androidx.annotation.CallSuper;

public abstract class Layer<D> implements ILayer {

    protected int mWidth;
    protected int mHeight;

    protected D mData;
    private Region mRegion;
    protected View mView;
    protected boolean mIsInner = false;

    private OnClickListener mClickListener;

    public Layer(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @CallSuper
    public boolean updateData(D data) {
        return updateData(data, true);
    }

    @CallSuper
    public boolean updateData(D data, boolean invalidate) {
        mData = data;
        if (invalidate) {
            invalidate();
        }
        return true;
    }

    public D getData() {
        return mData;
    }

    @Override
    public void attached(View view) {
        mView = view;
        mRegion = initRegion();
    }

    @Override
    public void focusChanged(boolean focus) {

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
        if (mClickListener != null) {
            mClickListener.onClick(this);
        }
    }

    public void setClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
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

    public interface OnClickListener {
        void onClick(Layer<?> layer);
    }
}
