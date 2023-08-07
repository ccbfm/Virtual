package com.virtual.mutual.simple.launcher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual.mutual.simple.launcher.holder.ShortcutHolder;
import com.virtual.mutual.simple.launcher.model.Shortcut;

import java.util.List;

public class ShortcutAdapter extends RecyclerView.Adapter<ShortcutHolder> {
    private final Context mContext;
    private final List<Shortcut> mShortcuts;
    private final int mWp, mHp;

    public ShortcutAdapter(Context context, List<Shortcut> shortcuts, int wp, int hp) {
        mContext = context;
        mShortcuts = shortcuts;
        mWp = wp;
        mHp = hp;
    }

    @NonNull
    @Override
    public ShortcutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShortcutHolder(new FrameLayout(mContext), mWp, mHp);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutHolder holder, int position) {
        final Shortcut shortcut = mShortcuts.get(position);
        holder.txt.setText(shortcut.name);
        holder.icon.setImageDrawable(shortcut.icon);
        if (shortcut.clickListener != null) {
            holder.itemView.setOnClickListener(shortcut.clickListener);
        } else if (shortcut.intent != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(shortcut.intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mShortcuts.size();
    }
}
