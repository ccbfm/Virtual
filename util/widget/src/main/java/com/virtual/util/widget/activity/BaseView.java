package com.virtual.util.widget.activity;

import android.content.Context;

import androidx.annotation.NonNull;

public interface BaseView<Data extends BasePackData> {

    Context context();

    void presenterCallback(int action, Data data);
}
