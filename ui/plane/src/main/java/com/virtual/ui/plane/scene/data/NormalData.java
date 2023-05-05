package com.virtual.ui.plane.scene.data;

import java.util.LinkedList;
import java.util.List;

public class NormalData {

    public Middle middle;
    public Inner inner;
    public Center center;
    public Outer outer;

    public static class Normal extends LayerData {
        public BodyData body;
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

    public static class NormalGroup extends Normal {
        public List<NormalChild> childList = new LinkedList<>();

        public NormalGroup() {
        }

        public NormalGroup(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class NormalChild extends Normal {
        public boolean active = false;
        public float angle = -1f;
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

    public static class Inner extends NormalGroup {

        public Inner() {
        }

        public Inner(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class Center extends NormalGroup {
        public Center() {
        }

        public Center(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }

    public static class Outer extends NormalGroup {
        public Outer() {
        }

        public Outer(int width, int height, float cx, float cy, float outRadius, float innerRadius) {
            super(width, height, cx, cy, outRadius, innerRadius);
        }
    }
}
