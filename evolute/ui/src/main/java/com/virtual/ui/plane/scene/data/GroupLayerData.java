package com.virtual.ui.plane.scene.data;

import androidx.annotation.NonNull;

import java.util.List;

public class GroupLayerData extends LayerData {
    public List<ChildLayerData> childList;

    public GroupLayerData(String name, String key) {
        super(name, key);
    }

    @NonNull
    @Override
    public String toString() {
        return "GroupLayerData{" +
                "childList=" + (childList != null ? childList.size() : "null") +
                ", name='" + name + '\'' +
                ", oneName='" + oneName + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
