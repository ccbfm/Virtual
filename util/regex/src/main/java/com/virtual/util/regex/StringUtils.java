package com.virtual.util.regex;

public class StringUtils {

    public static boolean isEmpty(String target){
        return ((target == null) || (target.trim().length() == 0));
    }
}
