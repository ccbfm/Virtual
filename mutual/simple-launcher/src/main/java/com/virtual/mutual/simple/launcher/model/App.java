package com.virtual.mutual.simple.launcher.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class App {
    public CharSequence name;
    public Drawable icon;
    public String packageName;
    public Intent intent;
    public boolean hasWorkplace;

    public App() {
    }

    public App(CharSequence name, Drawable icon, String packageName, Intent intent) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.intent = intent;
    }

    public App(App app) {
        this.name = app.name;
        this.icon = app.icon;
        this.packageName = app.packageName;
        this.intent = app.intent;
    }
}
