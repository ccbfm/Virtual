package com.virtual.evolute.ui.scene.layer;

import android.graphics.Canvas;
import android.view.View;

public interface ILayer {

    void attached(View view);

    void focusChanged(boolean focus);

    void draw(Canvas canvas);

    boolean isInner(float x, float y);

    void onTouchInner(boolean inner);

    void onClick();
}
