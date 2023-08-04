package com.virtual.mutual.simple.launcher.holder;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShortcutHolder extends RecyclerView.ViewHolder {
    public TextView content;
    public ShortcutHolder(@NonNull FrameLayout itemView, int hp) {
        super(itemView);
        final Context context = itemView.getContext();
        this.content = new TextView(context);
        this.content.setGravity(Gravity.CENTER);
        itemView.addView(this.content, new FrameLayout.LayoutParams(-1, hp));
    }
}
