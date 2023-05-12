package com.virtual.ui.plane;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import com.virtual.ui.plane.feel.BaseFeel;
import com.virtual.ui.plane.feel.NormalFeel;
import com.virtual.ui.plane.feel.data.FeelData;
import com.virtual.ui.plane.feel.data.NormalFeelData;
import com.virtual.ui.plane.scene.BaseScene;
import com.virtual.ui.plane.scene.NormalScene;
import com.virtual.ui.plane.scene.data.ChildLayerData;
import com.virtual.ui.plane.scene.data.GroupLayerData;
import com.virtual.ui.plane.scene.data.NormalSceneData;

import java.util.LinkedList;

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

        final BaseFeel<?, FeelData> feelV = new NormalFeel(context, w, (int) h1_2);
        FrameLayout.LayoutParams fvLp = new FrameLayout.LayoutParams(w, (int) h1_2);
        fvLp.topMargin = (int) h1_4;
        feelV.attached(content, fvLp);
        mFeelV = feelV;

        for (int i = 0; i < 20; i++) {
            FeelData feelData = new NormalFeelData();
            feelData.name = "i=" + i;
            feelV.onChange(feelData);
        }

        final BaseScene<NormalSceneData> sceneV = new NormalScene(context, w, w);
        //sceneV.setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams svLp = new FrameLayout.LayoutParams(w, w);
        svLp.topMargin = (int) (h1_2 + h1_4);
        content.addView(sceneV, svLp);
        mSceneV = sceneV;

        View view = new View(context);
        view.setBackgroundColor(Color.GRAY);
        FrameLayout.LayoutParams vLp = new FrameLayout.LayoutParams(w, (int) h1_4);
        vLp.topMargin = (int) (h1_2 + h1_4 + w);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeelData feelData = new NormalFeelData();
                feelData.name = "i=c";
                feelV.onChange(feelData);
                NormalSceneData sceneData = new NormalSceneData();

                GroupLayerData inner = new GroupLayerData("", "");
                inner.childList = new LinkedList<>();
                inner.childList.add(new ChildLayerData("叶凡", "ye_fan"));
                sceneData.inner = inner;

                GroupLayerData center = new GroupLayerData("", "");
                center.childList = new LinkedList<>();
                ChildLayerData childLayerData = new ChildLayerData("叶凡", "ye_fan");
                childLayerData.active = 1;
                center.childList.add(childLayerData);
                sceneData.center = center;
                sceneV.onChange(sceneData);
            }
        });
        content.addView(view, vLp);
        return content;
    }
}
