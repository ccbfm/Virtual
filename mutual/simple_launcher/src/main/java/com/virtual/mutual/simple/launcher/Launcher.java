package com.virtual.mutual.simple.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual.mutual.simple.launcher.adapter.ShortcutAdapter;
import com.virtual.mutual.simple.launcher.model.Shortcut;

import java.util.LinkedList;
import java.util.List;

public class Launcher extends Activity {

    private static final String TAG = "Launcher";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        List<Shortcut> shortcuts = new LinkedList<>();
        initShortcut(this, shortcuts);
        int shortcutsSize = shortcuts.size();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int hp = dm.heightPixels;
        int wp = dm.widthPixels;
        int topHp = hp >> 4;
        hp = hp - topHp;
        int sHp = 0;
        FrameLayout frameLayout = new FrameLayout(this);
        TextView topTV = new TextView(this);
        topTV.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        frameLayout.addView(topTV, new FrameLayout.LayoutParams(-1, topHp));

        if (shortcutsSize > 0) {
            int rows = Math.min((shortcutsSize / 4) + (shortcutsSize % 4 == 0 ? 0 : 1), 3);
            int siHp = hp / 9;
            sHp = siHp * rows;

            RecyclerView shortcutsRV = new RecyclerView(this);
            shortcutsRV.setBackgroundColor(Color.YELLOW);
            shortcutsRV.setLayoutManager(new GridLayoutManager(this, 4));
            shortcutsRV.setAdapter(new ShortcutAdapter(this, shortcuts, siHp));
            FrameLayout.LayoutParams sLp = new FrameLayout.LayoutParams(-1, sHp);
            sLp.topMargin = topHp;
            frameLayout.addView(shortcutsRV, sLp);
        }

        setContentView(frameLayout);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_HOME
                || keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initShortcut(Context context, List<Shortcut> shortcuts) {
        for (int i = 0; i < 4; i++) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage("com.android.settings");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.baseline_settings_24);
            shortcuts.add(new Shortcut("设置", drawable, intent));
        }
    }
}
