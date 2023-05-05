package com.virtual.ui.plane.scene.data;

public class BodyData {

    public String name;

    public String oneName;

    public String key;

    public BodyData(String name, String key) {
        this.name = name;
        this.oneName = name.substring(0, 1);
        this.key = key;
    }

    @Override
    public String toString() {
        return "BodyData{" +
                "name='" + name + '\'' +
                ", oneName='" + oneName + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
