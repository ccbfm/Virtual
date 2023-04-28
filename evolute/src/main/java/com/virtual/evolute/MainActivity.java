package com.virtual.evolute;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.virtual.ui.plane.scene.NormalScene;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout content = findViewById(R.id.content);

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = width;
        NormalScene normalScene = new NormalScene(this, width, height);
        normalScene.setBackgroundColor(Color.WHITE);
        //normalScene.invalidate(new Rect());
        FrameLayout.LayoutParams nsLp = new FrameLayout.LayoutParams(width, height);
        nsLp.gravity = Gravity.CENTER;
        content.addView(normalScene, nsLp);
    }
}