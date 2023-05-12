package com.virtual.evolute.ui.scene.data;

public class ChildLayerData extends LayerData {
    public int active;
    public float angle = -1;

    public boolean isActive() {
        return active > 0;
    }

    public ChildLayerData(String name, String key) {
        super(name, key);
    }

}
