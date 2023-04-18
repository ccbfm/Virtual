package com.virtual.util.persist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.virtual.util.context.VContextHolder;
import com.virtual.util.persist.provider.VPersistProvider;

public final class VPersistExported {


    public static void setValue(String key, String value) {
        setValue("", key, value);
    }

    public static void setValue(Context context, String key, String value) {
        setValue(context, "", key, value);
    }

    public static void setValue(String persistName, String key, String value) {
        setValue(VContextHolder.instance().getContext(), persistName, key, value);
    }

    public static void setValue(Context context, String persistName, String key, String value) {
        if (context == null) {
            Log.e("VPersistExported", "setValue-context is null.");
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(VPersistProvider.Config.KEY_PERSIST_NAME, persistName);
            values.put(VPersistProvider.Config.KEY_KEY, key);
            values.put(VPersistProvider.Config.KEY_VALUE, value);
            context.getContentResolver()
                    .insert(VPersistProvider.Config.CONTENT_URI, values);
        } catch (Throwable throwable) {
            Log.e("VPersistExported", "setValue-Throwable: " + throwable.getMessage());
        }
    }

    public static String getValue(String key) {
        return getValue("", key);
    }

    public static String getValue(Context context, String key) {
        return getValue(context, "", key);
    }

    public static String getValue(String persistName, String key) {
        return getValue(VContextHolder.instance().getContext(), persistName, key);
    }

    public static String getValue(Context context, String persistName, String key) {
        if (context == null) {
            Log.e("VPersistExported", "getValue-context is null.");
            return "";
        }
        try (Cursor cursor = context.getContentResolver()
                .query(VPersistProvider.Config.CONTENT_URI, null,
                        null, new String[]{persistName, key}, "ASC")) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getString(0);
            }
        } catch (Throwable th) {
            Log.e("VPersistExported", "getValue-Throwable=" + th.getMessage());
        }
        return "";
    }

}
