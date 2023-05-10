package com.virtual.ui.plane.scene.data;

public class ChildLayerData extends LayerData {
    public int active;
    public float angle;

    public boolean isActive() {
        return active > 0;
    }

    public ChildLayerData(String name, String key) {
        super(name, key);
    }

}
