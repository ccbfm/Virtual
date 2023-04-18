package com.virtual.util.persist.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.util.persist.VPersist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class VPersistProvider extends ContentProvider {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Config {
        String AUTHORITY = "com.virtual.util.persist.provider.VPersistProvider";
        String PATH_V_PERSIST = "v_persist";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_V_PERSIST);

        String KEY_PERSIST_NAME = "key_persist_name";
        String KEY_KEY = "key_key";
        String KEY_VALUE = "key_value";
    }

    @Override
    public boolean onCreate() {
        Log.d("VPersistProvider", "onCreate");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (Config.PATH_V_PERSIST.equals(uri.getPath().replaceAll("/", ""))) {
            if (selectionArgs != null && selectionArgs.length == 2) {
                try (MatrixCursor cursor = new MatrixCursor(new String[]{Config.KEY_VALUE})) {
                    String persistName = selectionArgs[0];
                    String key = selectionArgs[1];
                    String value = VPersist.getValue(persistName, key);
                    cursor.addRow(new Object[]{value});
                    return cursor;
                } catch (Throwable throwable) {
                    Log.e("VPersistProvider", "query-Throwable=" + throwable.getMessage());
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (Config.PATH_V_PERSIST.equals(uri.getPath().replaceAll("/", ""))) {
            if (values != null) {
                String persistName = values.getAsString(Config.KEY_PERSIST_NAME);
                String key = values.getAsString(Config.KEY_KEY);
                String value = values.getAsString(Config.KEY_VALUE);
                VPersist.setValue(persistName, key, value);
            }
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
