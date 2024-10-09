package com.virtual.util.common;

import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimpleDateUtils {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Pattern {
        String yyyyMMdd = "yyyyMMdd";
        String yyyyMMdd2 = "yyyy-MM-dd";
        String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    }

    public static String getCurrentDateStr() {
        return getCurrentDateStr(Pattern.yyyyMMdd);
    }

    public static String getCurrentDateStr(String pattern) {
        return getCurrentDateStr(pattern, Locale.getDefault());
    }

    public static String getCurrentDateStr(String pattern, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        Calendar calendar = Calendar.getInstance();
        return sdf.format(calendar.getTime());
    }

    public static String getDateStr(String pattern, long time) {
        return getDateStr(pattern, time, Locale.getDefault());
    }

    public static String getDateStr(String pattern, long time, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.format(new Date(time));
    }

    public static long getDateLongTh(String pattern, String dateStr, Locale locale) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        Date date = sdf.parse(dateStr);
        return date != null ? date.getTime() : 0L;
    }

    public static long getDateLongTh(String pattern, String dateStr) throws ParseException {
        return getDateLongTh(pattern, dateStr, Locale.getDefault());
    }

    public static long getDateLong(String pattern, String dateStr, Locale locale) {
        try {
            return getDateLongTh(pattern, dateStr, locale);
        } catch (Throwable throwable) {
            Log.e("SimpleDateUtils", "getDateLong Throwable ", throwable);
        }
        return 0L;
    }

    public static long getDateLong(String pattern, String dateStr) {
        return getDateLong(pattern, dateStr, Locale.getDefault());
    }

}
