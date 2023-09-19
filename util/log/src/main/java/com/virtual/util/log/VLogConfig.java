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
import java.util.concurrent.TimeUnit;

public final class VLogConfig {

    private VLogConfig() {
    }

    private String mLogTag = "Default";
    @VLogLevel
    private int mDebugLevel = VLogLevel.NONE;
    @VLogLevel
    private int mSaveLevel = VLogLevel.NONE;

    private String mSaveRootDir;
    private long mRetainedTime;

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

    public void setRetainedTime(long retainedTime) {
        mRetainedTime = retainedTime;
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

    public long getRetainedTime() {
        return mRetainedTime;
    }

    public List<IVLog> getILogs() {
        return mILogs;
    }

    public Context getContext() {
        return mContext;
    }

    public static final class Builder {
        private final Context context;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        private String logTag;
        @VLogLevel
        private int debugLevel = VLogLevel.NONE;
        private IVLog debugLog;
        @VLogLevel
        private int saveLevel = VLogLevel.NONE;
        private IVLog saveLog;
        private String saveRootDir;
        private long retainedTime;

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

        public Builder setRetainedTime(int day) {
            this.retainedTime = TimeUnit.DAYS.toMillis(day);
            return this;
        }

        public Builder setOtherILogs(List<IVLog> otherILogs) {
            this.otherILogs = otherILogs;
            return this;
        }

        public VLogConfig build() {
            VLogConfig logConfig = new VLogConfig();
            logConfig.clearILogs();
            if (TextUtils.isEmpty(this.logTag)) {
                throw new NullPointerException("logTag is null.");
            }
            logConfig.setLogTag(this.logTag);
            logConfig.setContext(this.context);
            if (this.debugLevel < VLogLevel.NONE) {
                logConfig.setDebugLevel(this.debugLevel);
                IVLog debugLog = this.debugLog;
                if (debugLog == null) {
                    debugLog = new VPrintLog(logConfig);
                }
                logConfig.addILog(debugLog);
            }
            if (this.saveLevel < VLogLevel.NONE) {
                logConfig.setSaveLevel(this.saveLevel);

                String saveRootDir = this.saveRootDir;
                if (TextUtils.isEmpty(saveRootDir)) {
                    File file = this.context.getExternalFilesDir("Log_" + this.logTag);
                    if (file != null) {
                        try {
                            saveRootDir = file.getCanonicalPath();
                        } catch (Throwable throwable) {
                            Log.e("VLogConfig", "build", throwable);
                            saveRootDir = file.getAbsolutePath();
                        }
                    }
                }
                if (TextUtils.isEmpty(saveRootDir)) {
                    throw new NullPointerException("Path saveRootDir is null.");
                }
                logConfig.setSaveRootDir(saveRootDir);
                if (this.retainedTime == 0L) {
                    setRetainedTime(10);
                }
                logConfig.setRetainedTime(this.retainedTime);

                IVLog saveLog = this.saveLog;
                if (saveLog == null) {
                    saveLog = new VSaveLog(logConfig);
                }
                logConfig.addILog(saveLog);
            }
            if (this.otherILogs != null) {
                logConfig.addAllILog(this.otherILogs);
            }
            return logConfig;
        }
    }

}
