package com.virtual.util.widget.activity;

import android.content.Context;

public interface VBaseView<Data extends VBasePackData> {

    Context context();

    void presenterCallback(int action, Data data);
}
