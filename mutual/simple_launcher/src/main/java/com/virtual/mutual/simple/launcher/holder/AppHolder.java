package com.virtual.mutual.simple.launcher.holder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual.mutual.simple.launcher.R;
import com.virtual.mutual.simple.launcher.adapter.AppAdapter;

public class AppHolder extends RecyclerView.ViewHolder {
    public ImageView icon;
    public TextView txt, detail, add;

    public AppHolder(@NonNull FrameLayout itemView, int type, int wp, int hp) {
        super(itemView);
        final Context context = itemView.getContext();
        int tp = Math.min(wp, hp);
        int paddingHp = tp / 5;
        int iconHp = (int) (tp * 4.f / 5.f);

        FrameLayout content = new FrameLayout(context);

        this.icon = new ImageView(context);
        this.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.icon.setBackgroundColor(0x8FD3D3D3);
        content.addView(this.icon, new FrameLayout.LayoutParams(iconHp, iconHp));

        int txtHp = iconHp / 3;
        int lmWp = iconHp + paddingHp;
        int txtWp = wp - lmWp;
        this.txt = new TextView(context);
        this.txt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        this.txt.setTextColor(Color.WHITE);
        this.txt.setSingleLine();
        this.txt.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.txt.setShadowLayer(2.0f, 2.0f, 2.0f, Color.BLACK);
        FrameLayout.LayoutParams txtLp = new FrameLayout.LayoutParams(txtWp, txtHp);
        txtLp.leftMargin = lmWp;
        content.addView(this.txt, txtLp);

        int detailHp = iconHp - txtHp;
        this.detail = new TextView(context);
        this.detail.setText(R.string.app_menu_detail);
        this.detail.setGravity(Gravity.CENTER);
        this.detail.setTextColor(Color.WHITE);
        this.detail.setShadowLayer(2.0f, 2.0f, 2.0f, Color.BLACK);

        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, 0x8FD3D3D3);
        border.setCornerRadius((txtHp / 2.f));
        this.detail.setBackground(border);

        if (type == AppAdapter.Type.ALL_APPS) {
            this.add = new TextView(context);
            this.add.setText(R.string.app_menu_add);
            this.add.setGravity(Gravity.CENTER);
            this.add.setTextColor(0xFF6495ED);
            this.add.setShadowLayer(2.0f, 2.0f, 2.0f, Color.BLACK);

            GradientDrawable borderAdd = new GradientDrawable();
            borderAdd.setStroke(1, 0x8FD3D3D3);
            borderAdd.setCornerRadius((txtHp / 2.f));
            this.add.setBackground(borderAdd);

            FrameLayout.LayoutParams addLp = new FrameLayout.LayoutParams(iconHp, detailHp);
            addLp.gravity = Gravity.END;
            addLp.topMargin = txtHp;
            content.addView(this.add, addLp);

            FrameLayout.LayoutParams detailLp = new FrameLayout.LayoutParams(iconHp, detailHp);
            detailLp.gravity = Gravity.END;
            detailLp.topMargin = txtHp;
            detailLp.rightMargin = txtHp + iconHp;
            content.addView(this.detail, detailLp);
        } else {
            FrameLayout.LayoutParams detailLp = new FrameLayout.LayoutParams(iconHp, detailHp);
            detailLp.gravity = Gravity.END;
            detailLp.topMargin = txtHp;
            content.addView(this.detail, detailLp);
        }

        int cTopHp = paddingHp / 2;
        FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(-1, iconHp);
        contentLp.topMargin = cTopHp;
        contentLp.bottomMargin = cTopHp;
        itemView.addView(content, contentLp);
    }
}
