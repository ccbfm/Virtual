package com.virtual.util.persist.file;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Android开发:filePath放在哪个文件夹
 * Environment.getDataDirectory() = /data
 * Environment.getDownloadCacheDirectory() = /cache
 * Environment.getExternalStorageDirectory() = /mnt/sdcard
 * Environment.getExternalStoragePublicDirectory(“test”) = /mnt/sdcard/test
 * Environment.getRootDirectory() = /system
 * getPackageCodePath() = /data/app/com.my.app-1.apk
 * getPackageResourcePath() = /data/app/com.my.app-1.apk
 * getCacheDir() = /data/data/com.my.app/cache
 * getFilesDir() = /data/data/com.my.app/files
 * getDatabasePath(“test”) = /data/data/com.my.app/databases/test
 * getDir(“test”, Context.MODE_PRIVATE) = /data/data/com.my.app/app_test
 * getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
 * getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
 * getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files
 */
public final class VFilePath {

    private VFilePath() {
        throw new UnsupportedOperationException("u can't instantiate me.");
    }

    public static File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    public static File getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    public static File getExternalStoragePublicDirectory(String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }

    public static File getRootDirectory() {
        return Environment.getRootDirectory();
    }

    public static String getPackageCodePath(Context context) {
        return context.getPackageCodePath();
    }

    public static String getPackageResourcePath(Context context) {
        return context.getPackageResourcePath();
    }

    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    public static File getDatabasePath(Context context, String name) {
        return context.getDatabasePath(name);
    }

    @IntDef(flag = true, value = {
            Context.MODE_PRIVATE,
            Context.MODE_APPEND,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FileMode {
    }

    public static File getDir(Context context, String name, @FileMode int mode) {
        return context.getDir(name, mode);
    }

    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    public static File getExternalFilesDir(Context context) {
        return getExternalFilesDir(context, null);
    }

    public static File getExternalFilesDir(Context context, @Nullable String type) {
        return context.getExternalFilesDir(type);
    }
}
