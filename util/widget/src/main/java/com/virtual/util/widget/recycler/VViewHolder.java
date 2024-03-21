package com.virtual.util.widget.recycler;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtual.util.widget.VViewStorage;

public class VViewHolder extends RecyclerView.ViewHolder {

    private final VViewStorage mViewStorage;

    public VViewHolder(@NonNull View itemView) {
        super(itemView);
        mViewStorage = new VViewStorage(itemView);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        return mViewStorage.getView(viewId);
    }
}
