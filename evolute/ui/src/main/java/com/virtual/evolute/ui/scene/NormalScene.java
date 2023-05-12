package com.virtual.evolute.ui.scene;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.virtual.evolute.ui.scene.data.ChildLayerData;
import com.virtual.evolute.ui.scene.data.GroupLayerData;
import com.virtual.evolute.ui.scene.data.LayerData;
import com.virtual.evolute.ui.scene.data.NormalSceneData;
import com.virtual.evolute.ui.scene.layer.GroupLayer;
import com.virtual.evolute.ui.scene.layer.ILayer;
import com.virtual.evolute.ui.scene.layer.Layer;
import com.virtual.util.math.MathCircle;
import com.virtual.util.math.MathRandom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ViewConstructor")
public class NormalScene extends BaseScene<NormalSceneData> {

    private Random mChildRandom;
    private MiddleLayer mMiddleLayer;
    private InnerLayer mInnerLayer;
    private CenterLayer mCenterLayer;
    private OuterLayer mOuterLayer;

    public NormalScene(@NonNull Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public void onChange(@NonNull NormalSceneData layerData) {
        if (layerData.middle != null) {
            mMiddleLayer.updateData(layerData.middle);
        }
        if (layerData.inner != null) {
            mInnerLayer.updateData(layerData.inner);
        }
        if (layerData.center != null) {
            mCenterLayer.updateData(layerData.center);
        }
        if (layerData.outer != null) {
            mOuterLayer.updateData(layerData.outer);
        }
    }

    @Override
    protected void init() {
        mChildRandom = MathRandom.get("normal_scene_child_random");
    }

    @Override
    protected void destroy() {

    }


    @Override
    protected void initLayers(List<ILayer> layers) {
        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        float dx = cx / 2.0f;
        float ex = dx / 3.0f;
        float ior = (dx + ex);
        float cor = (dx + ex + ex);

        MiddleLayer middleLayer = new MiddleLayer(mWidth, mHeight, cx, cy, dx, 0);
        mMiddleLayer = middleLayer;
        layers.add(middleLayer);

        InnerLayer innerLayer = new InnerLayer(mWidth, mHeight, cx, cy, ior, dx);
        mInnerLayer = innerLayer;
        layers.add(innerLayer);

        CenterLayer centerLayer = new CenterLayer(mWidth, mHeight, cx, cy, cor, ior);
        mCenterLayer = centerLayer;
        layers.add(centerLayer);

        OuterLayer outerLayer = new OuterLayer(mWidth, mHeight, cx, cy, cx, cor);
        mOuterLayer = outerLayer;
        layers.add(outerLayer);
        layers.add(new BackgroundLayer(mWidth, mHeight));
    }

    private static abstract class NormalLayer<D> extends Layer<D> {
        protected float mCx;
        protected float mCy;
        protected float mOutRadius;
        protected float mInnerRadius;

        public NormalLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height);
            mCx = cx;
            mCy = cy;
            mOutRadius = outRadius;
            mInnerRadius = innerRadius;
        }

        @CallSuper
        public void updateCXY(float cx, float cy) {
            mCx = cx;
            mCy = cy;
        }
    }

    private /*static*/ abstract class NormalGroupLayer
            extends GroupLayer<NormalChildLayer, GroupLayerData> {
        protected float mCx;
        protected float mCy;
        protected float mOutRadius;
        protected float mInnerRadius;
        protected float mIr, mOr;
        private final HashMap<String, NormalChildLayer> mChildLayerMap = new HashMap<>();

        public NormalGroupLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height);
            mCx = cx;
            mCy = cy;
            mOutRadius = outRadius;
            mInnerRadius = innerRadius;

            float ir = ((outRadius - innerRadius) / 2.0f);
            mIr = outRadius - ir;
            mOr = (ir / 5.0f) * 4;
        }

        @Override
        protected void initChildLayers(List<NormalChildLayer> layers) {
            mChildLayerMap.clear();
            if (mData != null && mData.childList != null) {
                for (ChildLayerData child : mData.childList) {
                    NormalChildLayer childLayer = createChildLayer(child);
                    if (childLayer != null) {
                        layers.add(childLayer);
                    }
                }
            }
        }

        protected NormalChildLayer createChildLayer(ChildLayerData child) {
            if (child == null) {
                return null;
            }
            float angle = child.angle;
            if (child.angle == -1) {
                angle = mChildRandom.nextInt(360);
            }
            if (angle != -1) {
                PointF pointF = MathCircle.pointByAngle(mCx, mCy, angle, mIr);
                child.angle = angle;
                NormalChildLayer childLayer = new NormalChildLayer(mWidth, mHeight, pointF.x, pointF.y, mOr, 0);
                childLayer.updateData(child, false);
                mChildLayerMap.put(child.key, childLayer);
                return childLayer;
            }
            return null;
        }

        @Override
        public boolean updateData(GroupLayerData data) {
            //Log.d("NormalGroupLayer", "updateData " + data);
            if (data.childList != null) {
                List<NormalChildLayer> addLayers = new LinkedList<>();
                List<NormalChildLayer> updateLayers = new LinkedList<>();
                for (ChildLayerData child : data.childList) {
                    NormalChildLayer childLayer = mChildLayerMap.remove(child.key);
                    if (childLayer != null) {
                        ChildLayerData oldChildData = childLayer.getData();
                        child.angle = oldChildData.angle;
                        childLayer.updateData(child, false);
                        updateLayers.add(childLayer);
                    } else {
                        childLayer = createChildLayer(child);
                        if (childLayer != null) {
                            addLayers.add(childLayer);
                        }
                    }
                }
                for (NormalChildLayer childLayer : mChildLayerMap.values()) {
                    removeLayer(childLayer);
                }
                mChildLayerMap.clear();
                for (NormalChildLayer childLayer : updateLayers) {
                    mChildLayerMap.put(childLayer.getData().key, childLayer);
                }
                for (NormalChildLayer childLayer : addLayers) {
                    addLayer(childLayer);
                    mChildLayerMap.put(childLayer.getData().key, childLayer);
                }
            }
            return super.updateData(data);
        }
    }

    private static class NormalChildLayer extends NormalLayer<ChildLayerData> {
        public float mTx;
        public float mTy;
        public float mOffsetX;
        public float mOffsetY;
        private final Paint mOPaint, mMaskPaint, mTextPaint;

        public NormalChildLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 1);
            mOPaint.setColor(Color.BLACK);

            mMaskPaint = createPaint();
            mMaskPaint.setColor(Color.parseColor("#10808080"));

            mTextPaint = createPaint();
            mTextPaint.setTextSize(outRadius);
            mTextPaint.setColor(Color.parseColor("#FFC0CB"));
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mOffsetX = (outRadius / 2);
            mOffsetY = ((fontMetrics.bottom + fontMetrics.top) / 2);

            mTx = cx - mOffsetX;
            mTy = cy - mOffsetY;
        }


        @Override
        public void updateCXY(float cx, float cy) {
            super.updateCXY(cx, cy);
            mTx = cx - mOffsetX;
            mTy = cy - mOffsetY;
            refreshRegion(initRegion());
        }

        @Override
        protected Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (mIsInner) {
                canvas.drawCircle(mCx, mCy, mOutRadius, mMaskPaint);
            } else {
                canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
            }
            canvas.drawText(mData.oneName, mTx, mTy, mTextPaint);
        }
    }

    private static class MiddleLayer extends NormalLayer<LayerData> {
        private final Paint mOPaint, mMaskPaint;

        public MiddleLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.YELLOW);

            mMaskPaint = createPaint();
            mMaskPaint.setColor(Color.parseColor("#10808080"));
        }

        @Override
        public Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (mIsInner) {
                canvas.drawCircle(mCx, mCy, mOutRadius, mMaskPaint);
            } else {
                canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
            }
        }
    }

    private /*static*/ class InnerLayer extends NormalGroupLayer {
        private final Paint mOPaint;

        public InnerLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.parseColor("#808080"));
        }

        @Override
        public Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(mCx, mCy, mInnerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private /*static*/ class CenterLayer extends NormalGroupLayer {
        private final Paint mOPaint;
        private Timer mTimer;

        public CenterLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.BLACK);

        }

        @Override
        public void focusChanged(boolean focus) {
            if (focus) {
                scheduleTask();
            } else {
                mTimer.cancel();
                mTimer = null;
            }
        }

        private void scheduleTask() {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Random random = mChildRandom;
                    try {
                        mChildLock.lock();
                        //Log.d("CenterLayer", "mChildLayers " + mChildLayers.size());
                        for (NormalChildLayer child : mChildLayers) {
                            //Log.d("CenterLayer", "mChildLayers child " + child.mCx + " " + child.mCy);
                            ChildLayerData childData = child.getData();
                            if (!childData.isActive()) {
                                continue;
                            }
                            float angle = childData.angle;
                            //Log.d("CenterLayer", "mChildLayers angle " + angle);
                            int act = random.nextInt(3);
                            if (act == 0) {
                                continue;
                            } else {
                                int ra = random.nextInt(10);
                                if (act == 1) {
                                    angle -= ra;
                                } else {
                                    angle += ra;
                                }
                            }
                            PointF pointF = MathCircle.pointByAngle(mCx, mCy, angle, mIr);
                            child.updateCXY(pointF.x, pointF.y);
                            childData.angle = angle;
                        }
                    } finally {
                        mChildLock.unlock();
                    }

                    postInvalidate();
                }
            }, 1000, 1000);
        }

        @Override
        protected Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(mCx, mCy, mInnerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private /*static*/ class OuterLayer extends NormalGroupLayer {
        private final Paint mOPaint;

        public OuterLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.GRAY);
        }

        @Override
        protected Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(mCx, mCy, mInnerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private static class BackgroundLayer extends Layer<LayerData> {
        private final Paint mBgPaint;

        public BackgroundLayer(int width, int height) {
            super(width, height);

            mBgPaint = createPaint();
            setPaintStroke(mBgPaint, 1);
            mBgPaint.setColor(Color.BLUE);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawRect(0, 0, mWidth, mHeight, mBgPaint);
        }
    }
}
