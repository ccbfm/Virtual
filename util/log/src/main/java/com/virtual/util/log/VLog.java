package com.virtual.util.log;

import java.util.List;

public final class VLog {

    public static void d(String tag, String msg) {
        List<IVLog> logs = VLogManager.instance().getDefault().getILogs();
        for (IVLog log : logs) {
            log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        List<IVLog> logs = VLogManager.instance().getDefault().getILogs();
        for (IVLog log : logs) {
            log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        List<IVLog> logs = VLogManager.instance().getDefault().getILogs();
        for (IVLog log : logs) {
            log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        List<IVLog> logs = VLogManager.instance().getDefault().getILogs();
        for (IVLog log : logs) {
            log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        List<IVLog> logs = VLogManager.instance().getDefault().getILogs();
        for (IVLog log : logs) {
            log.e(tag, msg, tr);
        }
    }

    public static void saveD(String tag, String msg) {
        IVLog log = VLogManager.instance().getDefault().getSaveIVLog();
        if (log != null) {
            log.d(tag, msg);
        }
    }

    public static void saveI(String tag, String msg) {
        IVLog log = VLogManager.instance().getDefault().getSaveIVLog();
        if (log != null) {
            log.i(tag, msg);
        }
    }

    public static void saveW(String tag, String msg) {
        IVLog log = VLogManager.instance().getDefault().getSaveIVLog();
        if (log != null) {
            log.w(tag, msg);
        }
    }

    public static void saveE(String tag, String msg) {
        IVLog log = VLogManager.instance().getDefault().getSaveIVLog();
        if (log != null) {
            log.e(tag, msg);
        }
    }

    public static void saveE(String tag, String msg, Throwable tr) {
        IVLog log = VLogManager.instance().getDefault().getSaveIVLog();
        if (log != null) {
            log.e(tag, msg, tr);
        }
    }
}
