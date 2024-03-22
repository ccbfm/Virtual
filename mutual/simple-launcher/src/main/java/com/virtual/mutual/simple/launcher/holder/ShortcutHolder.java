package com.virtual.mutual.simple.launcher.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShortcutHolder extends RecyclerView.ViewHolder {
    public TextView txt;
    public ImageView icon;

    public ShortcutHolder(@NonNull FrameLayout itemView, int wp, int hp) {
        super(itemView);
        final Context context = itemView.getContext();
        int tp = Math.min(wp, hp);
        int txtHp = tp / 5;
        int iconHp = (int) (tp * 3.f / 5.f);

        LinearLayout content = new LinearLayout(context);
        content.setGravity(Gravity.CENTER);
        content.setOrientation(LinearLayout.VERTICAL);

        this.icon = new ImageView(context);
        this.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.icon.setBackgroundColor(0x8FD3D3D3);
        content.addView(this.icon, new LinearLayout.LayoutParams(iconHp, iconHp));

        this.txt = new TextView(context);
        this.txt.setGravity(Gravity.CENTER);
        this.txt.setTextColor(Color.WHITE);
        this.txt.setSingleLine();
        this.txt.setEms(6);
        this.txt.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.txt.setShadowLayer(2.0f, 2.0f, 2.0f, Color.BLACK);
        content.addView(this.txt, new LinearLayout.LayoutParams(-1, txtHp));

        itemView.addView(content, new FrameLayout.LayoutParams(-1, hp));
    }
}
