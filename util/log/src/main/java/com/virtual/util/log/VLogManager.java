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

    /**
     * 添加对应tag logConfig
     * 需要自己写 VLog
     *
     * @param tag tag
     */
    public void createConfig(@NonNull Context context, String tag, @VLogLevel int debugLevel, @VLogLevel int saveLevel) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debugLevel)
                .setSaveLevel(saveLevel);
        addVLogConfig(builder.build());
    }

    /**
     * 默认tag logConfig
     * 直接使用 VLog
     */
    public void defaultConfig(@NonNull Context context, String tag, @VLogLevel int debugLevel, @VLogLevel int saveLevel) {
        VLogConfig.Builder builder = new VLogConfig.Builder(context);
        builder.setLogTag(tag)
                .setDebugLevel(debugLevel)
                .setSaveLevel(saveLevel);
        mLogConfigMap.put(mDefaultLog, builder.build());
    }

    public void defaultConfig(@NonNull Context context, String tag, boolean debug) {
        defaultConfig(context, tag, (debug ? VLogLevel.D : VLogLevel.NONE), VLogLevel.W);
    }

    public void defaultNotSaveConfig(@NonNull Context context, String tag, boolean debug) {
        defaultConfig(context, tag, (debug ? VLogLevel.D : VLogLevel.NONE), VLogLevel.NONE);
    }

    public VLogConfig getDefault() {
        VLogConfig logConfig = mLogConfigMap.get(mDefaultLog);
        if (logConfig == null) {
            throw new NullPointerException("Default is null.");
        }
        return logConfig;
    }
}
