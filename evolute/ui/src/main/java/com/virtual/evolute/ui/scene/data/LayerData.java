package com.virtual.evolute.ui.scene.data;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class LayerData {

    public String name = "";
    public String oneName = "";
    public String key = "";

    public LayerData(String name, String key) {
        this.name = name;
        if (!TextUtils.isEmpty(name)) {
            this.oneName = name.substring(0, 1);
        }
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return "BodyData{" +
                "name='" + name + '\'' +
                ", oneName='" + oneName + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
