package com.virtual.util.log.flavor;

import androidx.annotation.NonNull;

import com.virtual.util.log.IVLog;
import com.virtual.util.log.VLogConfig;

public abstract class VBaseLog implements IVLog {

    protected final String mLogTag;

    public VBaseLog(@NonNull VLogConfig logConfig) {
        mLogTag = logConfig.getLogTag();
    }

    protected String formatLog(String tag, String msg) {
        return "[" + tag + "]" + msg;
    }

    protected String formatLog(String level, String tag, String msg) {
        return level + " [" + tag + "]" + msg;
    }
}
