package com.virtual.util.log.flavor;

import android.util.Log;

import com.virtual.util.log.VLevel;
import com.virtual.util.log.VLogConfig;

public class VPrintLog extends VBaseLog {
    @VLevel
    protected final int mDebugLevel;

    public VPrintLog() {
        mDebugLevel = VLogConfig.instance().getDebugLevel();
    }

    protected boolean checkDebug(@VLevel int debugLevel) {
        return mDebugLevel <= debugLevel;
    }

    @Override
    public void d(String tag, String msg) {
        if (checkDebug(VLevel.D)) {
            Log.d(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void i(String tag, String msg) {
        if (checkDebug(VLevel.I)) {
            Log.i(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void w(String tag, String msg) {
        if (checkDebug(VLevel.W)) {
            Log.w(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg) {
        if (checkDebug(VLevel.E)) {
            Log.e(mLogTag, formatLog(tag, msg));
        }
    }

    @Override
    public void e(String tag, String msg, Throwable tr) {
        if (checkDebug(VLevel.E)) {
            Log.e(mLogTag, formatLog(tag, msg), tr);
        }
    }
}
