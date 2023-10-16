package com.virtual.util.access;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class VAccessManager {
    private static final String TAG = "VAccessManager";

    private static final class Singleton {
        private static final VAccessManager INSTANCE = new VAccessManager();
    }

    public static VAccessManager instance() {
        return Singleton.INSTANCE;
    }

    private VAccessManager() {
    }

    private VToastCallback mVToastCallback;

    public void setVToastCallback(VToastCallback toastCallback) {
        mVToastCallback = toastCallback;
    }

    private final HashMap<Class<?>, VAutoHandler> mAutoHandlerMap = new HashMap<>();
    private VAutoHandler mCurrentAutoHandler;

    public void addVAutoHandler(VAutoHandler autoHandler) {
        if (autoHandler == null) {
            return;
        }
        mAutoHandlerMap.put(autoHandler.getClass(), autoHandler);
    }

    public VAutoHandler getVAutoHandler(Class<?> clazz) {
        return mAutoHandlerMap.get(clazz);
    }

    public Pair<Boolean, String> start(Class<?> clazz) {
        if (hasAutoService()) {
            VAutoHandler autoHandler = getVAutoHandler(clazz);
            if (mCurrentAutoHandler != null) {
                if (mCurrentAutoHandler == autoHandler) {
                    return new Pair<>(false, "已经在执行");
                } else {
                    mCurrentAutoHandler.stop();
                }
            }
            if (autoHandler != null) {
                mCurrentAutoHandler = autoHandler;
                return autoHandler.start();
            }
        }
        return new Pair<>(false, "服务未开启");
    }

    public void stop(Class<?> clazz) {
        VAutoHandler autoHandler = getVAutoHandler(clazz);
        if (autoHandler != null) {
            autoHandler.stop();
        }
    }

    private VAutoService mVAutoService;
    private WeakReference<Context> mContext;
    private CharSequence mPackageName;

    public Context context() {
        return mContext != null ? mContext.get() : null;
    }

    public String getPackageName() {
        return mPackageName != null ? mPackageName.toString() : null;
    }

    public boolean hasAutoService() {
        return mVAutoService != null;
    }

    public AccessibilityNodeInfo rootNodeInfo() {
        return mVAutoService.getRootInActiveWindow();
    }

    public void setAutoService(VAutoService autoService) {
        if (autoService != null) {
            mVAutoService = autoService;
            Context context = autoService.getApplicationContext();
            mContext = new WeakReference<>(context);
        } else {
            mVAutoService = null;
            mContext = null;
        }
    }

    public void accessibilityEvent(CharSequence packageName, AccessibilityNodeInfo nodeInfo) {
        mPackageName = packageName;
        CharSequence className = nodeInfo.getClassName();
        if (className == null) {
            return;
        }
        String viewIdStr = nodeInfo.getViewIdResourceName();
        for (VAutoHandler autoHandler : mAutoHandlerMap.values()) {
            autoHandler.checkHandleEvent(packageName, className, viewIdStr, nodeInfo);
        }
    }

    public void startOpenService(Activity activity) {
        if (hasAutoService()) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        activity.startActivity(intent);
    }

    public void toast(String msg) {
        if (mVToastCallback != null) {
            mVToastCallback.toast(msg);
        } else {
            Log.i(TAG, "toast msg " + msg);
        }
    }

    public void back() {
        if (hasAutoService()) {
            mVAutoService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }

    public void clickByNode(AccessibilityNodeInfo nodeInfo) {
        if (hasAutoService()) {
            boolean result = VAccessNodeUtils.clickByNode(mVAutoService, nodeInfo);
            Log.i(TAG, "clickByNode result " + result + " " + nodeInfo);
        }
    }

    public void scrollVerticalByNode(AccessibilityNodeInfo nodeInfo, boolean up) {
        if (hasAutoService()) {
            boolean result = VAccessNodeUtils.scrollVerticalByNode(mVAutoService, nodeInfo, up);
            Log.i(TAG, "scrollVerticalByNode result " + result + " " + nodeInfo);
        }
    }
}
