package com.virtual.util.log;

import android.content.ContentValues;
import android.util.Log;

import com.virtual.util.context.VContextHolder;
import com.virtual.util.log.provider.VLogProvider;

/**
 * 跨应用输出log记录在本应用目录中
 */
public final class VLogExported {

    public static void d(String tag, String msg) {
        exportedProvider(VLevel.D, tag, msg);
    }

    public static void i(String tag, String msg) {
        exportedProvider(VLevel.I, tag, msg);
    }

    public static void w(String tag, String msg) {
        exportedProvider(VLevel.W, tag, msg);
    }

    public static void e(String tag, String msg) {
        exportedProvider(VLevel.E, tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        exportedProvider(VLevel.E, tag, msg + " Throwable: " + Log.getStackTraceString(tr));
    }

    private static void exportedProvider(@VLevel int level, String tag, String msg) {
        try {
            ContentValues values = new ContentValues();
            values.put(VLogProvider.Config.KEY_LEVEL, level);
            values.put(VLogProvider.Config.KEY_TAG, tag);
            values.put(VLogProvider.Config.KEY_MESSAGE, msg);
            VContextHolder.instance().getContext().getContentResolver()
                    .insert(VLogProvider.Config.CONTENT_URI, values);
        } catch (Throwable throwable) {
            Log.e("VLogExported", "exportedProvider-Throwable: " + throwable.getMessage());
        }
    }
}
