package com.virtual.util.common;

public class ParseUtils {

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }
    public static int parseInt(String s, int defaultInt) {
        try {
            return Integer.parseInt(s);
        } catch (Throwable ignore) {
        }
        return defaultInt;
    }

    public static long parseLong(String s) {
        return parseLong(s, 0);
    }
    public static long parseLong(String s, long defaultLong) {
        try {
            return Long.parseLong(s);
        } catch (Throwable ignore) {
        }
        return defaultLong;
    }
}
