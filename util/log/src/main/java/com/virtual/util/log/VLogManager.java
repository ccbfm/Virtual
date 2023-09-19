package com.virtual.util.log;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class VLogManager {

    private VLogManager() {
    }

    private static final class Singleton {
        private static final VLogManager INSTANCE = new VLogManager();
    }

    public static VLogManager instance() {
        return VLogManager.Singleton.INSTANCE;
    }

    private final String mDefaultLog = "DefaultLog";
    private final HashMap<String, VLogConfig> mLogConfigMap = new HashMap<>();

    public void addVLogConfig(VLogConfig logConfig) {
        if (logConfig == null) {
            return;
        }
        mLogConfigMap.put(logConfig.getLogTag(), logConfig);
    }

    public VLogConfig getVLogConfig(String logTag) {
        return mLogConfigMap.get(logTag);
    }

    public void defaultConfig(@NonNull Context context, String tag, @VLogLevel int debugLevel, @VLogLevel int saveLevel) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debugLevel)
                .setSaveLevel(saveLevel);
        mLogConfigMap.put(mDefaultLog, builder.build());
    }

    public void createConfig(@NonNull Context context, String tag, @VLogLevel int debugLevel, @VLogLevel int saveLevel) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debugLevel)
                .setSaveLevel(saveLevel);
        addVLogConfig(builder.build());
    }

    public void defaultConfig(@NonNull Context context, String tag, boolean debug) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debug ? VLogLevel.D : VLogLevel.NONE)
                .setSaveLevel(VLogLevel.W);
        mLogConfigMap.put(mDefaultLog, builder.build());
    }

    public void defaultNotSaveConfig(@NonNull Context context, String tag, boolean debug) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debug ? VLogLevel.D : VLogLevel.NONE)
                .setSaveLevel(VLogLevel.NONE);
        mLogConfigMap.put(mDefaultLog, builder.build());
    }

    public VLogConfig getDefault() {
        VLogConfig logConfig = mLogConfigMap.get(mDefaultLog);
        if (logConfig == null) {
            throw new NullPointerException("Default is null.");
        }
        return logConfig;
    }
}
