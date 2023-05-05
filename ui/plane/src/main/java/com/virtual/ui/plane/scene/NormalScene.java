package com.virtual.ui.plane.scene;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;

import androidx.annotation.NonNull;

import com.virtual.ui.plane.scene.data.BodyData;
import com.virtual.ui.plane.scene.data.LayerData;
import com.virtual.ui.plane.scene.data.NormalData;
import com.virtual.ui.plane.scene.layer.GroupLayer;
import com.virtual.ui.plane.scene.layer.ILayer;
import com.virtual.ui.plane.scene.layer.Layer;
import com.virtual.util.math.MathCircle;
import com.virtual.util.math.MathRandom;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ViewConstructor")
public class NormalScene extends BaseScene<Object> {

    private NormalData mNormalData;
    private Random mChildRandom;
    private Timer mTimer;

    public NormalScene(@NonNull Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public void onChange(Object o) {
        
    }

    @Override
    protected void init() {
        mNormalData = new NormalData();
        mChildRandom = MathRandom.get("normal_scene_child_random");
        mTimer = new Timer();
    }

    @Override
    protected void destroy() {
        mTimer.cancel();
    }

    @Override
    protected void initLayers(List<ILayer> layers) {
        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        float dx = cx / 2.0f;
        float ex = dx / 3.0f;
        float ior = (dx + ex);
        float cor = (dx + ex + ex);

        NormalData.Middle middle = new NormalData.Middle(mWidth, mHeight, cx, cy, dx, 0);
        mNormalData.middle = middle;
        layers.add(new MiddleLayer(middle));

        NormalData.Inner inner = new NormalData.Inner(mWidth, mHeight, cx, cy, ior, dx);
        NormalData.NormalChild innerC1 = new NormalData.NormalChild();
        innerC1.angle = 30f;
        innerC1.body = new BodyData("石头", "shi_tou");
        inner.childList.add(innerC1);
        mNormalData.inner = inner;
        layers.add(new InnerLayer(inner));

        NormalData.Center center = new NormalData.Center(mWidth, mHeight, cx, cy, cor, ior);

        NormalData.NormalChild centerC1 = new NormalData.NormalChild();
        centerC1.active = true;
        centerC1.body = new BodyData("叶凡", "ye_fan");
        center.childList.add(centerC1);

        mNormalData.center = center;
        layers.add(new CenterLayer(center));

        NormalData.Outer outer = new NormalData.Outer(mWidth, mHeight, cx, cy, cx, cor);
        mNormalData.outer = outer;
        layers.add(new OuterLayer(outer));


        layers.add(new BackgroundLayer(new LayerData(mWidth, mHeight)));
    }

    private static abstract class NormalLayer<D extends NormalData.Normal> extends Layer<D> {
        public NormalLayer(D layerData) {
            super(layerData);
        }

    }

    private static abstract class NormalGroupLayer<T extends NormalData.Normal, D extends NormalData.Normal>
            extends GroupLayer<NormalLayer<T>, D> {
        public NormalGroupLayer(D layerData) {
            super(layerData);
        }
    }

    private static class NormalChildLayer extends NormalLayer<NormalData.NormalChild> {
        private final Paint mOPaint, mMaskPaint, mTextPaint;

        public NormalChildLayer(NormalData.NormalChild layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 1);
            mOPaint.setColor(Color.BLACK);

            mMaskPaint = createPaint();
            mMaskPaint.setColor(Color.parseColor("#10808080"));

            mTextPaint = createPaint();
            mTextPaint.setTextSize(layerData.outRadius);
            mTextPaint.setColor(Color.parseColor("#FFC0CB"));
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            layerData.offsetX = (layerData.outRadius / 2);
            layerData.offsetY = ((fontMetrics.bottom + fontMetrics.top) / 2);


            layerData.tx = layerData.cx - layerData.offsetX;
            layerData.ty = layerData.cy - layerData.offsetY;
        }

        @Override
        public void update(NormalData.NormalChild layerData) {
            layerData.tx = layerData.cx - layerData.offsetX;
            layerData.ty = layerData.cy - layerData.offsetY;
            super.update(layerData);
            refreshRegion(initRegion());
        }

        @Override
        protected Region initRegion() {
            final NormalData.NormalChild layerData = mLayerData;
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(layerData.cx, layerData.cy, layerData.outRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final NormalData.NormalChild layerData = mLayerData;
            if (mIsInner) {
                canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mMaskPaint);
            } else {
                canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mOPaint);
            }
            canvas.drawText(layerData.body.oneName, layerData.tx, layerData.ty, mTextPaint);
        }
    }

    private static class MiddleLayer extends NormalLayer<NormalData.Middle> {
        private final Paint mOPaint, mMaskPaint;

        public MiddleLayer(NormalData.Middle layerData) {
            super(layerData);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.YELLOW);

            mMaskPaint = createPaint();
            mMaskPaint.setColor(Color.parseColor("#10808080"));
        }

        @Override
        public Region initRegion() {
            final NormalData.Middle layerData = mLayerData;
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(layerData.cx, layerData.cy, layerData.outRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final NormalData.Middle layerData = mLayerData;
            if (mIsInner) {
                canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mMaskPaint);
            } else {
                canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mOPaint);
            }
        }
    }

    private /*static*/ class InnerLayer extends NormalGroupLayer<NormalData.NormalChild, NormalData.Inner> {
        private final Paint mOPaint;
        private final float mIr, mOr;

        public InnerLayer(NormalData.Inner layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.parseColor("#808080"));

            float ir = ((layerData.outRadius - layerData.innerRadius) / 2.0f);
            mIr = layerData.outRadius - ir;
            mOr = (ir / 5.0f) * 4;
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            final NormalData.Inner layerData = mLayerData;
            for (NormalData.NormalChild child : layerData.childList) {
                float angle = child.angle;
                if (child.angle == -1) {
                    angle = mChildRandom.nextInt(360);
                }
                if (angle != -1) {
                    PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, mIr);
                    child.angle = angle;
                    child.width = layerData.width;
                    child.height = layerData.height;
                    child.cx = pointF.x;
                    child.cy = pointF.y;
                    child.outRadius = mOr;
                    child.innerRadius = 0;
                    layers.add(new NormalChildLayer(child));
                }
            }
        }

        @Override
        public Region initRegion() {
            final NormalData.Inner layerData = mLayerData;
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(layerData.cx, layerData.cy, layerData.outRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(layerData.cx, layerData.cy, layerData.innerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final NormalData.Inner layerData = mLayerData;
            canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mOPaint);
        }

    }

    private /*static*/ class CenterLayer extends NormalGroupLayer<NormalData.NormalChild, NormalData.Center> {
        private final Paint mOPaint;
        private final float mIr, mOr;

        public CenterLayer(NormalData.Center layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.BLACK);

            float ir = ((layerData.outRadius - layerData.innerRadius) / 2.0f);
            mIr = layerData.outRadius - ir;
            mOr = (ir / 5.0f) * 4;
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Random random = mChildRandom;
                    final NormalData.Center layerData = mLayerData;
                    try {
                        mChildLock.lock();

                        for (NormalLayer<NormalData.NormalChild> child : mChildLayers) {
                            NormalData.NormalChild childData = child.getLayerData();
                            if (!childData.active) {
                                continue;
                            }
                            float angle = childData.angle;
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
                            PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, mIr);
                            childData.cx = pointF.x;
                            childData.cy = pointF.y;
                            childData.angle = angle;
                            child.update(childData);
                        }
                    } finally {
                        mChildLock.unlock();
                    }

                    postInvalidate();
                }
            }, 1000, 1000);
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            final NormalData.Center layerData = mLayerData;
            for (NormalData.NormalChild child : layerData.childList) {
                float angle = child.angle;
                if (child.angle == -1) {
                    angle = mChildRandom.nextInt(360);
                }
                if (angle != -1) {
                    PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, mIr);
                    child.angle = angle;
                    child.width = layerData.width;
                    child.height = layerData.height;
                    child.cx = pointF.x;
                    child.cy = pointF.y;
                    child.outRadius = mOr;
                    child.innerRadius = 0;
                    layers.add(new NormalChildLayer(child));
                }
            }
        }

        @Override
        protected Region initRegion() {
            final NormalData.Center layerData = mLayerData;
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(layerData.cx, layerData.cy, layerData.outRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(layerData.cx, layerData.cy, layerData.innerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final NormalData.Center layerData = mLayerData;
            canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mOPaint);
        }

    }

    private /*static*/ class OuterLayer extends NormalGroupLayer<NormalData.NormalChild, NormalData.Outer> {
        private final Paint mOPaint;
        private final float mIr, mOr;

        public OuterLayer(NormalData.Outer layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.GRAY);

            float ir = ((layerData.outRadius - layerData.innerRadius) / 2.0f);
            mIr = layerData.outRadius - ir;
            mOr = (ir / 5.0f) * 4;
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            final NormalData.Outer layerData = mLayerData;
            for (NormalData.NormalChild child : layerData.childList) {
                float angle = child.angle;
                if (child.angle == -1) {
                    angle = mChildRandom.nextInt(360);
                }
                if (angle != -1) {
                    PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, mIr);
                    child.angle = angle;
                    child.width = layerData.width;
                    child.height = layerData.height;
                    child.cx = pointF.x;
                    child.cy = pointF.y;
                    child.outRadius = mOr;
                    child.innerRadius = 0;
                    layers.add(new NormalChildLayer(child));
                }
            }
        }

        @Override
        protected Region initRegion() {
            final NormalData.Outer layerData = mLayerData;
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(layerData.cx, layerData.cy, layerData.outRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);

            Path innerPath = new Path();
            innerPath.addCircle(layerData.cx, layerData.cy, layerData.innerRadius, Path.Direction.CW);

            circlePath.op(innerPath, Path.Op.DIFFERENCE);
            region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final NormalData.Outer layerData = mLayerData;
            canvas.drawCircle(layerData.cx, layerData.cy, layerData.outRadius, mOPaint);
        }

    }

    private static class BackgroundLayer extends Layer<LayerData> {
        private final Paint mBgPaint;

        public BackgroundLayer(LayerData layerData) {
            super(layerData);
            mBgPaint = createPaint();
            setPaintStroke(mBgPaint, 1);
            mBgPaint.setColor(Color.BLUE);
        }

        @Override
        public void onDraw(Canvas canvas) {
            final LayerData layerData = mLayerData;
            canvas.drawRect(0, 0, layerData.width, layerData.height, mBgPaint);
        }
    }
}
