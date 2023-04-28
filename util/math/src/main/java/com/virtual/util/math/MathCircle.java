package com.virtual.util.math;

import android.graphics.PointF;

public final class MathCircle {

    public static PointF pointByAngle(float cx, float cy, double angle, float radius) {
        double radian = Math.toRadians(angle);
        double x = cx + Math.cos(radian) * radius;
        double y = cy + Math.sin(radian) * radius;
        return new PointF((float) x, (float) y);
    }
}
