package com.virtual.util.log.flavor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.virtual.util.log.VLogLevel;
import com.virtual.util.log.VLogConfig;

public class VPrintLog extends VBaseLog {
    @VLogLevel
    protected final int mDebugLevel;

    public VPrintLog(@NonNull VLogConfig logConfig) {
        super(logConfig);
        mDebugLevel = logConfig.getDebugLevel();
    }

    protected boolean checkDebug(@VLogLevel int debugLevel) {
        return mDebugLevel <= debugLevel;
    }

    @Override
    public void d(String tag, String msg) {
        if (checkDebug(VLogLevel.D)) {
            Log.d(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (checkDebug(VLogLevel.I)) {
            Log.i(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (checkDebug(VLogLevel.W)) {
            Log.w(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (checkDebug(VLogLevel.E)) {
            Log.e(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (checkDebug(VLogLevel.E)) {
            Log.e(mLogTag, formatLog(tag, msg), tr);
        }
    }
}
