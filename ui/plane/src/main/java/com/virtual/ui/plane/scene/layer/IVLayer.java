package com.virtual.ui.plane.scene.layer;

import android.graphics.Canvas;
import android.view.View;

public interface IVLayer {

    void attached(View view);

    void draw(Canvas canvas);

    boolean isInner(float x, float y);

    void onTouchInner(boolean inner);

    void onClick();
}
