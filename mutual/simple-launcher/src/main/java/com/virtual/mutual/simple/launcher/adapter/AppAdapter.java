package com.virtual.mutual.simple.launcher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual.mutual.simple.launcher.AppLoad;
import com.virtual.mutual.simple.launcher.R;
import com.virtual.mutual.simple.launcher.holder.AppHolder;
import com.virtual.mutual.simple.launcher.model.App;

import java.util.LinkedList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppHolder> implements AppLoad.Callback {
    private final Context mContext;
    private final List<App> mApps = new LinkedList<>();
    private final int mWp, mHp;
    private final @Type int mType;

    public AppAdapter(Context context, int type, int wp, int hp) {
        mContext = context;
        mType = type;
        mWp = wp;
        mHp = hp;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void change(List<App> apps) {
        mApps.clear();
        mApps.addAll(apps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppHolder(new FrameLayout(mContext), mType, mWp, mHp);
    }

    @Override
    public void onBindViewHolder(@NonNull AppHolder holder, int position) {
        final App app = mApps.get(position);
        holder.txt.setText(app.name);
        holder.icon.setImageDrawable(app.icon);
        if (app.intent != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(app.intent);
                }
            });
        }
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", app.packageName, null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        if (holder.add != null) {
            if (app.hasWorkplace) {
                holder.add.setText(R.string.app_menu_remove);
                holder.add.setTextColor(0xFFF08080);
            } else {
                holder.add.setText(R.string.app_menu_add);
                holder.add.setTextColor(0xFF6495ED);
            }
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (app.hasWorkplace) {
                        AppLoad.instance().removeWorkspaceApp(mContext, app);
                    } else {
                        AppLoad.instance().addWorkspaceApp(mContext, app);
                    }
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public @interface Type {
        int WORKPLACE = 1;
        int ALL_APPS = 2;
    }
}
