package com.virtual.util.log.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.virtual.util.log.VLevel;
import com.virtual.util.log.VLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class VLogProvider extends ContentProvider {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Config {
        String AUTHORITY = "com.virtual.util.log.provider.VLogProvider";
        String PATH_V_LOG = "v_log";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_V_LOG);

        String KEY_LEVEL = "key_level";
        String KEY_TAG = "key_tag";
        String KEY_MESSAGE = "key_message";
    }

    @Override
    public boolean onCreate() {
        Log.d("VLogProvider", "onCreate");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
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
        if (Config.PATH_V_LOG.equals(uri.getPath().replaceAll("/", ""))) {
            if (values != null) {
                Integer level = values.getAsInteger(Config.KEY_LEVEL);
                String tag = values.getAsString(Config.KEY_TAG);
                String message = values.getAsString(Config.KEY_MESSAGE);

                if (level != null) {
                    switch (level) {
                        case VLevel.D:
                            VLog.d(tag, message);
                            break;
                        case VLevel.I:
                            VLog.i(tag, message);
                            break;
                        case VLevel.W:
                            VLog.w(tag, message);
                            break;
                        case VLevel.E:
                            VLog.e(tag, message);
                            break;
                    }
                }
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
