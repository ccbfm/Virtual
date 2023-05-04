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

import com.virtual.ui.plane.scene.data.LayerData;
import com.virtual.ui.plane.scene.data.NormalData;
import com.virtual.ui.plane.scene.layer.GroupLayer;
import com.virtual.ui.plane.scene.layer.ILayer;
import com.virtual.ui.plane.scene.layer.Layer;
import com.virtual.util.math.MathCircle;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("ViewConstructor")
public class NormalScene extends BaseScene {

    private Timer mTimer;

    public NormalScene(@NonNull Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void init() {
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

        layers.add(new MiddleLayer(new NormalData.Middle(mWidth, mHeight, cx, cy, dx, 0)));
        layers.add(new InnerLayer(new NormalData.Inner(mWidth, mHeight, cx, cy, ior, dx)));
        layers.add(new CenterLayer(new NormalData.Center(mWidth, mHeight, cx, cy, cor, ior)));
        layers.add(new OuterLayer(new NormalData.Outer(mWidth, mHeight, cx, cy, cx, cor)));
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
            canvas.drawText("赵", layerData.tx, layerData.ty, mTextPaint);
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

    private static class InnerLayer extends NormalGroupLayer<NormalData.NormalChild, NormalData.Inner> {
        private final Paint mOPaint;

        public InnerLayer(NormalData.Inner layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.parseColor("#808080"));
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            Random random = new Random();
            final NormalData.Inner layerData = mLayerData;
            float ir = (layerData.outRadius - layerData.innerRadius) / 2.0f;
            float or = (ir / 5.0f) * 4;
            for (int i = 0; i < 5; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, layerData.outRadius - ir);
                layers.add(new NormalChildLayer(new NormalData.NormalChild(layerData.width, layerData.height, pointF.x, pointF.y, or, 0)));
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

            mIr = (layerData.outRadius - layerData.innerRadius) / 2.0f;
            mOr = (mIr / 5.0f) * 4;
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Random random = new Random();
                    final NormalData.Center layerData = mLayerData;
                    float ir = layerData.outRadius - mIr;
                    for (NormalLayer<NormalData.NormalChild> child : mChildLayers) {
                        NormalData.NormalChild childData = child.getLayerData();

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
                        PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, ir);
                        childData.cx = pointF.x;
                        childData.cy = pointF.y;
                        childData.angle = angle;
                        child.update(childData);
                    }
                    postInvalidate();
                }
            }, 1000, 1000);
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            Random random = new Random();
            final NormalData.Center layerData = mLayerData;
            float ir = layerData.outRadius - mIr;
            for (int i = 0; i < 5; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, ir);
                layers.add(new NormalChildLayer(new NormalData.NormalChild(layerData.width, layerData.height, pointF.x, pointF.y, mOr, 0, angle)));
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

    private static class OuterLayer extends NormalGroupLayer<NormalData.NormalChild, NormalData.Outer> {
        private final Paint mOPaint;

        public OuterLayer(NormalData.Outer layerData) {
            super(layerData);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.GRAY);
        }

        @Override
        protected void initChildLayers(List<NormalLayer<NormalData.NormalChild>> layers) {
            final NormalData.Outer layerData = mLayerData;
            Random random = new Random();
            float ir = (layerData.outRadius - layerData.innerRadius) / 2.0f;
            float or = (ir / 5.0f) * 4;
            for (int i = 0; i < 2; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(layerData.cx, layerData.cy, angle, layerData.outRadius - ir);
                layers.add(new NormalChildLayer(new NormalData.NormalChild(layerData.width, layerData.height, pointF.x, pointF.y, or, 0)));
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
