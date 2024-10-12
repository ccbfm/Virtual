package com.virtual.util.common.box;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

public class ContextBox {

    private WeakReference<Context> mContext;

    public void setContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    public Context getContext() {
        Context context = mContext != null ? mContext.get() : null;
        if (context == null) {
            context = getApplicationByReflect();
            setContext(context);
        }
        return context;
    }

    @SuppressLint("PrivateApi")
    public Application getApplicationByReflect() {
        try {
            Application application = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication")
                    .invoke(null, (Object[]) null);
            if (application != null) {
                return application;
            }
        } catch (Throwable throwable) {
            Log.e("ContextBox", "getApplicationByReflect Throwable ", throwable);
        }
        return null;
    }
}
