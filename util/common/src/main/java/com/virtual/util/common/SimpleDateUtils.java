package com.virtual.util.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimpleDateUtils {

    @Retention(RetentionPolicy.SOURCE)
    public @interface Pattern {
        String yyyyMMdd = "yyyyMMdd";
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
}
