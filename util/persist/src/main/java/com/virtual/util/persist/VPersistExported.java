package com.virtual.util.persist;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.DeadObjectException;
import android.util.Log;

import com.virtual.util.persist.provider.VPersistProvider;

import java.lang.ref.WeakReference;

public final class VPersistExported {

    private static Uri sProviderUri;
    private static WeakReference<Context> sContext;

    public static void init(Context context, String packageName) {
        init(context, providerUri(packageName));
    }

    public static void init(Context context, Uri uri) {
        sContext = new WeakReference<>(context);
        sProviderUri = uri;
    }

    private static Uri checkProvider(Context context) {
        if (sProviderUri == null) {
            sProviderUri = providerUri(context.getPackageName());
        }
        return sProviderUri;
    }

    private static Context context() {
        if (sContext == null || sContext.get() == null) {
            throw new NullPointerException("context is null.");
        }
        return sContext.get();
    }

    public static void setValue(String key, String value) {
        setValue("", key, value);
    }

    public static void setValue(Context context, String key, String value) {
        setValue(context, "", key, value);
    }

    public static void setValue(String persistName, String key, String value) {
        Context context = context();
        setValue(context, persistName, key, value);
    }

    public static void setValue(Context context, String persistName, String key, String value) {
        if (context == null) {
            Log.e("VPersistExported", "setValue-context is null.");
            return;
        }
        Uri uri = checkProvider(context);

        try (ContentProviderClient client = context.getContentResolver()
                .acquireUnstableContentProviderClient(uri)) {
            if (client != null) {
                ContentValues values = new ContentValues();
                values.put(VPersistProvider.Config.KEY_PERSIST_NAME, persistName);
                values.put(VPersistProvider.Config.KEY_KEY, key);
                values.put(VPersistProvider.Config.KEY_VALUE, value);
                client.insert(sProviderUri, values);
            }
        } catch (DeadObjectException exception) {
            Log.e("VPersistExported", "setValue DeadObjectException.");
        } catch (Throwable throwable) {
            Log.e("VPersistExported", "setValue Throwable: " + throwable.getMessage());
        }
    }

    public static String getValue(String key) {
        return getValue("", key);
    }

    public static String getValue(Context context, String key) {
        return getValue(context, "", key);
    }

    public static String getValue(String persistName, String key) {
        Context context = context();
        return getValue(context, persistName, key);
    }

    public static String getValue(Context context, String persistName, String key) {
        if (context == null) {
            Log.e("VPersistExported", "getValue-context is null.");
            return "";
        }

        Uri uri = checkProvider(context);

        try (ContentProviderClient client = context.getContentResolver()
                .acquireUnstableContentProviderClient(uri)) {
            if (client != null) {
                try (Cursor cursor = client.query(sProviderUri, null,
                        null, new String[]{persistName, key}, "ASC")) {
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        return cursor.getString(0);
                    }
                }
            }
        } catch (DeadObjectException exception) {
            Log.e("VPersistExported", "getValue DeadObjectException.");
        } catch (Throwable throwable) {
            Log.e("VPersistExported", "getValue Throwable: " + throwable.getMessage());
        }
        return "";
    }

    private static Uri providerUri(String packageName) {
        String authority = packageName + ".provider.VPersistProvider";
        return Uri.parse("content://" + authority + "/" + VPersistProvider.Config.PATH_V_PERSIST);
    }
}
