package com.virtual.util.log;

public interface IVLog {

    void d(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);

    void e(String tag, String msg, Throwable tr);
}
