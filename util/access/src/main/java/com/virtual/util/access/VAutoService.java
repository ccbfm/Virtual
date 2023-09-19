package com.virtual.util.access;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

public class VAutoService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            VAccessManager.instance().setAutoService(this);
        } catch (Throwable throwable) {
            Log.e("VAutoService", "onCreate Throwable ", throwable);
        }
    }

    @Override
    public boolean onGesture(@NonNull AccessibilityGestureEvent gestureEvent) {
        return super.onGesture(gestureEvent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event == null) {
                return;
            }
            AccessibilityNodeInfo nodeInfo = event.getSource();
            if (nodeInfo == null) {
                return;
            }
            VAccessManager afManager = VAccessManager.instance();
            CharSequence packageName = event.getPackageName();
            afManager.accessibilityEvent(packageName, nodeInfo);
        } catch (Throwable throwable) {
            Log.e("AutoService", "onAccessibilityEvent Throwable ", throwable);
        }
    }

    @Override
    public void onInterrupt() {
        VAccessManager.instance().setAutoService(null);
    }
}
