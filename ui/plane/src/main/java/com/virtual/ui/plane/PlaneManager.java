package com.virtual.ui.plane;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import com.virtual.ui.plane.feel.BaseFeel;
import com.virtual.ui.plane.feel.NormalFeel;
import com.virtual.ui.plane.scene.BaseScene;
import com.virtual.ui.plane.scene.NormalScene;

public class PlaneManager {

    private PlaneManager() {
    }

    private static final class Singleton {
        private static final PlaneManager INSTANCE = new PlaneManager();
    }

    public static PlaneManager instance() {
        return Singleton.INSTANCE;
    }


    private BaseFeel<?, ?> mFeelV;
    private BaseScene<?> mSceneV;

    public View contentView(Context context, int w, int h) {
        int h1 = h - w;
        float h1_2 = h1 / 2f;
        float h1_4 = h1 / 4f;
        FrameLayout content = new FrameLayout(context);

        BaseFeel<?, Object> feelV = new NormalFeel(context, w, (int) h1_2);
        FrameLayout.LayoutParams fvLp = new FrameLayout.LayoutParams(w, (int) h1_2);
        fvLp.topMargin = (int) h1_4;
        feelV.attached(content, fvLp);
        mFeelV = feelV;

        feelV.onChange(new Object());

        BaseScene<?> sceneV = new NormalScene(context, w, w);
        sceneV.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams svLp = new FrameLayout.LayoutParams(w, w);
        svLp.topMargin = (int) (h1_2 + h1_4);
        content.addView(sceneV, svLp);
        mSceneV = sceneV;
        return content;
    }
}
