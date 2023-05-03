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

import com.virtual.ui.plane.scene.layer.IVLayer;
import com.virtual.ui.plane.scene.layer.VGroupLayer;
import com.virtual.ui.plane.scene.layer.VLayer;
import com.virtual.util.math.MathCircle;

import java.util.List;
import java.util.Random;

@SuppressLint("ViewConstructor")
public class NormalScene extends BaseScene {

    public NormalScene(@NonNull Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    protected void initLayers(List<IVLayer> layers) {
        float cx = mWidth / 2.0f;
        float cy = mHeight / 2.0f;
        float dx = cx / 2.0f;
        float ex = dx / 3.0f;
        float ior = (dx + ex);
        float cor = (dx + ex + ex);

        layers.add(new MiddleLayer(mWidth, mHeight, cx, cy, dx, 0));
        layers.add(new InnerLayer(mWidth, mHeight, cx, cy, ior, dx));
        layers.add(new CenterLayer(mWidth, mHeight, cx, cy, cor, ior));
        layers.add(new OuterLayer(mWidth, mHeight, cx, cy, cx, cor));
        layers.add(new BackgroundLayer(mWidth, mHeight));
    }

    private static abstract class NormalLayer extends VLayer {
        protected final float mCx, mCy, mOutRadius, mInnerRadius;

        public NormalLayer(int width, int height,
                           float cx, float cy,
                           float outRadius, float innerRadius) {
            super(width, height);
            mCx = cx;
            mCy = cy;
            mOutRadius = outRadius;
            mInnerRadius = innerRadius;
        }
    }

    private static abstract class NormalGroupLayer extends VGroupLayer {
        protected final float mCx, mCy, mOutRadius, mInnerRadius;

        public NormalGroupLayer(int width, int height,
                                float cx, float cy,
                                float outRadius, float innerRadius) {
            super(width, height);
            mCx = cx;
            mCy = cy;
            mOutRadius = outRadius;
            mInnerRadius = innerRadius;
        }
    }

    private static class NormalChildLayer extends NormalLayer {
        private final Paint mOPaint, mMaskPaint, mTextPaint;
        private final float mTx, mTy;

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
            mTy = cy - ((fontMetrics.bottom + fontMetrics.top) / 2);
            mTx = cx - (outRadius / 2);
        }

        @Override
        protected Region initRegion() {
            Region region = new Region();
            Path circlePath = new Path();
            circlePath.addCircle(mCx, mCy, mOutRadius, Path.Direction.CW);
            RectF rectF = new RectF();
            circlePath.computeBounds(rectF, true);
            //Log.d("NormalChildLayer", "initRegion rectF: " + rectF);
            boolean flag = region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            //Log.d("NormalChildLayer", "initRegion flag: " + flag);
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (mIsInner) {
                canvas.drawCircle(mCx, mCy, mOutRadius, mMaskPaint);
            } else {
                canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
            }
            canvas.drawText("陌", mTx, mTy, mTextPaint);
        }
    }

    private static class MiddleLayer extends NormalLayer {
        private final Paint mOPaint, mMaskPaint;

        public MiddleLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.parseColor("#808080"));

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
            //Log.d("MiddleLayer", "initRegion rectF: " + rectF);
            boolean flag = region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            //Log.d("MiddleLayer", "initRegion flag: " + flag);
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

    private static class InnerLayer extends NormalGroupLayer {
        private final Paint mOPaint;

        public InnerLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.YELLOW);
        }

        @Override
        protected void initChildLayers(List<VLayer> layers) {
            Random random = new Random();
            float ir = (mOutRadius - mInnerRadius) / 2.0f;
            float or = (ir / 5.0f) * 4;
            for (int i = 0; i < 20; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(mCx, mCy, angle, mOutRadius - ir);
                layers.add(new NormalChildLayer(mWidth, mHeight, pointF.x, pointF.y, or, 0));
            }
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
            //Log.d("InnerLayer", "initRegion rectF: " + rectF);
            boolean flag = region.setPath(circlePath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
            //Log.d("InnerLayer", "initRegion flag: " + flag);
            return region;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private static class CenterLayer extends NormalGroupLayer {
        private final Paint mOPaint;

        public CenterLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.BLACK);
        }

        @Override
        protected void initChildLayers(List<VLayer> layers) {
            Random random = new Random();
            float ir = (mOutRadius - mInnerRadius) / 2.0f;
            float or = (ir / 5.0f) * 4;
            for (int i = 0; i < 20; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(mCx, mCy, angle, mOutRadius - ir);
                layers.add(new NormalChildLayer(mWidth, mHeight, pointF.x, pointF.y, or, 0));
            }
        }

        @Override
        protected Region initRegion() {
            return super.initRegion();
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private static class OuterLayer extends NormalGroupLayer {
        private final Paint mOPaint;

        public OuterLayer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);

            mOPaint = createPaint();
            setPaintStroke(mOPaint, 2);
            mOPaint.setColor(Color.GRAY);
        }

        @Override
        protected void initChildLayers(List<VLayer> layers) {
            Random random = new Random();
            float ir = (mOutRadius - mInnerRadius) / 2.0f;
            float or = (ir / 5.0f) * 4;
            for (int i = 0; i < 20; i++) {
                int angle = random.nextInt(360);
                PointF pointF = MathCircle.pointByAngle(mCx, mCy, angle, mOutRadius - ir);
                layers.add(new NormalChildLayer(mWidth, mHeight, pointF.x, pointF.y, or, 0));
            }
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawCircle(mCx, mCy, mOutRadius, mOPaint);
        }

    }

    private static class BackgroundLayer extends VLayer {
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
