package com.virtual.mutual.simple.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.virtual.mutual.simple.launcher.adapter.AppAdapter;
import com.virtual.mutual.simple.launcher.adapter.ShortcutAdapter;
import com.virtual.mutual.simple.launcher.anim.AllAppsAnim;
import com.virtual.mutual.simple.launcher.model.Shortcut;

import java.util.LinkedList;
import java.util.List;

public class Launcher extends Activity {

    private static final String TAG = "Launcher";
    private AllAppsAnim mAllAppsAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        List<Shortcut> shortcuts = new LinkedList<>();
        initShortcut(this, shortcuts);
        int shortcutsSize = shortcuts.size();

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        int width = dm.widthPixels;

        int hp = height;
        int topHp = hp >> 4;
        hp = hp - topHp;
        int sHp = 0;
        FrameLayout content = new FrameLayout(this);
        FrameLayout workspace = new FrameLayout(this);


        TextView topTV = new TextView(this);
        //topTV.setBackgroundColor(Color.WHITE);
        topTV.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        workspace.addView(topTV, new FrameLayout.LayoutParams(-1, topHp));

        int siHp = hp / 9;
        int appHp = hp;
        int topAppHp = topHp;
        if (shortcutsSize > 0) {
            int rows = Math.min((shortcutsSize / 4) + (shortcutsSize % 4 == 0 ? 0 : 1), 3);
            sHp = siHp * rows;
            int siWp = width >> 2;

            RecyclerView shortcutsRV = new RecyclerView(this);
            //shortcutsRV.setBackgroundColor(Color.RED);
            shortcutsRV.setLayoutManager(new GridLayoutManager(this, 4));
            shortcutsRV.setAdapter(new ShortcutAdapter(this, shortcuts, siWp, siHp));
            FrameLayout.LayoutParams sLp = new FrameLayout.LayoutParams(-1, sHp);
            sLp.topMargin = topHp;
            workspace.addView(shortcutsRV, sLp);
            appHp = hp - sHp;
            topAppHp = topHp + sHp;
        }
        int seWp = width >> 4;
        int appWp = width - seWp - seWp;

        RecyclerView appRV = new RecyclerView(this);
        //appRV.setBackgroundColor(Color.BLUE);
        appRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        final AppAdapter workspaceAdapter = new AppAdapter(this, AppAdapter.Type.WORKPLACE, appWp, siHp);
        AppLoad.instance().setWorkspaceCallback(workspaceAdapter);
        appRV.setAdapter(workspaceAdapter);
        FrameLayout.LayoutParams appLp = new FrameLayout.LayoutParams(appWp, appHp);
        appLp.topMargin = topAppHp;
        appLp.leftMargin = seWp;
        appLp.rightMargin = seWp;
        workspace.addView(appRV, appLp);

        //悬浮按钮
        FloatingActionButton addAppsBtn = new FloatingActionButton(this);

        ColorStateList colorStateList = ColorStateList.valueOf(0x8FD3D3D3);
        addAppsBtn.setBackgroundTintMode(PorterDuff.Mode.SRC_ATOP);
        addAppsBtn.setBackgroundTintList(colorStateList);
        addAppsBtn.setAlpha(0.5f);

        addAppsBtn.setImageResource(R.drawable.add_circle_outline_24);
        addAppsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllAppsAnim.showAllApps();
            }
        });

        FrameLayout.LayoutParams addAppsBtnLp = new FrameLayout.LayoutParams(-2, -2);
        addAppsBtnLp.gravity = Gravity.BOTTOM | Gravity.END;
        addAppsBtnLp.bottomMargin = seWp;
        addAppsBtnLp.rightMargin = seWp;
        workspace.addView(addAppsBtn, addAppsBtnLp);

        content.addView(workspace);

        FrameLayout allApps = new FrameLayout(this);
        FrameLayout searchContent = new FrameLayout(this);
        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, 0x8FD3D3D3);
        border.setCornerRadius((1));
        searchContent.setBackground(border);

        EditText search = new EditText(this);
        search.setBackgroundColor(0);
        search.setHint(R.string.search_app_hint);
        search.setHintTextColor(0x8FD3D3D3);
        search.setTextColor(Color.WHITE);
        search.setSingleLine();
        search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        FrameLayout.LayoutParams searchLp = new FrameLayout.LayoutParams(-1, topHp);
        searchLp.leftMargin = seWp + seWp;
        searchLp.rightMargin = seWp + seWp;
        searchContent.addView(search, searchLp);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                Log.d(TAG, "afterTextChanged " + keyword);
                AppLoad.instance().searchApps(keyword);
            }
        });

        ImageView searchIcon = new ImageView(this);
        searchIcon.setBackgroundResource(R.drawable.search_24);
        FrameLayout.LayoutParams searchIconLp = new FrameLayout.LayoutParams(seWp, seWp);
        searchIconLp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        searchIconLp.leftMargin = seWp;
        searchContent.addView(searchIcon, searchIconLp);

        ImageButton cancelIcon = new ImageButton(this);
        cancelIcon.setBackgroundResource(R.drawable.cancel_24);
        FrameLayout.LayoutParams cancelIconLp = new FrameLayout.LayoutParams(seWp, seWp);
        cancelIconLp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        cancelIconLp.rightMargin = seWp;
        searchContent.addView(cancelIcon, cancelIconLp);
        cancelIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        FrameLayout.LayoutParams searchContentLp = new FrameLayout.LayoutParams(-1, topHp);
        allApps.addView(searchContent, searchContentLp);


        RecyclerView allAppRV = new RecyclerView(this);
        allAppRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        AppAdapter allAppsAdapter = new AppAdapter(this, AppAdapter.Type.ALL_APPS, appWp, siHp);
        AppLoad.instance().setAllAppsCallback(allAppsAdapter);
        allAppRV.setAdapter(allAppsAdapter);
        FrameLayout.LayoutParams allAppLp = new FrameLayout.LayoutParams(appWp, -1);
        allAppLp.topMargin = topHp;
        allAppLp.leftMargin = seWp;
        allAppLp.rightMargin = seWp;
        allApps.addView(allAppRV, allAppLp);

        content.addView(allApps);
        allApps.setVisibility(View.GONE);

        mAllAppsAnim = new AllAppsAnim(addAppsBtn, workspace, allApps);
        setContentView(content);

        AppLoad.instance().load(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Log.d(TAG, "onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mAllAppsAnim.hideAllApps();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void initShortcut(Context context, List<Shortcut> shortcuts) {

    }

}
