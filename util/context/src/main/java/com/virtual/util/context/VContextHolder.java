package com.virtual.util.context;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.ref.WeakReference;

public final class VContextHolder {

    private VContextHolder() {
    }

    private static final class Singleton {
        private static final VContextHolder INSTANCE = new VContextHolder();
    }

    public static VContextHolder instance() {
        return Singleton.INSTANCE;
    }

    private WeakReference<Context> mContext;

    public Context getContext() {
        if (mContext == null || mContext.get() == null) {
            Log.d("UContextHolder", "getApplicationByReflect");
            Context context = getApplicationByReflect();
            if (context == null) {
                throw new NullPointerException("Context in UContextHolder is null.");
            }
            mContext = new WeakReference<>(context);
        }
        return mContext.get();
    }

    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    public void setContext(Context context) {
        mContext = new WeakReference<>(context);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
