package com.virtual.util.widget.recycler;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;
import java.util.List;

public abstract class VAdapter<T> extends RecyclerView.Adapter<VViewHolder> {

    protected LayoutInflater mLayoutInflater;
    protected final List<T> mData;

    public VAdapter(@NonNull List<T> data) {
        mData = data;
    }

    public void addData(@NonNull T data) {
        mData.add(data);
        notifyItemInserted(getItemCount() - 1);
        needDataSizeChanged(1);
    }

    public void addData(@IntRange(from = 0) int position, @NonNull T data) {
        mData.add(position, data);
        notifyItemInserted(position);
        needDataSizeChanged(1);
    }

    public void addData(@NonNull Collection<T> data) {
        mData.addAll(data);
        int newSize = data.size();
        notifyItemRangeChanged(getItemCount() - newSize, newSize);
        needDataSizeChanged(newSize);
    }

    public void addData(@IntRange(from = 0) int position, @NonNull Collection<T> data) {
        mData.addAll(position, data);
        int newSize = data.size();
        notifyItemRangeChanged(position, newSize);
        needDataSizeChanged(newSize);
    }

    public void setData(@IntRange(from = 0) int index, @NonNull T data) {
        mData.set(index, data);
        notifyItemChanged(index);
    }

    public void replaceData(@NonNull Collection<? extends T> data) {
        mData.clear();
        mData.addAll(data);
        needDataSizeChanged(-1);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void needDataSizeChanged(int size) {
        if (getItemCount() == size) {
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public VViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = itemViewLayoutResId(viewType);
        View view = null;
        if (layoutResId > 0) {
            view = inflateView(layoutResId, parent);
        } else {
            view = itemViewLayout(viewType);
        }
        if (view == null) {
            TextView textV = new TextView(parent.getContext());
            String hintText = "itemView not impl";
            textV.setText(hintText);
            view = textV;
        }
        return new VViewHolder(view);
    }

    protected @LayoutRes int itemViewLayoutResId(int viewType) {
        return 0;
    }

    protected View itemViewLayout(int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VViewHolder holder, int position) {
        bindViewHolder(holder, getData(position));
    }

    protected T getData(int position) {
        return mData.get(position);
    }

    protected abstract void bindViewHolder(@NonNull VViewHolder holder, T data);

    protected View inflateView(@LayoutRes int layoutResId, @NonNull ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
