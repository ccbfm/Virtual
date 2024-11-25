package com.virtual.util.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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
            PackageInfo packInfo = getVersionInfo(context, packageName);
            if (packInfo != null) {
                return packInfo.versionName;
            }
        } catch (Throwable throwable) {
            Log.e("PackageUtils", "getVersionName Throwable ", throwable);
        }
        return "";
    }

    public static long getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    public static long getVersionCode(Context context, String packageName) {
        try {
            PackageInfo packInfo = getVersionInfo(context, packageName);
            if (packInfo != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    return packInfo.getLongVersionCode();
                } else {
                    return packInfo.versionCode;
                }
            }
        } catch (Throwable throwable) {
            Log.e("PackageUtils", "getVersionCode Throwable ", throwable);
        }
        return 0L;
    }

    public static PackageInfo getVersionInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getPackageInfo(packageName, 0);
        } catch (Throwable throwable) {
            Log.e("PackageUtils", "getVersionInfo Throwable ", throwable);
        }
        return null;
    }

    public static ApplicationInfo getApplicationInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationInfo(packageName, 0);
        } catch (Throwable throwable) {
            Log.e("PackageUtils", "getApplicationInfo Throwable ", throwable);
        }
        return null;
    }

    public static String getAppSourceDir(Context context, String packageName) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, packageName);
        if (applicationInfo != null) {
            return applicationInfo.sourceDir;
        }
        return null;
    }
}
