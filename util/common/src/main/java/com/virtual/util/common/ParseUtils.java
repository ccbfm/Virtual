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

    public static double parseDouble(String s) {
        return parseDouble(s, 0D);
    }

    public static double parseDouble(String s, double defaultDouble) {
        try {
            return Double.parseDouble(s);
        } catch (Throwable ignore) {
        }
        return defaultDouble;
    }
}
