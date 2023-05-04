package com.virtual.ui.plane.scene.data;

public class NormalData {

    public Middle middle;
    public Inner inner;
    public Center center;
    public Outer outer;

    public static class Normal extends LayerData {
        public float cx;
        public float cy;
        public float outRadius;
        public float innerRadius;

        public Normal() {
        }

        public Normal(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height);
            this.cx = cx;
            this.cy = cy;
            this.outRadius = outRadius;
            this.innerRadius = innerRadius;
        }
    }

    public static class NormalChild extends Normal {
        public String text;
        public float angle;
        public float tx;
        public float ty;
        public float offsetX;
        public float offsetY;

        public NormalChild() {
        }

        public NormalChild(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }

        public NormalChild(int width, int height, float cx, float cy, float outRadius, float innerRadius, float angle) {
            super(width, height, cx, cy, outRadius, innerRadius);
            this.angle = angle;
        }
    }


    public static class Middle extends Normal {
        public Middle() {
        }

        public Middle(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class Inner extends Normal {
        public Inner() {
        }

        public Inner(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class Center extends Normal {
        public Center() {
        }

        public Center(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class Outer extends Normal {
        public Outer() {
        }

        public Outer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }
}
