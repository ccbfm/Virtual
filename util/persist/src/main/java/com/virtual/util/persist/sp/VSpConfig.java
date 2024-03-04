package com.virtual.util.persist.sp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VSpConfig {

    private VSpConfig() {
    }

    private static final class Singleton {
        private static final VSpConfig INSTANCE = new VSpConfig();
    }

    public static VSpConfig instance() {
        return Singleton.INSTANCE;
    }


    private final HashMap<String, Builder> mSpMap = new HashMap<>();


    public Builder getSp(@NonNull Context context, String name) {
        Builder builder = mSpMap.get(name);
        if (builder == null) {
            builder = new Builder(context, name);
            mSpMap.put(name, builder);
        }
        return builder;
    }


    public static class Builder {
        String name;
        SharedPreferences sp;

        Builder(@NonNull Context context, String name) {
            this.name = name;
            this.sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        }

        public void putString(String key, @Nullable String value) {
            this.sp.edit().putString(key, value).apply();
        }

        public void putStringSet(String key, @Nullable Set<String> values) {
            this.sp.edit().putStringSet(key, values).apply();
        }

        public void putInt(String key, int value) {
            this.sp.edit().putInt(key, value).apply();
        }

        public void putLong(String key, long value) {
            this.sp.edit().putLong(key, value).apply();
        }

        public void putFloat(String key, float value) {
            this.sp.edit().putFloat(key, value).apply();
        }

        public void putBoolean(String key, boolean value) {
            this.sp.edit().putBoolean(key, value).apply();
        }

        public String getString(String key) {
            return this.getString(key, "");
        }

        @Nullable
        public String getString(String key, @Nullable String defValue) {
            return this.sp.getString(key, defValue);
        }

        public Set<String> getStringSet(String key) {
            return this.getStringSet(key, null);
        }

        @Nullable
        public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
            return this.sp.getStringSet(key, defValues);
        }

        public int getInt(String key) {
            return this.getInt(key, 0);
        }

        public int getInt(String key, int defValue) {
            return this.sp.getInt(key, defValue);
        }

        public long getLong(String key) {
            return this.getLong(key, 0L);
        }

        public long getLong(String key, long defValue) {
            return this.sp.getLong(key, defValue);
        }

        public float getFloat(String key) {
            return this.getFloat(key, 0f);
        }

        public float getFloat(String key, float defValue) {
            return this.sp.getFloat(key, defValue);
        }

        public boolean getBoolean(String key) {
            return this.getBoolean(key, false);
        }

        public boolean getBoolean(String key, boolean defValue) {
            return this.sp.getBoolean(key, defValue);
        }

        public void clear() {
            this.sp.edit().clear().apply();
        }

        public Map<String, ?> getAll() {
            return this.sp.getAll();
        }
    }
}
