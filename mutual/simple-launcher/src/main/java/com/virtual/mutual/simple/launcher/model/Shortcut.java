package com.virtual.mutual.simple.launcher.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Shortcut {

    public String name;
    public Drawable icon;
    public View.OnClickListener clickListener;
    public Intent intent;

    public Shortcut(String name, Drawable icon, View.OnClickListener clickListener) {
        this.name = name;
        this.icon = icon;
        this.clickListener = clickListener;
    }

    public Shortcut(String name, Drawable icon, Intent intent) {
        this.name = name;
        this.icon = icon;
        this.intent = intent;
    }
}
