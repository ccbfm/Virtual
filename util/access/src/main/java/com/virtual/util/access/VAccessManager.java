package com.virtual.util.access;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;

public class VAccessManager {

    private static final class Singleton {
        private static final VAccessManager INSTANCE = new VAccessManager();
    }

    public static VAccessManager instance() {
        return Singleton.INSTANCE;
    }

    private VAccessManager() {
    }

    private final HashMap<Class<?>, VAutoHandler> mAutoHandlerMap = new HashMap<>();

    public void addVAutoHandler(VAutoHandler autoHandler) {
        if (autoHandler == null) {
            return;
        }
        mAutoHandlerMap.put(autoHandler.getClass(), autoHandler);
    }

    public VAutoHandler getVAutoHandler(Class<?> clazz) {
        return mAutoHandlerMap.get(clazz);
    }

    public void setAutoService(VAutoService autoService){

    }

    public void accessibilityEvent(CharSequence packageName, AccessibilityNodeInfo nodeInfo){

    }
}
