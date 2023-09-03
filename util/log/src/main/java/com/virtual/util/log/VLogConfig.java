package com.virtual.util.log;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.log.flavor.VPrintLog;
import com.virtual.util.log.flavor.VSaveLog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public final class VLogConfig {

    private VLogConfig() {
    }

    private static final class Singleton {
        private static final VLogConfig INSTANCE = new VLogConfig();
    }

    public static VLogConfig instance() {
        return Singleton.INSTANCE;
    }

    public void defaultConfig(@NonNull Context context, String tag, boolean debug) {
        createBuilder(context)
                .setLogTag(tag)
                .setDebugLevel(debug ? VLogLevel.D : VLogLevel.NONE)
                .setSaveLevel(VLogLevel.W)
                .build();
    }

    public void defaultNotSaveConfig(@NonNull Context context, String tag, boolean debug) {
        createBuilder(context)
                .setLogTag(tag)
                .setDebugLevel(debug ? VLogLevel.D : VLogLevel.NONE)
                .setSaveLevel(VLogLevel.NONE)
                .build();
    }

    private String mLogTag = "Default";
    @VLogLevel
    private int mDebugLevel = VLogLevel.NONE;
    @VLogLevel
    private int mSaveLevel = VLogLevel.NONE;

    private String mSaveRootDir;

    private final List<IVLog> mILogs = new LinkedList<>();

    private Context mContext;

    private void setLogTag(String logTag) {
        mLogTag = logTag;
    }

    private void setDebugLevel(int debugLevel) {
        mDebugLevel = debugLevel;
    }

    private void setSaveLevel(int saveLevel) {
        mSaveLevel = saveLevel;
    }

    private void setSaveRootDir(String saveRootDir) {
        mSaveRootDir = saveRootDir;
    }

    private void addILog(IVLog iLog) {
        mILogs.add(iLog);
    }

    private void addAllILog(List<IVLog> iLogs) {
        mILogs.addAll(iLogs);
    }

    private void clearILogs() {
        mILogs.clear();
    }

    private void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public String getLogTag() {
        return mLogTag;
    }

    public int getDebugLevel() {
        return mDebugLevel;
    }

    public int getSaveLevel() {
        return mSaveLevel;
    }

    public String getSaveRootDir() {
        File file = new File(mSaveRootDir);
        if (!file.exists()) {
            Log.d("VLogConfig", "getSaveRootDir-mkdirs=" + file.mkdirs());
        }
        return mSaveRootDir;
    }

    public List<IVLog> getILogs() {
        return mILogs;
    }

    public Context getContext() {
        return mContext;
    }

    public Builder createBuilder(@NonNull Context context) {
        return new Builder(this, context);
    }

    public static final class Builder {
        private final VLogConfig logConfig;
        private final Context context;

        public Builder(VLogConfig logConfig, @NonNull Context context) {
            this.logConfig = logConfig;
            this.context = context;
        }

        private String logTag = "Default";
        @VLogLevel
        private int debugLevel = VLogLevel.NONE;
        private IVLog debugLog;
        @VLogLevel
        private int saveLevel = VLogLevel.NONE;
        private IVLog saveLog;
        private String saveRootDir;

        private List<IVLog> otherILogs;

        public Builder setLogTag(@NonNull String logTag) {
            this.logTag = logTag;
            return this;
        }

        public Builder setDebugLevel(@VLogLevel int debugLevel) {
            this.debugLevel = debugLevel;
            return this;
        }

        public Builder setDebugLog(IVLog debugLog) {
            this.debugLog = debugLog;
            return this;
        }

        public Builder setSaveLevel(@VLogLevel int saveLevel) {
            this.saveLevel = saveLevel;
            return this;
        }

        public Builder setSaveLog(IVLog saveLog) {
            this.saveLog = saveLog;
            return this;
        }

        public Builder setSaveRootDir(String saveRootDir) {
            this.saveRootDir = saveRootDir;
            return this;
        }

        public Builder setOtherILogs(List<IVLog> otherILogs) {
            this.otherILogs = otherILogs;
            return this;
        }

        public void build() {
            this.logConfig.clearILogs();
            this.logConfig.setLogTag(this.logTag);
            if (this.debugLevel < VLogLevel.NONE) {
                this.logConfig.setDebugLevel(this.debugLevel);
                IVLog debugLog = this.debugLog;
                if (debugLog == null) {
                    debugLog = new VPrintLog();
                }
                this.logConfig.addILog(debugLog);
            }
            if (this.saveLevel < VLogLevel.NONE) {
                this.logConfig.setSaveLevel(this.saveLevel);

                String saveRootDir = this.saveRootDir;
                if (TextUtils.isEmpty(saveRootDir)) {
                    saveRootDir = this.context.getExternalFilesDir("Log_" + this.logTag).getAbsolutePath();
                }
                if (TextUtils.isEmpty(saveRootDir)) {
                    throw new NullPointerException("Path saveRootDir is null.");
                }
                this.logConfig.setSaveRootDir(saveRootDir);

                IVLog saveLog = this.saveLog;
                if (saveLog == null) {
                    saveLog = new VSaveLog();
                }
                this.logConfig.addILog(saveLog);
            }
            if (this.otherILogs != null) {
                this.logConfig.addAllILog(this.otherILogs);
            }
            this.logConfig.setContext(this.context);
        }
    }

}
