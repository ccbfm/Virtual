package com.virtual.util.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class PackageUtils {

    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    public static String getVersionName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
            return packInfo.versionName;
        } catch (Throwable e) {
            Log.e("PackageUtils", "getVersionName Throwable ", e);
        }
        return "";
    }

    public static long getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    public static long getVersionCode(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return packInfo.getLongVersionCode();
            } else {
                return packInfo.versionCode;
            }
        } catch (Throwable e) {
            Log.e("PackageUtils", "getVersionCode Throwable ", e);
        }
        return 0L;
    }
}
